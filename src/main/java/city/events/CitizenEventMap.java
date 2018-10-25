package city.events;

import city.Citizen;

import java.util.HashMap;
import java.util.Optional;

final class CitizenEventMap extends HashMap<String, Events> {

    void put(Citizen citizen, Event event) {
        if (!keySet().contains(citizen.name)) {
            put(citizen.name, new Events());
        }
        get(citizen.name).add(event);
    }

    Events get(Citizen citizen) {
        return get(citizen.name);
    }

    boolean contains(Citizen c, Event event) {
        if (!keySet().contains(c.name)) {
            return false;
        }
        return get(c.name).contains(event);
    }

    boolean isCurrentPartner(Citizen c, Citizen o) {
        if (!keySet().contains(c.name)) {
            return false;
        }
        return get(c.name).isCurrentPartner(o);
    }

    boolean isChangingPartner(Citizen citizen, Citizen newPartner) {
        if (!keySet().contains(citizen.name)) {
            return false;
        }
        final Optional<Citizen> currentPartner = get(citizen.name).getCurrentPartner();
        return currentPartner.map(partner -> !(partner.equals(newPartner))).orElse(false);
    }

    void endPartnershipOf(Citizen citizen) {
        get(citizen.name).getCurrentPartner().ifPresent(oldPartner -> {
            get(oldPartner).resetPartner();
        });
    }

    void newPartnershipOf(String rawMessage, Citizen c1, Citizen c2) {
        put(c1, new Event.Partner(rawMessage, c2));
        put(c2, new Event.Partner(rawMessage, c1));

    }

    void born(Citizen citizen) {
        put(citizen, new Event.Birth());
    }

    void adulthood(Citizen citizen) {
        put(citizen, new Event.Adulthood());
    }

    void haveChildren(String rawMessage, Citizen c1, Citizen c2) {
        put(c1, new Event.Children(rawMessage));
        put(c2, new Event.Children(rawMessage));
    }

    void died(Citizen citizen) {
        final Events events = get(citizen);
        events.getCurrentPartner().ifPresent(c -> get(c).resetPartner());
        events.resetPartner();
        put(citizen, new Event.Death());
    }

    void education(Citizen citizen) {
        put(citizen, new Event.Education());
    }

    boolean isAdult(Citizen citizen) {
        return contains(citizen, new Event.Adulthood());
    }

    boolean arePartners(Citizen c1, Citizen c2) {
        return isCurrentPartner(c1, c2)
                && isCurrentPartner(c2, c1);
    }

    boolean isDead(Citizen citizen) {
        return contains(citizen, new Event.Death());
    }

    boolean isBorn(Citizen citizen) {
        return contains(citizen, new Event.Birth());
    }

    boolean hasPartnership(Citizen citizen) {
        return get(citizen.name).hasPartner();
    }
}
