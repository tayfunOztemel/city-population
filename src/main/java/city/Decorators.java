package city;

import akka.japi.function.Function;
import city.events.Events.Adulthood;
import city.events.Events.Birth;
import city.events.Events.Death;
import city.events.Events.Education;

class Decorators {

    static Function<Citizen, Citizen> toBeLogged_IfDied() {
        return c -> Death.filter(c) ? new Citizen.Logged(c) : c;
    }

    static Function<Citizen, Citizen> toBeLogged_IfBorn() {
        return c -> Birth.filter(c) ? new Citizen.Logged(c) : c;
    }

    static Function<Citizen, Citizen> toBeDropped_IfAdult() {
        return c -> Adulthood.filter(c) ? new Citizen.Logged(c) : c;
    }

    static Function<Citizen, Citizen> toBeLogged_IfEducated() {
        return c -> Education.filter(c) ? new Citizen.Logged(c) : c;
    }

    static Function<Citizen, Citizen> toBeRequeued_IfUnbornYet() {
        return c -> Birth.filter(c) ? c : new Citizen.Requeue(c);
    }

    static Function<Citizen, Citizen> toBeRequeued_IfNotAdultYet() {
        return c -> Adulthood.filter(c) ? c : new Citizen.Requeue(c);
    }

    static boolean isLoggedOrRequeue(Citizen citizen) {
        return (citizen instanceof Citizen.Logged) || (citizen instanceof Citizen.Requeue);
    }

}
