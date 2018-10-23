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

    private final ConsumerSettings<String, String> consumerSettings;
    private final Source<ConsumerMessage.CommittableMessage<String, String>, Consumer.Control> cityPopulationSource;

    private CityEventConsumer(ActorSystem system) {
        consumerSettings = createConsumerSettings(system);
        cityPopulationSource = Consumer.committableSource(consumerSettings, getTopics());
    }

    public static void initialize(ActorSystem system) {
        if (instance == null) {
            instance = new CityEventConsumer(system);
        }
    }

    public static CityEventConsumer getInstance() {
        return instance;
    }

    private static ConsumerSettings<String, String> createConsumerSettings(ActorSystem system) {
        final Config config = system.settings().config().getConfig("akka.kafka.consumer");
        return ConsumerSettings.create(config, new StringDeserializer(), new StringDeserializer())
                .withBootstrapServers("localhost:9092")
                .withGroupId("group1")
                .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        /*
         For using
            Consumer.plainSource
         and
            Consumer.plainPartitionedManualOffsetSource
         with an Offset Storage external to Kafka

            settings.
                .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
                .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000");
        */
    }

    private AutoSubscription getTopics() {
        return Subscriptions.topics(KafkaTopic.CITY_POPULATION_TOPIC_NAME);
    }

    public Source<ConsumerMessage.CommittableMessage<String, String>, Consumer.Control> source() {
        return cityPopulationSource;
    }


}