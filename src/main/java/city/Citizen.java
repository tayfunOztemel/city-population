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

}
