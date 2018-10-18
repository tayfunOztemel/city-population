import akka.actor.ActorSystem;
import akka.kafka.ConsumerMessage;
import akka.kafka.ProducerSettings;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import city.Citizen;
import city.Graphs;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    private static Map<String, Citizen> map = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("city-population");
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        final CityEventProducer cityEventProducer = CityEventProducer.getInstance(system);

        final CityEventConsumer cityEventConsumer = CityEventConsumer.getInstance(system);

        final KafkaProducer kafkaProducer = cityEventProducer.getKafkaProducer();
        final ProducerSettings<String, String> kafkaProducerSettings = cityEventProducer.getProducerSettings();


        cityEventConsumer.source()
                .filter(Main::checkFormat)
                .filter(msg -> msg.record() != null) // TODO delete this
                .map(msg -> msg.record().value())
                .divertTo(Graphs.birthFlow, c -> c.startsWith("Birth"))
                .divertTo(Graphs.deathFlow, c -> c.startsWith("Death"))
                .divertTo(Graphs.adulthoodSink, c -> c.startsWith("Adulthood"))
                .divertTo(Graphs.partnerSinkGraph, c -> c.startsWith("Partner"))
//                .divertTo(Graphs.children, c -> c.startsWith("Children"))
                .divertTo(Graphs.educationFlow, c -> c.startsWith("Education"))
                .divertTo(Graphs.logged, Main::ignored)
                .to(Sink.foreach(c -> System.out.println("Unknown event:" + c)))
                .run(materializer);
    }

    private static boolean ignored(String rawMessage) {
        return rawMessage.startsWith("Travels") || rawMessage.startsWith("Accidents");
    }

    private static boolean checkFormat(ConsumerMessage.CommittableMessage<String, String> msg) {
        final String value = msg.record().value();
        return !value.isEmpty() && value.contains("-");
    }

}
