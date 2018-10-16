import akka.actor.ActorSystem;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;
import com.typesafe.config.Config;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

public class CityEventProducer {

    private static CityEventProducer instance;

    private KafkaProducer kafkaProducer;
    private ProducerSettings<String, String> producerSettings;

    private CityEventProducer(ProducerSettings<String, String> settings) {
        producerSettings = settings;
        kafkaProducer = producerSettings.createKafkaProducer();
    }

    public static CityEventProducer getInstance(ActorSystem system) {
        if (instance == null) {
            instance = new CityEventProducer(createSettings(system));
        }
        return instance;
    }

    public KafkaProducer getKafkaProducer() {
        return kafkaProducer;
    }

    public ProducerSettings<String, String> getProducerSettings() {
        return producerSettings;
    }

    public Map<MetricName, ? extends Metric> getMetrics() {
        return kafkaProducer.metrics();
    }

    private static ProducerSettings<String, String> createSettings(ActorSystem system) {
        final Config config = system.settings().config().getConfig("akka.kafka.producer");
        return ProducerSettings.create(config, new StringSerializer(), new StringSerializer())
                .withBootstrapServers("localhost:9092");
    }

    void test(ActorMaterializer materializer) {
        Source
                .from(Arrays.asList("CityEventProducer says", "echo", "echo", "echo"))
                .throttle(1, Duration.ofSeconds(1))
                .map(value -> new ProducerRecord<String, String>(KafkaTopic.CITY_POPULATION_TOPIC_NAME, value))
                .runWith(Producer.plainSink(producerSettings, kafkaProducer), materializer);
    }

}
