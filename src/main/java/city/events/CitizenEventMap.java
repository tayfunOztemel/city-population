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

    boolean contains(Citizen c, Event event) {
        if (!keySet().contains(c.name)) {
            return false;
        }
        return get(c.name).contains(event);
    }

    boolean isCurrentPartner(Citizen c, String o) {
        if (!keySet().contains(c.name)) {
            return false;
        }
        return get(c.name).isCurrentPartner(o);
    }

    boolean isChangingPartner(Citizen c, String name) {
        if (!keySet().contains(c.name)) {
            return false;
        }
        final Optional<String> currentPartner = get(c.name).getCurrentPartner();
        return currentPartner.map(s -> !(s.equals(name))).orElse(false);
    }

    void divorce(Citizen citizen) {
        get(citizen.name).getCurrentPartner().ifPresent( oldPartner -> {
            get(oldPartner).resetCurrentPartner();
        });
    }
}
