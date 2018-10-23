package city.events;

import city.Citizen;

interface Event {

    String name();

    class Death implements Event {

        public String name() {
            return "Death";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Death
                    && ((Death) obj).name().equals(name());
        }

        @Override
        public int hashCode() {
            return name().hashCode();
        }
    }

    class Birth implements Event {

        public String name() {
            return "Birth";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Birth
                    && ((Birth) obj).name().equals(name());
        }

        @Override
        public int hashCode() {
            return name().hashCode();
        }
    }

    class Education implements Event {
        public String name() {
            return "Education";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Education
                    && ((Education) obj).name().equals(name());
        }

        @Override
        public int hashCode() {
            return name().hashCode();
        }
    }

    class Adulthood implements Event {
        public String name() {
            return "Adulthood";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Adulthood
                    && ((Adulthood) obj).name().equals(name());
        }

        @Override
        public int hashCode() {
            return name().hashCode();
        }
    }

    class Partner implements Event {
        private Citizen partner;

        Partner(Citizen c2) {
            partner = c2;
        }

        @Override
        public String name() {
            return "Partner";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Partner
                    && ((Partner) obj).name().equals(name())
                    && ((Partner) obj).partner.equals(partner);
        }

        @Override
        public int hashCode() {
            return name().hashCode();
        }
    }

    class Children implements Event {

        private Citizen partner;

        Children(Citizen c2) {
            partner = c2;
        }

        @Override
        public String name() {
            return "Children";
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Children
                    && ((Children) obj).name().equals(name())
                    && ((Children) obj).partner.equals(partner);
        }

        @Override
        public int hashCode() {
            return name().hashCode();
        }
    }
}
