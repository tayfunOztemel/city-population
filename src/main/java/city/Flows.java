package city;

import akka.NotUsed;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import city.events.EventHandler.*;

import static city.FlowPredicates.*;

public class Flows {

    public final static Sink<String, NotUsed> birthFlow = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .divertTo(Sinks.sinkEventToLog, ifCitizenDied())
            .to(Sink.foreach(BirthHandler::sink));

    public final static Sink<String, NotUsed> deathFlow = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .divertTo(Sinks.sinkEventToKafka, ifCitizenNotBornYet())
            .divertTo(Sinks.sinkEventToLog, ifCitizenDied())
            .to(Sink.foreach(DeathHandler::sink));

    public final static Sink<String, NotUsed> educationFlow = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .divertTo(Sinks.sinkEventToKafka, ifCitizenNotBornYet())
            .divertTo(Sinks.sinkEventToLog, ifCitizenDied())
            .to(Sink.foreach(EducationHandler::sink));

    public final static Sink<String, NotUsed> adulthoodFlow = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .divertTo(Sinks.sinkEventToKafka, ifCitizenNotBornYet())
            .divertTo(Sinks.sinkEventToLog, ifCitizenDied())
            .to(Sink.foreach(AdulthoodHandler::sink));

    public final static Sink<String, NotUsed> partnerFlow = Flow.of(String.class)
            .map(Partnership::toPartnership)
            .divertTo(Sinks.sinkPartnerEventToKafka, ifPartnersUnbornYet())
            .divertTo(Sinks.sinkPartnerEventToLog, ifPartnersDied())
            .divertTo(Sinks.sinkPartnerEventToKafka, ifPartnersNotAdultYet())
            .to(Sink.foreach(PartnershipHandler::sink));

    public final static Sink<String, NotUsed> childrenFlow = Flow.of(String.class)
            .map(Partnership::withChildren)
            .divertTo(Sinks.sinkPartnerEventToKafka, ifPartnersUnbornYet())
            .divertTo(Sinks.sinkPartnerEventToLog, ifPartnersDied())
            .divertTo(Sinks.sinkPartnerEventToKafka, ifPartnersNotAdultYet())
            .divertTo(Sinks.sinkPartnerEventToKafka, ifNotPartnersYet())
            .to(Sink.foreach(ChildrenHandler::sink));

    public final static Sink<String, NotUsed> ignoredEventFlow = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .divertTo(Sinks.sinkEventToKafka, ifCitizenNotBornYet())
            .divertTo(Sinks.sinkEventToLog, ifCitizenDied())
            .to(Sinks.sinkEventToLog);

}
