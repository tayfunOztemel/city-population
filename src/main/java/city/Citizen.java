package city;

public class Citizen {

    final public String name;
    final String event;
    final public String rawMessage;

    private Citizen(String rawMessage, String name, String event) {
        this.rawMessage = rawMessage;
        this.name = name;
        this.event = event;
    }

    Citizen(Citizen c) {
        name = c.name;
        event = c.event;
        rawMessage = c.rawMessage;
    }

    static Citizen toCitizen(String rawMessage, String name) {
        return new Citizen(rawMessage, name, rawMessage.split("-")[0]);
    }

    static Citizen toCitizen(String rawMessage) {
        String[] split = rawMessage.split("-");
        return new Citizen(rawMessage, split[1], split[0]);
    }

    static class Requeue extends Citizen {
        private Citizen c;

        Requeue(Citizen c) {
            super(c);
            this.c = c;
        }
    }

    static class Logged extends Citizen {
        private Citizen c;

        Logged(Citizen c) {
            super(c);
            this.c = c;
        }
    }

}
