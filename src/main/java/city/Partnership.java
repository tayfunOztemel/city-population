package city;

import akka.japi.function.Predicate;

public class Partnership {

    public final Citizen c1;
    public final Citizen c2;
    public String rawMessage;

    protected Partnership(String rawMessage) {
        this.rawMessage = rawMessage;
        final String[] split = rawMessage.split("-");
        c1 = Citizen.toCitizen(rawMessage, split[1]);
        c2 = Citizen.toCitizen(rawMessage, split[2]);
    }

    Partnership(Citizen c1, Citizen c2, String rawMessage) {
        this.c1 = c1;
        this.c2 = c2;
        this.rawMessage = rawMessage;
    }

    static Partnership toPartnership(String s) {
        return new Partnership(s);
    }

    static Partnership withChildren(String s) {
        return new Partnership(s);
    }

    static Predicate<Partnership> ifPartnersNeedRequeue() {
        return p -> p.c1 instanceof Citizen.Requeue || p.c2 instanceof Citizen.Requeue;
    }

    static Predicate<Partnership> ifPartnersNeedLogging() {
        return p -> p.c1 instanceof Citizen.Logged || p.c2 instanceof Citizen.Logged;
    }
}
