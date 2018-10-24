import akka.actor.ActorSystem;
import akka.kafka.ConsumerMessage;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import city.Citizen;
import city.Flows;
import kafka.CityEventConsumer;
import kafka.CityEventProducer;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    private static Map<String, Citizen> map = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("city-population");
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        CityEventProducer.initialize(system);
        CityEventConsumer.initialize(system);

        final CityEventConsumer cityEventConsumer = CityEventConsumer.getInstance();

        cityEventConsumer.source()
                .throttle(1, Duration.ofSeconds(1))
                .filter(Main::formatCheck)
                .map(msg -> msg.record().value())
                .divertTo(Flows.birthFlow, c -> c.startsWith("Birth"))
                .divertTo(Flows.deathFlow, c -> c.startsWith("Death"))
                .divertTo(Flows.adulthoodFlow, c -> c.startsWith("Adulthood"))
                .divertTo(Flows.partnerFlow, c -> c.startsWith("Partner"))
                .divertTo(Flows.childrenFlow, c -> c.startsWith("Children"))
                .divertTo(Flows.educationFlow, c -> c.startsWith("Education"))
                .divertTo(Flows.ignoredEventFlow, Main::ignored)
                .to(Sink.ignore())
                .run(materializer);

        CityPopulation.main(new String[]{"Started"});

        System.out.println("started");
    }

    private static boolean ignored(String rawMessage) {
        return rawMessage.startsWith("Travels") || rawMessage.startsWith("Accidents");
    }

    private static boolean formatCheck(ConsumerMessage.CommittableMessage<String, String> msg) {
        final String value = msg.record().value();
        return !value.isEmpty() && value.contains("-");
    }

}
