package city.events;

import city.Citizen;

import java.util.LinkedList;
import java.util.Optional;

public class Events extends LinkedList<Event> {

    private Citizen currentPartner;

    @Override
    public boolean add(Event event) {
        if (event instanceof Event.Partner) {
            currentPartner = ((Event.Partner) event).getPartner();
        }
        return super.add(event);
    }

    Optional<Citizen> getCurrentPartner() {
        return Optional.ofNullable(currentPartner);
    }

    void resetPartner() {
        currentPartner = null;
    }

    boolean isCurrentPartner(Citizen c) {
        return c.equals(currentPartner);
    }

    boolean hasPartner() {
        return currentPartner != null;
    }
}
