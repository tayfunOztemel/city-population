package city;

public class Partnership {

    public final Citizen c1;
    public final Citizen c2;
    public String rawMessage;

    private Partnership(String rawMessage) {
        this.rawMessage = rawMessage;
        final String[] split = rawMessage.split("-");
        c1 = Citizen.toCitizen(rawMessage, split[1]);
        c2 = Citizen.toCitizen(rawMessage, split[2]);
    }

    static Partnership toPartnership(String s) {
        return new Partnership(s);
    }

    static Partnership withChildren(String s) {
        return new Partnership(s);
    }

}
