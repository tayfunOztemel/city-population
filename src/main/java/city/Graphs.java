package city;

import akka.NotUsed;
import akka.japi.Pair;
import akka.japi.function.Function;
import akka.stream.*;
import akka.stream.javadsl.*;
import city.Citizen.Drop;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static city.Citizen.Requeue;

public class Graphs {

    private static Graph<SinkShape<Citizen>, ?> sinkToLogAndIgnore = Flow.of(Citizen.class)
            .filter(c -> c instanceof Drop)
            .map(c -> c.rawMessage)
            .to(Sink.foreach(Graphs::logAndDrop));

    private static Graph<SinkShape<Citizen>, ?> sinkToKafka = Flow.of(Citizen.class)
            .filter(c -> c instanceof Requeue)
            .map(c -> c.rawMessage)
            .to(Sink.foreach(Graphs::requeue));

    private static final Flow<Citizen, Citizen, NotUsed> combinedSinkFlow = Flow.of(Citizen.class)
            .alsoTo(sinkToKafka)
            .alsoTo(sinkToLogAndIgnore)
            .filterNot(Graphs::dropOrRequeue);

    public final static Sink<String, NotUsed> birthFlow = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .map(toBeDropped_IfBorn())
            .map(toBeDropped_IfDied())
            .alsoTo(sinkToLogAndIgnore)
            .filterNot(Graphs::dropOrRequeue)
            .to(Sink.foreach(Birth::sink));

    public final static Sink<String, NotUsed> deathFlow = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeDropped_IfDied())
            .via(combinedSinkFlow)
            .to(Sink.foreach(Death::sink));

    public final static Sink<String, NotUsed> educationFlow = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeDropped_IfDied())
            .map(toBeDropped_IfEducated())
            .via(combinedSinkFlow)
            .to(Sink.foreach(Education::sink));

    public final static Sink<String, NotUsed> adulthoodSink = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeDropped_IfDied())
            .map(toBeDropped_IfAdult())
            .via(combinedSinkFlow)
            .to(Sink.foreach(Adulthood::sink));

    // Parallel Processing
    private static final Sink<Partnership, NotUsed> sinkToPartnership = Flow.of(Partnership.class)
            .filterNot(p -> dropOrRequeue(p.c1) || dropOrRequeue(p.c2))
            .to(Sink.foreach(Partner::sink));


    private static final Sink<Partnership, NotUsed> sinkToLog = Flow.of(Partnership.class)
            .filter(p -> (p.c1 instanceof Drop) || (p.c2 instanceof Drop))
            .map(p -> p.rawMessage)
            .to(Sink.foreach(Graphs::logAndDrop));

    private static final Sink<Partnership, NotUsed> sinkToRequeu = Flow.of(Partnership.class)
            .filter(p -> (p.c1 instanceof Requeue) || (p.c2 instanceof Requeue))
            .map(p -> p.rawMessage)
            .to(Sink.foreach(Graphs::requeue));

    static List<Sink<Partnership, NotUsed>> list = Arrays.asList(sinkToRequeu, sinkToLog, sinkToPartnership);

    private static Sink partnerCombinedSink =
            Sink.fromGraph(GraphDSL.create(
                    list, (GraphDSL.Builder<List<NotUsed>> builder, List<SinkShape<Partnership>> outs) -> {

                        final UniformFanOutShape<Partnership, Partnership> bcast = builder.add(Broadcast.create(outs.size()));

                        for (SinkShape<Partnership> out : outs) {
                            builder.from(bcast).to(out);
                        }

                        return SinkShape.of(bcast.in());
                    }));

    public final static Sink<String, Partnership> partnerSinkGraph =
            Sink.fromGraph(GraphDSL.create(
                    partnerCombinedSink, (GraphDSL.Builder<NotUsed> builder, SinkShape<Partnership> out) -> {

                        final UniformFanOutShape<String, Citizen> unzipShape = builder.add(unzipGraph());
                        final UniformFanInShape<Citizen, Partnership> zipShape = builder.add(zipGraph());
                        final FlowShape<String, String> inlet = builder.add(Flow.of(String.class).map(s -> s));

                        final FlowShape<Citizen, Citizen> adultValidationFlow = builder.add(Flow.of(Citizen.class)
                                .map(toBeRequeued_IfUnbornYet())
                                .map(toBeDropped_IfDied())
                                .map(toBeRequeued_IfNotAdultYet()));
                        final FlowShape<Citizen, Citizen> adultValidationFlow2 = builder.add(Flow.of(Citizen.class)
                                .map(toBeRequeued_IfUnbornYet())
                                .map(toBeDropped_IfDied())
                                .map(toBeRequeued_IfNotAdultYet()));

                        builder.from(inlet)
                                .viaFanOut(unzipShape)
                                .via(adultValidationFlow)
                                .viaFanIn(zipShape)
                                .to(out);

                        builder.from(unzipShape).via(adultValidationFlow2).toFanIn(zipShape);

                        return SinkShape.of(inlet.in());
                    }));


    public final static Sink<String, NotUsed> logged = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeDropped_IfDied())
            .to(sinkToLogAndIgnore);

    private static void requeue(String rawMessage) {
        System.out.println("Requeue:" + rawMessage);
    }

    private static void logAndDrop(String rawMessage) {
        System.out.println("Dropped: " + rawMessage);

    }

    // Decoration utilities
    private static Function<Citizen, Citizen> toBeDropped_IfDied() {
        return c -> Death.filter(c) ? new Drop(c) : c;
    }

    private static Function<Citizen, Citizen> toBeDropped_IfBorn() {
        return c -> Birth.filter(c) ? new Drop(c) : c;
    }

    private static Function<Citizen, Citizen> toBeDropped_IfAdult() {
        return c -> Adulthood.filter(c) ? new Drop(c) : c;
    }

    private static Function<Citizen, Citizen> toBeDropped_IfEducated() {
        return c -> Education.filter(c) ? new Drop(c) : c;
    }

    private static Function<Citizen, Citizen> toBeRequeued_IfUnbornYet() {
        return c -> Birth.filter(c) ? c : new Requeue(c);
    }

    private static Function<Citizen, Citizen> toBeRequeued_IfNotAdultYet() {
        return c -> Adulthood.filter(c) ? c : new Requeue(c);
    }

    private static boolean dropOrRequeue(Citizen citizen) {
        return (citizen instanceof Drop) || (citizen instanceof Requeue);
    }

    static class Birth {
        private final static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        static Citizen sink(Citizen c) {
            set.add(c.name);
            System.out.println("SINK:"+c.rawMessage);
            return c;
        }

        static boolean filter(Citizen citizen) {
            return set.contains(citizen.name);
        }
    }

    static class Adulthood {
        private final static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        static Citizen sink(Citizen c) {
            set.add(c.name);
            System.out.println("SINK:"+c.rawMessage);
            return c;
        }

        static boolean filter(Citizen citizen) {
            return set.contains(citizen.name);
        }

    }

    static class Partner {
        private final static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        static Partnership sink(Partnership partnership) {
            set.add(partnership.rawMessage);
            System.out.println("SINK:"+partnership.rawMessage);

            return partnership;
        }

        public static boolean filter(Partnership partnership) {
            return set.contains(partnership.rawMessage);
        }
    }

    static class Children {
        private final static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        static Citizen sink(Citizen citizen) {
            set.add(citizen.rawMessage);
            return citizen;
        }

        public static boolean filter(Citizen citizen) {
            return true;
        }
    }

    static class Education {
        private final static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        static Citizen sink(Citizen c) {
            set.add(c.name);
            System.out.println("SINK:"+c.rawMessage);
            return c;
        }

        static boolean filter(Citizen c) {
            return set.contains(c.name);
        }
    }

    static class Death {
        private static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        static boolean filter(Citizen citizen) {
            return set.contains(citizen.name);
        }

        static void sink(Citizen citizen) {
            set.add(citizen.name);
            System.out.println("SINK:"+citizen.rawMessage);
        }
    }

    // Graph factories

    private static Graph<UniformFanOutShape<String, Citizen>, NotUsed> unzipGraph() {

        return GraphDSL.create(builder -> {
            final FanOutShape2<String, Citizen, Citizen> unzipShape = builder.add(UnzipWith.create(message -> {
                final Partnership partnership = new Partnership(message);
                return Pair.create(partnership.c1, partnership.c2);
            }));

            final Inlet<String> inlet = unzipShape.in();
            final Outlet[] outlets = {unzipShape.out0(), unzipShape.out1()};
            return new UniformFanOutShape<String, Citizen>(inlet, outlets);
        });
    }

    private static Graph<UniformFanInShape<Citizen, Partnership>, NotUsed> zipGraph() {

        return GraphDSL.create(builder -> {
            final FanInShape2<Citizen, Citizen, Partnership> zipShape = builder.add(ZipWith.create(Partnership::of));

            final Inlet[] inlets = {zipShape.in0(), zipShape.in1()};
            final Outlet<Partnership> outlet = zipShape.out();
            return new UniformFanInShape<Citizen, Partnership>(outlet, inlets);
        });
    }

}
