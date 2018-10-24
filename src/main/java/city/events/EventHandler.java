package city.events;

import city.Citizen;
import city.Couple;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public final class EventHandler {

    private static final CitizenEventMap map = new CitizenEventMap();
    private static final AtomicInteger inhabitants = new AtomicInteger(0);
    private static final AtomicInteger adults = new AtomicInteger(0);
    private static final AtomicInteger partners = new AtomicInteger(0);

    private EventHandler() {
    }

    public static LinkedList<Event> inhabitant(String id) {
        return map.get(id);
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

    public static class BirthHandler {

        public static void sink(Citizen citizen) {
            map.put(citizen, new Event.Birth());
            inhabitants.incrementAndGet();
            System.out.println("SINK:" + citizen.rawMessage);
        }

        public static boolean filter(Citizen citizen) {
            return map.contains(citizen, new Event.Birth());
        }
    }

    public static class AdulthoodHandler {

        public static void sink(Citizen citizen) {
            map.put(citizen, new Event.Adulthood());
            adults.incrementAndGet();
            System.out.println("SINK:" + citizen.rawMessage);
        }

        public static boolean filter(Citizen citizen) {
            return map.contains(citizen, new Event.Adulthood());
        }

    }

    public static class PartnershipHandler {

        public static void sink(Couple couple) {
            if (map.isChangingPartner(couple.c1, couple.c2.name)) {
                map.divorce(couple.c1);
                partners.decrementAndGet();
            }
            if (map.isChangingPartner(couple.c2, couple.c1.name)) {
                map.divorce(couple.c2);
                partners.decrementAndGet();
            }
            partners.incrementAndGet();
            map.put(couple.c1, new Event.Partner(couple.rawMessage, couple.c2.name));
            map.put(couple.c2, new Event.Partner(couple.rawMessage, couple.c1.name));
            System.out.println("SINK:" + couple.rawMessage);
        }

        public static boolean filter(Couple couple) {
            final Citizen c1 = couple.c1;
            final Citizen c2 = couple.c2;
            return map.isCurrentPartner(c1, couple.c2.name)
                    && map.isCurrentPartner(c2, couple.c1.name);
        }
    }

    public static class ChildrenHandler {

        public static void sink(Couple couple) {
            map.put(couple.c1, new Event.Children(couple.rawMessage));
            map.put(couple.c2, new Event.Children(couple.rawMessage));
            System.out.println("SINK:" + couple.rawMessage);
        }
    }

    public static class DeathHandler {

        public static boolean filter(Citizen citizen) {
            return map.contains(citizen, new Event.Death());
        }

        public static void sink(Citizen citizen) {
            // if adult,
//            adults.decrementAndGet();
            // if partner,
//            partners.decrementAndGet();

            map.put(citizen, new Event.Death());
            inhabitants.decrementAndGet();
            System.out.println("SINK:" + citizen.rawMessage);
        }
    }

    public static class EducationHandler {
        public static void sink(Citizen citizen) {
            map.put(citizen, new Event.Education());
            System.out.println("SINK:" + citizen.rawMessage);
        }
    }

}
