package city.events;

import city.Citizen;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CitizenEventMapTest {

    private Citizen c1;
    private Citizen c2;
    private Citizen c3;
    private Citizen c4;
    private CitizenEventMap map;

    @Before
    public void setup() {
        c1 = Citizen.toCitizen("Birth-1");
        c2 = Citizen.toCitizen("Birth-2");
        c3 = Citizen.toCitizen("Birth-3");
        c4 = Citizen.toCitizen("Birth-4");

        map = new CitizenEventMap();

    }
    @Test
    public void put() {
    }

    @Test
    public void contains() {
        map.put(c1, new Event.Partner("Partner-1-2", "2"));
        map.put(c2, new Event.Partner("Partner-1-2", "1"));

        assertTrue(map.isCurrentPartner(c1, "2"));
        assertTrue(map.isCurrentPartner(c2, "1"));
    }

    @Test
    public void containsLast() {

        Citizen c = Citizen.toCitizen("Birth-1");
        Citizen c2 = Citizen.toCitizen("Birth-2");
        Citizen c3 = Citizen.toCitizen("Birth-3");
        Citizen c4 = Citizen.toCitizen("Birth-4");

        final CitizenEventMap citizenEventMap = new CitizenEventMap();
        citizenEventMap.put(c, new Event.Birth());
        citizenEventMap.put(c, new Event.Adulthood());
        citizenEventMap.put(c, new Event.Partner("Partner-2", "2"));
        citizenEventMap.put(c, new Event.Partner("Partner-3", "3"));
        citizenEventMap.put(c, new Event.Education());
        citizenEventMap.put(c, new Event.Partner("Partner-4", "4"));
        citizenEventMap.put(c, new Event.Education());

        assertTrue(citizenEventMap.isCurrentPartner(c,"4"));

        citizenEventMap.put(c, new Event.Partner("Partner-5", "5"));

        assertTrue(citizenEventMap.isCurrentPartner(c, "5"));
    }
}