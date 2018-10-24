package city.events;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventsTest {

    private Events events;

    @Before
    public void setup() {
        events = new Events();
    }

    @Test
    public void testPartner() {
        assertFalse(events.getCurrentPartner().isPresent());

        events.add(new Event.Partner("Partner-1-2", "1"));

        assertTrue(events.getCurrentPartner().isPresent());
        assertEquals("1", events.getCurrentPartner().get());
    }

    @Test
    public void testDivorsion() {
        events.add(new Event.Partner("Partner-1-2", "1"));
        events.add(new Event.Partner("Partner-1-2", "2"));
        events.add(new Event.Partner("Partner-1-3", "1"));
        events.add(new Event.Partner("Partner-1-3", "3"));

        assertTrue(events.getCurrentPartner().isPresent());
        assertEquals("1", events.getCurrentPartner().get());
    }

}