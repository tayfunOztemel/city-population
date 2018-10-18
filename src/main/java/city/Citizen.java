package city;

public class Citizen {

    public final String name;
    public final String event;
    public final String rawMessage;

    private Citizen(String rawMessage, String name, String event) {
        this.rawMessage = rawMessage;
        this.name = name;
        this.event = event;
    }

    protected Citizen(Citizen c) {
        name = c.name;
        event = c.event;
        rawMessage = c.rawMessage;
    }

    public static Citizen toCitizen(String rawMessage, String name) {
        return new Citizen(rawMessage, name, rawMessage.split("-")[0]);
    }

    public static Citizen toCitizen(String rawMessage) {
        String[] split = rawMessage.split("-");
        return new Citizen(rawMessage, split[1], split[0]);
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
            c1 = Citizen.toCitizen(rawMessage, split[1]);
            c2 = Citizen.toCitizen(rawMessage, split[2]);
        }

        public static Partnership of(Citizen c1, Citizen c2) {
            return new Partnership(c1, c2);
        }
    }
}
