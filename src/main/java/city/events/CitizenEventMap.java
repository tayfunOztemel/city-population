package city.events;

import city.Citizen;

import java.util.HashMap;
import java.util.LinkedList;

final class CitizenEventMap extends HashMap<String, LinkedList<String>> {

    void put(Citizen citizen, String event) {
        if (!keySet().contains(citizen.name)) {
            put(citizen.name, new LinkedList<>());
        }
        get(citizen.name).add(event);
    }

    boolean contains(Citizen c, String event) {
        if (!keySet().contains(c.name)) {
            return false;
        }
        return get(c.name).contains(event);
    }

    boolean containsLast(Citizen c, String o) {
        if (!keySet().contains(c.name)) {
            return false;
        }
        return get(c.name).getLast().equals(o);
    }

}
