package city.events;

import city.Citizen;

class Event {

    static final class Birth extends Event {
        public String toString() {
            return "Birth";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Birth;
        }

        @Override
        public int hashCode() {
            return "Birth".hashCode();
        }
    }

    static final class Death extends Event {
        public String toString() {
            return "Death";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Death;
        }

        @Override
        public int hashCode() {
            return "Death".hashCode();
        }
    }

    static final class Adulthood extends Event {
        public String toString() {
            return "Adulthood";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Adulthood;
        }

        @Override
        public int hashCode() {
            return "Adulthood".hashCode();
        }
    }

    static final class Education extends Event {
        public String toString() {
            return "Education";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Education;
        }

        @Override
        public int hashCode() {
            return "Education".hashCode();
        }
    }

    static final class Partner extends Event {
        private String rawEvent;
        private Citizen partner;

        Partner(String s, Citizen partner) {
            this.rawEvent = s;
            this.partner = partner;
        }

        Citizen getPartner() {
            return partner;
        }

        public String toString() {
            return rawEvent;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Partner
                    && rawEvent.equals(((Partner) obj).rawEvent);
        }

        @Override
        public int hashCode() {
            return rawEvent.hashCode();
        }

    }

    static final class Children extends Event {
        private String rawEvent;

        Children(String s) {
            this.rawEvent = s;
        }

        public String toString() {
            return rawEvent;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Children
                    && rawEvent.equals(((Children) obj).rawEvent);
        }

        @Override
        public int hashCode() {
            return rawEvent.hashCode();
        }
    }


}
