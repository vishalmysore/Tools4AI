package com.enterprise.fly;

public class FlightDetails {
        private String date;
        private String name;
        private String fromLocation;
        private String toLocation;

        public FlightDetails() {
        }

        public FlightDetails(String date, String name, String fromLocation, String toLocation) {
            this.date = date;
            this.name = name;
            this.fromLocation = fromLocation;
            this.toLocation = toLocation;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFromLocation() {
            return fromLocation;
        }

        public void setFromLocation(String fromLocation) {
            this.fromLocation = fromLocation;
        }

        public String getToLocation() {
            return toLocation;
        }

        public void setToLocation(String toLocation) {
            this.toLocation = toLocation;
        }
    }

