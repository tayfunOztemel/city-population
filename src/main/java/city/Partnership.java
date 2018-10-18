package city;

public class Partnership {

    final Citizen c1;
    final Citizen c2;
    final public String rawMessage;

    private Partnership(Citizen c1, Citizen c2) {
        this.c1 = c1;
        this.c2 = c2;
        rawMessage = "Partner-" + c1.name + "-" + c2.name;
    }

    Partnership(String rawMessage) {
        this.rawMessage = rawMessage;
        final String[] split = rawMessage.split("-");
        c1 = Citizen.toCitizen(rawMessage, split[1]);
        c2 = Citizen.toCitizen(rawMessage, split[2]);
    }

    static Partnership of(Citizen c1, Citizen c2) {
        return new Partnership(c1, c2);
    }
}
