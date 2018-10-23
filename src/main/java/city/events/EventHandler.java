package city.events;

import city.Citizen;
import city.Partnership;

public final class EventHandler {

    private static final CitizenEventMap map = new CitizenEventMap();

    private EventHandler() {
    }

    public static class BirthHandler {

        public static void sink(Citizen citizen) {
            map.put(citizen, Event.BIRTH);
            System.out.println("SINK:" + citizen.rawMessage);

        }

        public static boolean filter(Citizen citizen) {
            return map.contains(citizen, Event.BIRTH);
        }
    }

    public static class AdulthoodHandler {

        public static void sink(Citizen citizen) {
            map.put(citizen, Event.ADULTHOOD);
            System.out.println("SINK:" + citizen.rawMessage);
        }

        public static boolean filter(Citizen citizen) {
            return map.contains(citizen, Event.ADULTHOOD);
        }

    }

    public static class PartnershipHandler {

        public static void sink(Partnership partnership) {
            map.put(partnership.c1, partnership.c2.name);
            map.put(partnership.c2, partnership.c1.name);
            System.out.println("SINK:" + partnership.rawMessage);
        }

        public static boolean filter(Partnership partnership) {
            final Citizen c1 = partnership.c1;
            final Citizen c2 = partnership.c2;
            return map.containsLast(c1, c2.name) && map.containsLast(c2, c1.name);
        }
    }

    public static class ChildrenHandler {

        public static void sink(Partnership partnership) {
            map.put(partnership.c1, partnership.c2.name);
            map.put(partnership.c2, partnership.c1.name);
            System.out.println("SINK:" + partnership.rawMessage);
        }
    }

    public static class DeathHandler {

        public static boolean filter(Citizen citizen) {
            return map.contains(citizen, Event.DEATH);
        }

        public static void sink(Citizen citizen) {
            map.put(citizen, Event.DEATH);
            System.out.println("SINK:" + citizen.rawMessage);
        }
    }

    public static class EducationHandler {
        public static void sink(Citizen citizen) {
            map.put(citizen, Event.EDUCATION);
            System.out.println("SINK:" + citizen.rawMessage);
        }
    }

}
