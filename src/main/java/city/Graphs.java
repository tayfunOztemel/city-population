package city;

import akka.NotUsed;
import akka.japi.function.Function;
import akka.stream.Graph;
import akka.stream.SinkShape;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import city.Citizen.Drop;
import city.Citizen.Partnership;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static city.Citizen.Requeue;

public class Graphs {

    private static Graph<SinkShape<Citizen>, ?> sinkToLogAndIgnore = Flow.of(Citizen.class)
            .filter(c -> c instanceof Drop)
            .to(Sink.foreach(Graphs::logAndDrop));

    private static Graph<SinkShape<Citizen>, ?> sinkToKafka = Flow.of(Citizen.class)
            .filter(c -> c instanceof Requeue)
            .to(Sink.foreach(Graphs::requeue));

    public final static Sink<Citizen, NotUsed> birth = Flow.of(Citizen.class)
            .map(toBeDropped_IfBorn())
            .map(toBeDropped_IfDied())
            .alsoTo(sinkToLogAndIgnore)
            .filterNot(c -> dropOrRequeue(c))
            .to(Sink.foreach(Birth::sink));

    public final static Sink<Citizen, NotUsed> death = Flow.of(Citizen.class)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeDropped_IfDied())
            .alsoTo(sinkToLogAndIgnore)
            .alsoTo(sinkToKafka)
            .filterNot(c -> dropOrRequeue(c))
            .to(Sink.foreach(Death::sink));

    public final static Sink<Citizen, NotUsed> education = Flow.of(Citizen.class)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeDropped_IfDied())
            .map(toBeDropped_IfEducated())
            .alsoTo(sinkToLogAndIgnore)
            .alsoTo(sinkToKafka)
            .filterNot(c -> dropOrRequeue(c))
            .to(Sink.foreach(Education::sink));

    public final static Sink<Citizen, NotUsed> adulthood = Flow.of(Citizen.class)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeDropped_IfDied())
            .map(toBeDropped_IfAdult())
            .alsoTo(sinkToLogAndIgnore)
            .alsoTo(sinkToKafka)
            .filterNot(c -> dropOrRequeue(c))
            .to(Sink.foreach(Adulthood::sink));


    // Parallel Processing
//    public final static RunnableGraph<CompletionStage<String>> partnerGraph = RunnableGraph.fromGraph(
//            GraphDSL.create(sinkToKafka, (builder, out) -> {
//
//                final FanOutShape2<String, Citizen, Citizen> unzip = builder.add(UnzipWith.<String, Citizen, Citizen>create(message -> {
//                    final Partnership partnership = new Partnership(message);
//                    return Pair.create(partnership.c1, partnership.c2);
//                }));
//
//                final FanInShape2<Citizen, Citizen, Partnership> zip = builder.add(ZipWith.create(Partnership::of));
//
//                builder.from(builder.add(Source.single("Partner-1-2")))
//                        .via(builder.add(Flow.of(String.class).map(s -> s)))
//                        .viaFanOut(unzip)
//                        .via(builder.add(adulthood))
//                        .viaFanIn(zip)
//                        .to(out);
//
////                builder.from(unzip).via(adulthood).toFanIn(zip);
//
//                return ClosedShape.getInstance();
//            }));


    private static boolean dropOrRequeue(Citizen citizen) {
        return (citizen instanceof Drop) || (citizen instanceof Requeue);
    }

    public final static Sink<Citizen, NotUsed> partner = Flow.of(Citizen.class)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeDropped_IfDied())
            .map(toBeRequeued_IfNotAdultYet())
            .alsoTo(sinkToLogAndIgnore)
            .alsoTo(sinkToKafka)
            .to(Sink.ignore());
//            .to(Sink.foreach(Partner::sink));

    public final static Sink<Citizen, NotUsed> children = Flow.of(Citizen.class)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeDropped_IfDied())
            .filter(Adulthood::filter)
//            .filter(Partner::filter)
            .alsoTo(sinkToLogAndIgnore)
            .alsoTo(sinkToKafka)
            .to(Sink.foreach(Children::sink));

    public final static Sink<Citizen, NotUsed> logged = Flow.of(Citizen.class)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeDropped_IfDied())
            .to(sinkToLogAndIgnore);

    private static void requeue(Citizen c) {
        System.out.println("Requeue:" + c.rawMessage);
    }

    private static void logAndDrop(Citizen c) {
        System.out.println("Dropped: " + c.rawMessage);

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

    static class Birth {
        private final static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        static Citizen sink(Citizen c) {
            set.add(c.name);
            return c;
        }

        static boolean filter(Citizen citizen) {
            return set.contains(citizen.name);
        }
    }

    static class Adulthood {
        private final static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        static Citizen sink(Citizen c) {
            set.add(c.rawMessage);
            return c;
        }

        static boolean filter(Citizen citizen) {
            return set.contains(citizen.rawMessage);
        }

    }

    static class Partner {
        private final static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        static Partnership sink(Partnership partnership) {
            set.add(partnership.rawMessage);
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
        }
    }
}
