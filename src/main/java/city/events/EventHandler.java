package city.events;

import city.Citizen;
import city.Partnership;
import city.events.Event.*;

public final class EventHandler {

    private static final CitizenEventMap map = new CitizenEventMap();

    private EventHandler() {
    }

    public static class BirthHandler {

        public static void sink(Citizen citizen) {
            map.put(citizen, new Birth());
            System.out.println("SINK:" + citizen.rawMessage);

        }

        public static boolean filter(Citizen citizen) {
            return map.contains(citizen, new Birth());
        }
    }

    public static class AdulthoodHandler {

        public static void sink(Citizen citizen) {
            map.put(citizen, new Adulthood());
            System.out.println("SINK:" + citizen.rawMessage);
        }

        public static boolean filter(Citizen citizen) {
            return map.contains(citizen, new Adulthood());
        }

    }

    public static class PartnershipHandler {

        public static void sink(Partnership partnership) {
            map.put(partnership.c1, new Partner(partnership.c2));
            map.put(partnership.c2, new Partner(partnership.c1));
            System.out.println("SINK:" + partnership.rawMessage);
        }

        public static boolean filter(Partnership partnership) {
            final Citizen c1 = partnership.c1;
            final Citizen c2 = partnership.c2;
            return map.containsLast(c1, new Partner(c2))
                    && map.containsLast(c2, new Partner(c1));
        }
    }

    public static class ChildrenHandler {

        public static void sink(Partnership partnership) {
            map.put(partnership.c1, new Children(partnership.c2));
            map.put(partnership.c2, new Children(partnership.c1));
            System.out.println("SINK:" + partnership.rawMessage);
        }
    }

    public static class DeathHandler {

        public static boolean filter(Citizen citizen) {
            return map.contains(citizen, new Death());
        }

        public static void sink(Citizen citizen) {
            map.put(citizen, new Death());
            System.out.println("SINK:" + citizen.rawMessage);
        }
    }

    public static class EducationHandler {
        public static void sink(Citizen citizen) {
            map.put(citizen, new Education());
            System.out.println("SINK:" + citizen.rawMessage);
        }
    }

}
