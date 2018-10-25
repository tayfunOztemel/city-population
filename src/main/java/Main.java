import akka.actor.ActorSystem;
import akka.kafka.ConsumerMessage;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import city.Flows;
import kafka.CityEventConsumer;
import kafka.CityEventProducer;

import java.time.Duration;

public class Main {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("city-population");
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        CityEventProducer.initialize(system);
        CityEventConsumer.initialize(system);

        initializeStream(materializer);

        CityPopulationAPI.main(new String[0]);
    }

    private static void initializeStream(ActorMaterializer materializer) {
        CityEventConsumer.getInstance()
                .source()
                .throttle(1, Duration.ofSeconds(1))
                .filter(Main::syntaxCheck)
                .map(msg -> msg.record().value())
                .divertTo(Flows.birthFlow, c -> c.startsWith("Birth"))
                .divertTo(Flows.deathFlow, c -> c.startsWith("Death"))
                .divertTo(Flows.adulthoodFlow, c -> c.startsWith("Adulthood"))
                .divertTo(Flows.partnerFlow, c -> c.startsWith("Partner"))
                .divertTo(Flows.childrenFlow, c -> c.startsWith("Children"))
                .divertTo(Flows.educationFlow, c -> c.startsWith("Education"))
                .divertTo(Flows.ignoredEventFlow, Main::ignoredEvents)
                .to(Sink.ignore())
                .run(materializer);
    }

    private static boolean ignoredEvents(String rawMessage) {
        return rawMessage.startsWith("Travels") || rawMessage.startsWith("Accidents");
    }

    private static boolean syntaxCheck(ConsumerMessage.CommittableMessage<String, String> msg) {
        final String value = msg.record().value();
        return !value.isEmpty() && value.contains("-");
    }

}
