package city;

import akka.NotUsed;
import akka.stream.Graph;
import akka.stream.SinkShape;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import kafka.CityEventProducer;

final class Sinks {

    static final Graph<SinkShape<Citizen>, NotUsed> sinkEventToKafka = Flow.of(Citizen.class)
            .filter(c -> c instanceof Citizen.Requeue)
            .map(p -> {
                System.out.println("REQUEUED: " + p.rawMessage);
                return p;
            })
            .map(c -> CityEventProducer.toRecord(c.rawMessage))
            .to(Sink.fromGraph(CityEventProducer.getInstance().sink()));

    static final Sink<Partnership, NotUsed> sinkPartnerEventToKafka = Flow.of(Partnership.class)
            .filter(p -> (p.c1 instanceof Citizen.Requeue) || (p.c2 instanceof Citizen.Requeue))
            .map(p -> {
                System.out.println("REQUEUED: " + p.rawMessage);
                return p;
            })
            .map(p -> CityEventProducer.toRecord(p.rawMessage))
            .to(Sink.fromGraph(CityEventProducer.getInstance().sink()));

    static final Graph<SinkShape<Citizen>, NotUsed> sinkEventToLog = Flow.of(Citizen.class)
            .filter(c -> c instanceof Citizen.Logged)
            .map(c -> c.rawMessage)
            .to(Sink.foreach(rawMessage -> System.out.println("DROPPED: " + rawMessage)));

    static final Sink<Partnership, NotUsed> sinkPartnerEventToLog = Flow.of(Partnership.class)
            .filter(p -> (p.c1 instanceof Citizen.Logged) || (p.c2 instanceof Citizen.Logged))
            .map(p -> p.rawMessage)
            .to(Sink.foreach(rawMessage -> System.out.println("DROPPED: " + rawMessage)));


}