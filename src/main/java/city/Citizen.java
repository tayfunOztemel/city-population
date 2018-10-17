package city;

import akka.kafka.ConsumerMessage;

public class Citizen {

    public final String name;
    public final String event;
    public final String rawMessage;

    protected Citizen(Citizen c) {
        name = c.name;
        event = c.event;
        rawMessage = c.rawMessage;
    }

    public Citizen(String string) {
        String[] split = string.split("-");
        name = split[1];
        event = split[0];
        rawMessage = string;
    }

    public static Citizen toCitizen(ConsumerMessage.CommittableMessage<String, String> msg) {
        return new Citizen(msg.record().value());
    }

    public static class Requeue extends Citizen {
        private Citizen c;

        public Requeue(Citizen c) {
            super(c);
            this.c = c;
        }
    }

    public static class Drop extends Citizen {
        private Citizen c;

        public Drop(Citizen c) {
            super(c);
            this.c = c;
        }
    }

    public static class Partnership {

        public final Citizen c1;
        public final Citizen c2;
        public final String rawMessage;

        private Partnership(Citizen c1, Citizen c2) {
            this.c1 = c1;
            this.c2 = c2;
            rawMessage = "Partner-" + c1.name + "-" + c2.name;
        }

        public Partnership(String rawMessage) {
            this.rawMessage = rawMessage;
            final String[] split = rawMessage.split("-");
            c1 = new Citizen(split[0] + "-" + split[1]);
            c2 = new Citizen(split[0] + "-" + split[2]);
        }

        public static Partnership of(Citizen c1, Citizen c2) {
            return new Partnership(c1, c2);
        }
    }
}
