package city;

public class Citizen {

    final public String name;
    final public String rawMessage;

    private Citizen(String rawMessage, String name) {
        this.rawMessage = rawMessage;
        this.name = name;
    }

    static Citizen toCitizen(String rawMessage, String name) {
        return new Citizen(rawMessage, name);
    }

    static Citizen toCitizen(String rawMessage) {
        String[] split = rawMessage.split("-");
        return new Citizen(rawMessage, split[1]);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Citizen
                && ((Citizen) obj).name.equals(name);
    }

}
