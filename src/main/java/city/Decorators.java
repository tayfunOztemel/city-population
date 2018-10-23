package city;

import akka.japi.function.Function;
import city.events.EventHandler;
import city.events.EventHandler.BirthHandler;
import city.events.EventHandler.EducationHandler;

class Decorators {

    static Function<Citizen, Citizen> toBeLogged_IfDied() {
        return c -> EventHandler.DeathHandler.filter(c) ? new Citizen.Logged(c) : c;
    }

    static Function<Citizen, Citizen> toBeLogged_IfBorn() {
        return c -> BirthHandler.filter(c) ? new Citizen.Logged(c) : c;
    }

    static Function<Citizen, Citizen> toBeLogged_IfAdult() {
        return c -> EventHandler.AdulthoodHandler.filter(c) ? new Citizen.Logged(c) : c;
    }

    static Function<Citizen, Citizen> toBeLogged_IfEducated() {
        return c -> EducationHandler.filter(c) ? new Citizen.Logged(c) : c;
    }

    static Function<Citizen, Citizen> toBeRequeued_IfUnbornYet() {
        return c -> BirthHandler.filter(c) ? c : new Citizen.Requeue(c);
    }

    static Function<Partnership, Partnership> toBeLogged_IfPartnersDied() {
        return p -> {
            if (EventHandler.DeathHandler.filter(p.c1) || EventHandler.DeathHandler.filter(p.c2)) {
                final Citizen.Logged c1 = new Citizen.Logged(p.c1);
                final Citizen.Logged c2 = new Citizen.Logged(p.c2);
                return new Partnership(c1, c2, p.rawMessage);
            }
            return p;
        };
    }

    static Function<Partnership, Partnership> toBeRequeued_IfPartnersNotAdultYet() {
        return p -> {
            if (EventHandler.AdulthoodHandler.filter(p.c1) && EventHandler.AdulthoodHandler.filter(p.c2)) {
                return p;
            }
            Citizen c1 = new Citizen.Requeue(p.c1);
            Citizen c2 = new Citizen.Requeue(p.c2);
            return new Partnership(c1, c2, p.rawMessage);
        };
    }

    static Function<Partnership, Partnership> toBeRequeued_IfNotPartnersYet() {
        return p -> {
            if (EventHandler.PartnershipHandler.filter(p)) {
                return p;
            }
            final Citizen.Requeue c1 = new Citizen.Requeue(p.c1);
            final Citizen.Requeue c2 = new Citizen.Requeue(p.c2);
            return new Partnership(c1, c2, p.rawMessage);

        };
    }

    static Function<Partnership, Partnership> toBeDropped_IfPartners() {
        return p -> {
            if (EventHandler.PartnershipHandler.filter(p)) {
                final Citizen.Logged c1 = new Citizen.Logged(p.c1);
                final Citizen.Logged c2 = new Citizen.Logged(p.c2);
                return new Partnership(c1, c2, p.rawMessage);
            }
            return p;
        };
    }

    static Function<Partnership, Partnership> toBeRequeued_IfPartnersUnbornYet() {
        return p -> {
            if (BirthHandler.filter(p.c1) && BirthHandler.filter(p.c2)) {
                return p;
            }
            final Citizen c1 = new Citizen.Requeue(p.c1);
            final Citizen c2 = new Citizen.Requeue(p.c2);
            return new Partnership(c1, c2, p.rawMessage);

        };
    }

}
