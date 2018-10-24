package city;

import akka.japi.function.Predicate;
import city.events.EventHandler.AdulthoodHandler;
import city.events.EventHandler.BirthHandler;
import city.events.EventHandler.DeathHandler;
import city.events.EventHandler.PartnershipHandler;

class FlowPredicates {

    static Predicate<Citizen> ifCitizenNotBornYet() {
        return c -> !(BirthHandler.filter(c));
    }

    static Predicate<Citizen> ifCitizenDied() {
        return DeathHandler::filter;
    }

    static Predicate<Citizen> ifCitizenAdultAlready() {
        return AdulthoodHandler::filter;
    }

    static Predicate<Partnership> ifPartnersUnbornYet() {
        return p -> !(BirthHandler.filter(p.c1) && BirthHandler.filter(p.c2));
    }

    static Predicate<Partnership> ifPartnersDied() {
        return p -> (DeathHandler.filter(p.c1) || DeathHandler.filter(p.c2));
    }

    static Predicate<Partnership> ifPartnersNotAdultYet() {
        return p -> !(AdulthoodHandler.filter(p.c1) && AdulthoodHandler.filter(p.c2));
    }

    static Predicate<Partnership> ifNotPartnersYet() {
        return p -> !(PartnershipHandler.filter(p));
    }

}
