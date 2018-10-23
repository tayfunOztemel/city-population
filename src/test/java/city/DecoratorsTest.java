package city;

import city.events.EventHandler;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DecoratorsTest {

    @Test
    public void testLogUnBornCitizens() throws Exception {
        final Citizen citizen = Citizen.toCitizen("Birth-1");

        Citizen applied = Decorators.toBeRequeued_IfUnbornYet().apply(citizen);

        assertTrue(applied instanceof Citizen.Requeue);

        EventHandler.BirthHandler.sink(citizen);

        applied = Decorators.toBeLogged_IfBorn().apply(citizen);

        assertTrue(applied instanceof Citizen.Logged);

        EventHandler.DeathHandler.sink(Citizen.toCitizen("Death-1"));

        applied = Decorators.toBeLogged_IfBorn().apply(Citizen.toCitizen("Death-1"));

        assertTrue(applied instanceof Citizen.Logged);

        applied = Decorators.toBeLogged_IfDied().apply(Citizen.toCitizen("Death-1"));

        assertTrue(applied instanceof Citizen.Logged);

        applied = Decorators.toBeLogged_IfDied().apply(Citizen.toCitizen("Birth-1"));

        assertTrue(applied instanceof Citizen.Logged);

    }

}