package kafka;

import akka.actor.ActorSystem;
import akka.kafka.*;
import akka.kafka.javadsl.Consumer;
import akka.kafka.javadsl.Producer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import com.typesafe.config.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

public class CityEventConsumerTest {

    private Map<String, String> map = new ConcurrentHashMap<>();

    private static ConsumerSettings<String, String> consumerSettings(ActorSystem system) {
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

    void test(ActorMaterializer materializer) {

        final ActorSystem system = ActorSystem.create("city-population");

        Consumer.Control control =
                CityEventConsumer.getInstance(system).source()
                        .map(elem -> {
                            System.out.println("print Element:" + elem);
                            return elem;
                        })
                        .log("log Record.value()", s -> s.record().value())
                        .mapAsync(1, msg -> business(msg).thenApply(done -> msg.committableOffset()))
                        .batch(20, ConsumerMessage::createCommittableOffsetBatch, ConsumerMessage.CommittableOffsetBatch::updated)
                        .mapAsync(3, c -> c.commitJavadsl())
//                        .mapAsync(1, offset -> offset.commitJavadsl())
                        .to(Sink.ignore())
                        .run(materializer);
    }

    private CompletionStage<String> business(ConsumerMessage.CommittableMessage<String, String> msg) {
        if (!msg.record().value().contains("-")) {
            System.out.println("ommitting rawMessage");
            return CompletableFuture.completedFuture("true");
        }
        final String[] split = msg.record().value().split("-");
        final String event = split[0];
        final String person = split[1];

        final boolean contains = map.keySet().contains(person);
        System.out.println("Already contains " + person + "?:" + contains);

        map.putIfAbsent(person, event);

        return CompletableFuture.completedFuture("true");
    }

    void producer(ProducerSettings producerSettings, Materializer materializer, org.apache.kafka.clients.producer.Producer producer, ActorSystem system) {
        Consumer.committableSource(consumerSettings(system), getTopics())
                .map(msg -> new ProducerMessage.Message<String, String, ConsumerMessage.Committable>(
                                new ProducerRecord<>(KafkaTopic.CITY_POPULATION_TOPIC_NAME, msg.record().key(), msg.record().value()),
                                msg.committableOffset()
                        )
                )
                .to(Producer.commitableSink(producerSettings, producer))
                .run(materializer);
    }

    private AutoSubscription getTopics() {
        return Subscriptions.topics(KafkaTopic.CITY_POPULATION_TOPIC_NAME);
    }

}