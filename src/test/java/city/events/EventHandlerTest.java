package city.events;

import city.Citizen;
import city.Partnership;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventHandlerTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void persistBirthRecordOfACitizen() {
        Citizen citizen = Citizen.toCitizen("BirthHandler-1");
        EventHandler.BirthHandler.sink(citizen);

        assertTrue(EventHandler.BirthHandler.filter(citizen));
    }

    @Test
    public void persistDeathRecordOfACitizen() {
        Citizen citizen = Citizen.toCitizen("BirthHandler-1");
        EventHandler.BirthHandler.sink(citizen);
        EventHandler.DeathHandler.sink(citizen);

        assertTrue(EventHandler.BirthHandler.filter(citizen));
        assertTrue(EventHandler.DeathHandler.filter(citizen));
    }

    @Test
    public void eventsAreLinked() {
        Citizen citizen = Citizen.toCitizen("BirthHandler-1");
        EventHandler.BirthHandler.sink(citizen);
        EventHandler.AdulthoodHandler.sink(citizen);
        EventHandler.DeathHandler.sink(citizen);

        assertTrue(EventHandler.BirthHandler.filter(citizen));
        assertTrue(EventHandler.AdulthoodHandler.filter(citizen));
        assertTrue(EventHandler.DeathHandler.filter(citizen));
    }

    @Test
    public void partnership() {
        Citizen citizen = Citizen.toCitizen("BirthHandler-1");
        Citizen citizen2 = Citizen.toCitizen("BirthHandler-2");
        Partnership p12 = new Partnership("PartnershipHandler-1-2");
        Partnership p21 = new Partnership("PartnershipHandler-2-1");
        Partnership p13 = new Partnership("PartnershipHandler-1-3");

        EventHandler.BirthHandler.sink(citizen);
        EventHandler.BirthHandler.sink(citizen2);
        EventHandler.PartnershipHandler.sink(p12);

        assertTrue(EventHandler.BirthHandler.filter(citizen));
        assertTrue(EventHandler.PartnershipHandler.filter(p12));
        assertTrue(EventHandler.PartnershipHandler.filter(p21));
        assertFalse(EventHandler.PartnershipHandler.filter(p13));
    }
}