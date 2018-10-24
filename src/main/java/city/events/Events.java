package city.events;

import java.util.LinkedList;
import java.util.Optional;

public class Events extends LinkedList<Event> {

    private String currentPartner;

    @Override
    public boolean add(Event event) {
        if (event instanceof Event.Partner) {
            currentPartner = ((Event.Partner)event).getPartner();
        }
        return super.add(event);
    }

    public Optional<String> getCurrentPartner() {
        return Optional.ofNullable(currentPartner);
    }

    public void resetCurrentPartner() {
        currentPartner = null;
    }

    public boolean isCurrentPartner(String o) {
        return o.equals(currentPartner);
    }
}
