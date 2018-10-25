package city.events;

import city.Citizen;
import city.Couple;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public final class EventHandler {

    private static final CitizenEventMap citizenEventMap = new CitizenEventMap();

    public static class Metrics {

        static final AtomicInteger inhabitants = new AtomicInteger(0);
        static final AtomicInteger adults = new AtomicInteger(0);
        static final AtomicInteger partners = new AtomicInteger(0);

        public static LinkedList<Event> inhabitant(String id) {
            return citizenEventMap.get(id);
        }

        public static int inhabitants() {
            return inhabitants.get();
        }

        public static int adults() {
            return adults.get();
        }

        public static int partners() {
            return partners.get();
        }

    }

    public static class BirthHandler {

        public static void sink(Citizen citizen) {
            citizenEventMap.born(citizen);
            Metrics.inhabitants.incrementAndGet();
            System.out.println("SINK:" + citizen.rawMessage);
        }

        public static boolean filter(Citizen citizen) {
            return citizenEventMap.isBorn(citizen);
        }
    }

    public static class AdulthoodHandler {

        public static void sink(Citizen citizen) {
            citizenEventMap.adulthood(citizen);
            Metrics.adults.incrementAndGet();
            System.out.println("SINK:" + citizen.rawMessage);
        }

        public static boolean filter(Citizen citizen) {
            return citizenEventMap.isAdult(citizen);
        }

    }

    public static class PartnershipHandler {

        public static void sink(Couple couple) {
            if (citizenEventMap.isChangingPartner(couple.c1, couple.c2)) {
                citizenEventMap.endPartnershipOf(couple.c1);
                Metrics.partners.decrementAndGet();
            }
            if (citizenEventMap.isChangingPartner(couple.c2, couple.c1)) {
                citizenEventMap.endPartnershipOf(couple.c2);
                Metrics.partners.decrementAndGet();
            }
            citizenEventMap.newPartnershipOf(couple.rawMessage, couple.c1, couple.c2);
            Metrics.partners.incrementAndGet();
            System.out.println("SINK:" + couple.rawMessage);
        }

        public static boolean filter(Couple couple) {
            return citizenEventMap.arePartners(couple.c1, couple.c2);
        }
    }

    public static class ChildrenHandler {

        public static void sink(Couple couple) {
            citizenEventMap.haveChildren(couple.rawMessage, couple.c1, couple.c2);
            System.out.println("SINK:" + couple.rawMessage);
        }
    }

    public static class DeathHandler {

        public static boolean filter(Citizen citizen) {
            return citizenEventMap.isDead(citizen);
        }

        public static void sink(Citizen citizen) {
            if (citizenEventMap.isAdult(citizen)) {
                Metrics.adults.decrementAndGet();
            }
            if (citizenEventMap.hasPartnership(citizen)) {
                Metrics.partners.decrementAndGet();
            }
            citizenEventMap.died(citizen);
            Metrics.inhabitants.decrementAndGet();
            System.out.println("SINK:" + citizen.rawMessage);
        }
    }

    public static class EducationHandler {
        public static void sink(Citizen citizen) {
            citizenEventMap.education(citizen);
            System.out.println("SINK:" + citizen.rawMessage);
        }
    }

}
