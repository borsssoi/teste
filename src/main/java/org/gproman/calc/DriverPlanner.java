package org.gproman.calc;

import java.util.ArrayList;
import java.util.List;

public class DriverPlanner {

    private List<DriverPlanStep> steps = new ArrayList<DriverPlanStep>();

    public static class DriverPlanStep {
        // 0 = start; 1 = finish;
        private DriverAttributes[] attrs = new DriverAttributes[2];
        private DriverEvent        race;
        private DriverEvent        test;
        private DriverEvent        training;
        private DriverEvent        reset;
    }

    public static class DriverAttributes {
        private Range overall;
        private Range concentration;
        private Range talent;
        private Range aggressiveness;
        private Range experience;
        private Range techInsight;
        private Range stamina;
        private Range charisma;
        private Range motivation;
        private Range weight;
        private int   age;

        public DriverAttributes(Range overall,
                                 Range concentration,
                                 Range talent,
                                 Range aggressiveness,
                                 Range experience,
                                 Range techInsight,
                                 Range stamina,
                                 Range charisma,
                                 Range motivation,
                                 Range weight,
                                 int age) {
            super();
            this.overall = overall;
            this.concentration = concentration;
            this.talent = talent;
            this.aggressiveness = aggressiveness;
            this.experience = experience;
            this.techInsight = techInsight;
            this.stamina = stamina;
            this.charisma = charisma;
            this.motivation = motivation;
            this.weight = weight;
            this.age = age;
        }

        public Range getOverall() {
            return overall;
        }

        public void setOverall(Range overall) {
            this.overall = overall;
        }

        public Range getConcentration() {
            return concentration;
        }

        public void setConcentration(Range concentration) {
            this.concentration = concentration;
        }

        public Range getTalent() {
            return talent;
        }

        public void setTalent(Range talent) {
            this.talent = talent;
        }

        public Range getAggressiveness() {
            return aggressiveness;
        }

        public void setAggressiveness(Range aggressiveness) {
            this.aggressiveness = aggressiveness;
        }

        public Range getExperience() {
            return experience;
        }

        public void setExperience(Range experience) {
            this.experience = experience;
        }

        public Range getTechInsight() {
            return techInsight;
        }

        public void setTechInsight(Range techInsight) {
            this.techInsight = techInsight;
        }

        public Range getStamina() {
            return stamina;
        }

        public void setStamina(Range stamina) {
            this.stamina = stamina;
        }

        public Range getCharisma() {
            return charisma;
        }

        public void setCharisma(Range charisma) {
            this.charisma = charisma;
        }

        public Range getMotivation() {
            return motivation;
        }

        public void setMotivation(Range motivation) {
            this.motivation = motivation;
        }

        public Range getWeight() {
            return weight;
        }

        public void setWeight(Range weight) {
            this.weight = weight;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static class Range {
        public static final Range ZERO = new Range( 0, 0 );
        public static Range range(double val) {
            return new Range( val, val );
        }

        public static Range range(double min,
                                  double max) {
            return new Range( min, max );
        }

        private double min;
        private double max;

        public Range(double min,
                     double max) {
            super();
            this.min = min;
            this.max = max;
        }

        public Range(double val) {
            this( val, val );
        }

        public Range() {
            this( 0, 0 );
        }

        public double getMin() {
            return min;
        }

        public void setMin(double min) {
            this.min = min;
        }

        public double getMax() {
            return max;
        }

        public void setMax(double max) {
            this.max = max;
        }

        @Override
        public String toString() {
            return equals( min, max ) ? String.format( "%1.0f", min ) : String.format( "[ %1.0f, %1.0f ]", min, max );
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            long temp;
            temp = Double.doubleToLongBits( max );
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits( min );
            result = prime * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            Range other = (Range) obj;
            if ( Double.doubleToLongBits( max ) != Double.doubleToLongBits( other.max ) ) return false;
            if ( Double.doubleToLongBits( min ) != Double.doubleToLongBits( other.min ) ) return false;
            return true;
        }

        private boolean equals(double v1,
                               double v2) {
            return Math.abs( v1 - v2 ) < .01;
        }
    }

}
