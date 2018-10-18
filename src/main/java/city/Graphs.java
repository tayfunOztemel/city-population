package city;

import akka.NotUsed;
import akka.japi.Pair;
import akka.stream.*;
import akka.stream.javadsl.*;
import city.Citizen.Logged;
import city.events.Events.*;

import java.util.Arrays;
import java.util.List;

import static city.Citizen.Requeue;
import static city.Decorators.*;

public class Graphs {

    private static Graph<SinkShape<Citizen>, ?> sinkToLog = Flow.of(Citizen.class)
            .filter(c -> c instanceof Logged)
            .map(c -> c.rawMessage)
            .to(Sink.foreach(Graphs::logAndDrop));

    private static Graph<SinkShape<Citizen>, ?> sinkToKafka = Flow.of(Citizen.class)
            .filter(c -> c instanceof Requeue)
            .map(c -> c.rawMessage)
            .to(Sink.foreach(Graphs::requeue));

    public final static Sink<String, NotUsed> birthFlow = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .map(toBeLogged_IfBorn())
            .map(toBeLogged_IfDied())
            .alsoTo(sinkToLog)
            .filterNot(Decorators::isLoggedOrRequeue)
            .to(Sink.foreach(Birth::sink));

    public final static Sink<String, NotUsed> deathFlow = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeLogged_IfDied())
            .alsoTo(sinkToKafka)
            .alsoTo(sinkToLog)
            .filterNot(Decorators::isLoggedOrRequeue)
            .to(Sink.foreach(Death::sink));

    public final static Sink<String, NotUsed> educationFlow = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeLogged_IfDied())
            .map(toBeLogged_IfEducated())
            .alsoTo(sinkToKafka)
            .alsoTo(sinkToLog)
            .filterNot(Decorators::isLoggedOrRequeue)
            .to(Sink.foreach(Education::sink));

    public final static Sink<String, NotUsed> adulthoodSink = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeLogged_IfDied())
            .map(toBeDropped_IfAdult())
            .alsoTo(sinkToKafka)
            .alsoTo(sinkToLog)
            .filterNot(Decorators::isLoggedOrRequeue)
            .to(Sink.foreach(Adulthood::sink));

    // Parallel Processing
    private static final Sink<Partnership, NotUsed> sinkToPartnership = Flow.of(Partnership.class)
            .filterNot(p -> isLoggedOrRequeue(p.c1) || isLoggedOrRequeue(p.c2))
            .to(Sink.foreach(Partner::sink));


    private static final Sink<Partnership, NotUsed> sinkPartnersToLog = Flow.of(Partnership.class)
            .filter(p -> (p.c1 instanceof Logged) || (p.c2 instanceof Logged))
            .map(p -> p.rawMessage)
            .to(Sink.foreach(Graphs::logAndDrop));

    private static final Sink<Partnership, NotUsed> sinkToRequeu = Flow.of(Partnership.class)
            .filter(p -> (p.c1 instanceof Requeue) || (p.c2 instanceof Requeue))
            .map(p -> p.rawMessage)
            .to(Sink.foreach(Graphs::requeue));

    static List<Sink<Partnership, NotUsed>> list = Arrays.asList(sinkToRequeu, sinkPartnersToLog, sinkToPartnership);

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
                                .map(toBeLogged_IfDied())
                                .map(toBeRequeued_IfNotAdultYet()));
                        final FlowShape<Citizen, Citizen> adultValidationFlow2 = builder.add(Flow.of(Citizen.class)
                                .map(toBeRequeued_IfUnbornYet())
                                .map(toBeLogged_IfDied())
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
            .map(toBeLogged_IfDied())
            .to(sinkToLog);

    private static void requeue(String rawMessage) {
        System.out.println("Requeue:" + rawMessage);
    }

    private static void logAndDrop(String rawMessage) {
        System.out.println("Dropped: " + rawMessage);

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
