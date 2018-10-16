package city;

import akka.NotUsed;
import akka.stream.Attributes;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Graphs {

    public final static Sink<Citizen, NotUsed> birth = Flow.of(Citizen.class)
            .filterNot(Birth::filter)
            .filterNot(Death::filter)
            .to(Sink.foreach(Birth::sink));

    public final static Sink<Citizen, NotUsed> death = Flow.of(Citizen.class)
            .filter(Birth::filter)
            .filterNot(Death::filter)
            .to(Sink.foreach(Death::sink));

    public final static Sink<Citizen, NotUsed> education = Flow.of(Citizen.class)
            .filter(Birth::filter)
            .filterNot(Death::filter)
            .to(Sink.foreach(Education::sink));

    public final static Sink<Citizen, NotUsed> adulthood = Flow.of(Citizen.class)
            .filter(Birth::filter)
            .filterNot(Death::filter)
            .to(Sink.foreach(Adulthood::sink));

    public final static Sink<Citizen, NotUsed> partner = Flow.of(Citizen.class)
            .filter(Birth::filter)
            .filterNot(Death::filter)
            .filter(Adulthood::filter)
            .to(Sink.foreach(Partner::sink));

    public final static Sink<Citizen, NotUsed> children = Flow.of(Citizen.class)
            .filter(Birth::filter)
            .filterNot(Death::filter)
            .filter(Adulthood::filter)
            .filter(Partner::filter)
            .to(Sink.foreach(Children::sink));

    public final static Sink<Citizen, NotUsed> logged = Flow.of(Citizen.class)
            .filter(Birth::filter)
            .filterNot(Death::filter)
            .log("Ignored:", citizen -> citizen.rawMessage)
            .addAttributes(Attributes.createLogLevels(
                    Attributes.logLevelOff(), // onElement
                    Attributes.logLevelError(), // onFailure
                    Attributes.logLevelDebug())) // onFinish
            .to(Sink.ignore());

    static class Birth {
        private final static Map<String, String> map = new ConcurrentHashMap<>();

        static Citizen sink(Citizen c) {
            map.put(c.name, c.event);
            return c;
        }

        static boolean filter(Citizen citizen) {
            final boolean alreadyBorn = map.keySet().contains(citizen.name);
            if (alreadyBorn && citizen.rawMessage.startsWith("Birth")) {
                //log
                // requeue
            }
            return alreadyBorn;
        }
    }

    static class Adulthood {
        private final static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        static Citizen sink(Citizen c) {
            set.add(c.rawMessage);
            return c;
        }

        static boolean filter(Citizen citizen) {
            return set.contains(citizen.rawMessage);
        }
    }

    static class Partner {
        private final static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        static Citizen sink(Citizen citizen) {
            set.add(citizen.rawMessage);
            return citizen;
        }

        public static boolean filter(Citizen citizen) {
            return true;
        }
    }

    static class Children {
        private final static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        static Citizen sink(Citizen citizen) {
            set.add(citizen.rawMessage);
            return citizen;
        }

        public static boolean filter(Citizen citizen) {
            return true;
        }
    }

    static class Education {
        static Citizen sink(Citizen c) {
            return c;
        }
    }

    static class Death {
        private static Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

        static boolean filter(Citizen citizen) {
            final boolean alreadyDied = set.contains(citizen.name);
            if (alreadyDied) {
                System.out.println("Already died:" + citizen.name);
            }
            return alreadyDied;
        }

        static void sink(Citizen citizen) {
            set.add(citizen.name);
        }
    }
}
