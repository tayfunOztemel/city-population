package city;

public class Couple {

    public final Citizen c1;
    public final Citizen c2;
    public String rawMessage;

    private Couple(String rawMessage) {
        this.rawMessage = rawMessage;
        final String[] split = rawMessage.split("-");
        c1 = Citizen.toCitizen(rawMessage, split[1]);
        c2 = Citizen.toCitizen(rawMessage, split[2]);
    }

    static Couple toPartnership(String s) {
        return new Couple(s);
    }

    static Couple withChildren(String s) {
        return new Couple(s);
    }

}
