package city.events;

import city.Citizen;
import city.Partnership;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Events {

    public static class Birth {
        private final static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        public static Citizen sink(Citizen c) {
            set.add(c.name);
            System.out.println("SINK:" + c.rawMessage);
            return c;
        }

        public static boolean filter(Citizen citizen) {
            return set.contains(citizen.name);
        }
    }

    public static class Adulthood {
        private final static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        public static Citizen sink(Citizen c) {
            set.add(c.name);
            System.out.println("SINK:" + c.rawMessage);
            return c;
        }

        public static boolean filter(Citizen citizen) {
            return set.contains(citizen.name);
        }

    }

    public static class Partner {
        private final static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        public static Partnership sink(Partnership partnership) {
            set.add(partnership.rawMessage);
            System.out.println("SINK:" + partnership.rawMessage);

            return partnership;
        }

        public static boolean filter(Partnership partnership) {
            return set.contains(partnership.rawMessage);
        }
    }

    public static class Children {
        private final static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        public static Citizen sink(Citizen citizen) {
            set.add(citizen.rawMessage);
            return citizen;
        }

        public static boolean filter(Citizen citizen) {
            return true;
        }
    }

    public static class Death {
        private static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        public static boolean filter(Citizen citizen) {
            return set.contains(citizen.name);
        }

        public static void sink(Citizen citizen) {
            set.add(citizen.name);
            System.out.println("SINK:" + citizen.rawMessage);
        }
    }

    public static class Education {
        private final static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        public static Citizen sink(Citizen c) {
            set.add(c.name);
            System.out.println("SINK:" + c.rawMessage);
            return c;
        }

        public static boolean filter(Citizen c) {
            return set.contains(c.name);
        }
    }
}
