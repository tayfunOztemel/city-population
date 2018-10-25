package kafka;

import akka.actor.ActorSystem;
import akka.kafka.AutoSubscription;
import akka.kafka.ConsumerMessage;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.javadsl.Source;
import com.typesafe.config.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

public class CityEventConsumer {

    private static CityEventConsumer instance;

    private final Source<ConsumerMessage.CommittableMessage<String, String>, Consumer.Control> cityPopulationSource;

    public static void initialize(ActorSystem system) {
        if (instance == null) {
            instance = new CityEventConsumer(system);
        }
    }

    private CityEventConsumer(ActorSystem system) {
        cityPopulationSource = Consumer.committableSource(consumerSettings(system), subscription());
    }

    private static ConsumerSettings<String, String> consumerSettings(ActorSystem system) {
        final Config config = system.settings().config().getConfig("akka.kafka.consumer");
        return ConsumerSettings.create(config, new StringDeserializer(), new StringDeserializer())
                .withBootstrapServers("localhost:9092")
                .withGroupId("group1")
                .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    private AutoSubscription subscription() {
        return Subscriptions.topics(KafkaTopic.CITY_POPULATION_TOPIC_NAME);
    }

    public static CityEventConsumer getInstance() {
        return instance;
    }

    public Source<ConsumerMessage.CommittableMessage<String, String>, Consumer.Control> source() {
        return cityPopulationSource;
    }


}