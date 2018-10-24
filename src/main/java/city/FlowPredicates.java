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

    static Predicate<Couple> ifPartnersUnbornYet() {
        return p -> !(BirthHandler.filter(p.c1) && BirthHandler.filter(p.c2));
    }

    static Predicate<Couple> ifPartnersDied() {
        return p -> (DeathHandler.filter(p.c1) || DeathHandler.filter(p.c2));
    }

    static Predicate<Couple> ifPartnersNotAdultYet() {
        return p -> !(AdulthoodHandler.filter(p.c1) && AdulthoodHandler.filter(p.c2));
    }

    static Predicate<Couple> ifNotPartnersYet() {
        return p -> !(PartnershipHandler.filter(p));
    }

    static Predicate<Couple> ifPartnersAlready() {
        return PartnershipHandler::filter;
    }



}
