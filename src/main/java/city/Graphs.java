package city;

import akka.NotUsed;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import city.events.EventHandler.*;

import static city.Citizen.ifCitizenNeedsLogging;
import static city.Citizen.ifCitizenNeedsRequeue;
import static city.Decorators.*;
import static city.Partnership.ifPartnersNeedLogging;
import static city.Partnership.ifPartnersNeedRequeue;

public class Graphs {

    public final static Sink<String, NotUsed> birthFlow = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .map(toBeLogged_IfBorn())
            .map(toBeLogged_IfDied())
            .divertTo(Sinks.sinkEventToLog, ifCitizenNeedsLogging())
            .to(Sink.foreach(BirthHandler::sink));

    public final static Sink<String, NotUsed> deathFlow = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeLogged_IfDied())
            .divertTo(Sinks.sinkEventToKafka, ifCitizenNeedsRequeue())
            .divertTo(Sinks.sinkEventToLog, ifCitizenNeedsLogging())
            .to(Sink.foreach(DeathHandler::sink));

    public final static Sink<String, NotUsed> educationFlow = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeLogged_IfDied())
            .map(toBeLogged_IfEducated())
            .divertTo(Sinks.sinkEventToKafka, ifCitizenNeedsRequeue())
            .divertTo(Sinks.sinkEventToLog, ifCitizenNeedsLogging())
            .to(Sink.foreach(EducationHandler::sink));

    public final static Sink<String, NotUsed> adulthoodFlow = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeLogged_IfDied())
            .map(toBeLogged_IfAdult())
            .divertTo(Sinks.sinkEventToKafka, ifCitizenNeedsRequeue())
            .divertTo(Sinks.sinkEventToLog, ifCitizenNeedsLogging())
            .to(Sink.foreach(AdulthoodHandler::sink));

    public final static Sink<String, NotUsed> partnerFlow = Flow.of(String.class)
            .map(Partnership::toPartnership)
            .map(toBeRequeued_IfPartnersUnbornYet())
            .map(toBeLogged_IfPartnersDied())
            .map(toBeRequeued_IfPartnersNotAdultYet())
            .map(toBeDropped_IfPartners())
            .divertTo(Sinks.sinkPartnerEventToKafka, ifPartnersNeedRequeue())
            .divertTo(Sinks.sinkPartnerEventToLog, ifPartnersNeedLogging())
            .to(Sink.foreach(PartnershipHandler::sink));

    public final static Sink<String, NotUsed> childrenFlow = Flow.of(String.class)
            .map(Partnership::withChildren)
            .map(toBeRequeued_IfPartnersUnbornYet())
            .map(toBeLogged_IfPartnersDied())
            .map(toBeRequeued_IfPartnersNotAdultYet())
            .map(toBeRequeued_IfNotPartnersYet())
            .divertTo(Sinks.sinkPartnerEventToKafka, ifPartnersNeedRequeue())
            .divertTo(Sinks.sinkPartnerEventToLog, ifPartnersNeedLogging())
            .to(Sink.foreach(ChildrenHandler::sink));

    public final static Sink<String, NotUsed> ignoredEventFlow = Flow.of(String.class)
            .map(Citizen::toCitizen)
            .map(toBeRequeued_IfUnbornYet())
            .map(toBeLogged_IfDied())
            .to(Sinks.sinkEventToLog);

}
