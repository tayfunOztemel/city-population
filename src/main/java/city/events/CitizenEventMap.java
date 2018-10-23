package city.events;

import city.Citizen;

import java.util.HashMap;
import java.util.LinkedList;

final class CitizenEventMap extends HashMap<String, LinkedList<Event>> {

    void put(Citizen citizen, Event event) {
        if (!keySet().contains(citizen.name)) {
            put(citizen.name, new LinkedList<>());
        }
        get(citizen.name).add(event);
    }

    boolean contains(Citizen c, Event o) {
        if (!keySet().contains(c.name)) {
            return false;
        }
        return get(c.name).contains(o);
    }

    boolean containsLast(Citizen c, Event o) {
        if (!keySet().contains(c.name)) {
            return false;
        }
        return get(c.name).getLast().equals(o);
    }

}
