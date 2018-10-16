package city;

import akka.kafka.ConsumerMessage;

public class Citizen {

    public final String event;
    public final String name;
    public final String rawMessage;

    public Citizen(String string) {
        final String[] split = string.split("-");
        event = split[0];
        name = split[1];
        rawMessage = string;
    }

    public static Citizen toCitizen(ConsumerMessage.CommittableMessage<String, String> msg) {
        return new Citizen(msg.record().value());
    }
}
