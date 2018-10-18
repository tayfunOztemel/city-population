package city;

import akka.NotUsed;
import akka.stream.Graph;
import akka.stream.SinkShape;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;

final class Sinks {

    static final Sink<Partnership, NotUsed> sinkPartnerEventToLog = Flow.of(Partnership.class)
            .filter(p -> (p.c1 instanceof Citizen.Logged) || (p.c2 instanceof Citizen.Logged))
            .map(p -> p.rawMessage)
            .to(Sink.foreach(Sinks::logAndDrop));

    static final Sink<Partnership, NotUsed> sinkPartnerEventToKafka = Flow.of(Partnership.class)
            .filter(p -> (p.c1 instanceof Citizen.Requeue) || (p.c2 instanceof Citizen.Requeue))
            .map(p -> p.rawMessage)
            .to(Sink.foreach(Sinks::requeue));

    static final Graph<SinkShape<Citizen>, ?> sinkEventToLog = Flow.of(Citizen.class)
            .filter(c -> c instanceof Citizen.Logged)
            .map(c -> c.rawMessage)
            .to(Sink.foreach(Sinks::logAndDrop));

    static final Graph<SinkShape<Citizen>, ?> sinkEventToKafka = Flow.of(Citizen.class)
            .filter(c -> c instanceof Citizen.Requeue)
            .map(c -> c.rawMessage)
            .to(Sink.foreach(Sinks::requeue));

    static void logAndDrop(String rawMessage) {
        System.out.println("Dropped: " + rawMessage);
    }

    static void requeue(String rawMessage) {
        System.out.println("Requeue:" + rawMessage);
    }
}
