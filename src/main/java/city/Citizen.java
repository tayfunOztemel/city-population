package city;

import akka.japi.function.Predicate;

public class Citizen {

    final public String name;
    final public String rawMessage;
    final String event;

    private Citizen(String rawMessage, String event, String name) {
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
        return new Citizen(rawMessage, rawMessage.split("-")[0], name);
    }

    public static Citizen toCitizen(String rawMessage) {
        String[] split = rawMessage.split("-");
        return new Citizen(rawMessage, split[0], split[1]);
    }

    static Predicate<Citizen> ifCitizenNeedsRequeue() {
        return c -> c instanceof Citizen.Requeue;
    }

    static Predicate<Citizen> ifCitizenNeedsLogging() {
        return c -> c instanceof Citizen.Logged;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Citizen
                && ((Citizen) obj).name.equals(name);
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
