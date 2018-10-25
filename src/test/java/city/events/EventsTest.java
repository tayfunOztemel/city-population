package city.events;

import city.Citizen;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventsTest {

    private Events events;

    private Citizen c1;
    private Citizen c2;
    private Citizen c3;

    @Before
    public void setup() {
        events = new Events();
        c1 = Citizen.toCitizen("Birth-1");
        c2 = Citizen.toCitizen("Birth-2");
        c3 = Citizen.toCitizen("Birth-3");
    }

    @Test
    public void testPartner() {
        assertFalse(events.getCurrentPartner().isPresent());

        events.add(new Event.Partner("Partner-1-2", c1));

        assertTrue(events.getCurrentPartner().isPresent());
        assertEquals("1", events.getCurrentPartner().get());
    }

    @Test
    public void testDivorsion() {
        events.add(new Event.Partner("Partner-1-2", c1));
        events.add(new Event.Partner("Partner-1-2", c2));
        events.add(new Event.Partner("Partner-1-3", c1));
        events.add(new Event.Partner("Partner-1-3", c3));

        assertTrue(events.getCurrentPartner().isPresent());
        assertEquals("1", events.getCurrentPartner().get());
    }

}