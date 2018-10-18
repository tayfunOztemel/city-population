package city;

import akka.japi.function.Function;
import city.events.Events;

class Decorators {
    // Decoration utilities
    static Function<Citizen, Citizen> toBeDropped_IfDied() {
        return c -> Events.Death.filter(c) ? new Citizen.Drop(c) : c;
    }

    static Function<Citizen, Citizen> toBeDropped_IfBorn() {
        return c -> Events.Birth.filter(c) ? new Citizen.Drop(c) : c;
    }

    static Function<Citizen, Citizen> toBeDropped_IfAdult() {
        return c -> Events.Adulthood.filter(c) ? new Citizen.Drop(c) : c;
    }

    static Function<Citizen, Citizen> toBeDropped_IfEducated() {
        return c -> Events.Education.filter(c) ? new Citizen.Drop(c) : c;
    }

    static Function<Citizen, Citizen> toBeRequeued_IfUnbornYet() {
        return c -> Events.Birth.filter(c) ? c : new Citizen.Requeue(c);
    }

    static Function<Citizen, Citizen> toBeRequeued_IfNotAdultYet() {
        return c -> Events.Adulthood.filter(c) ? c : new Citizen.Requeue(c);
    }

    static boolean dropOrRequeue(Citizen citizen) {
        return (citizen instanceof Citizen.Drop) || (citizen instanceof Citizen.Requeue);
    }

}
