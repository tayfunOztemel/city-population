package kafka;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.javadsl.Sink;
import com.typesafe.config.Config;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class CityEventProducer {

    private static CityEventProducer instance;

    private KafkaProducer kafkaProducer;
    private ProducerSettings<String, String> producerSettings;

    private CityEventProducer(ProducerSettings<String, String> settings) {
        producerSettings = settings;
        kafkaProducer = producerSettings.createKafkaProducer();
    }

    public static void initialize(ActorSystem system) {
        if (instance == null) {
            instance = new CityEventProducer(createSettings(system));
        }
    }

    public static CityEventProducer getInstance() {
        return instance;
    }

    private static ProducerSettings<String, String> createSettings(ActorSystem system) {
        final Config config = system.settings().config().getConfig("akka.kafka.producer");
        return ProducerSettings.create(config, new StringSerializer(), new StringSerializer())
                .withBootstrapServers("localhost:9092");
    }

    public static ProducerRecord<String, String> toRecord(String rawMessage) {
        return new ProducerRecord<String, String>(KafkaTopic.CITY_POPULATION_TOPIC_NAME, rawMessage);
    }

    public Sink<ProducerRecord<String, String>, NotUsed> sink() {
        return Producer.plainSink(producerSettings, kafkaProducer);
    }

}
