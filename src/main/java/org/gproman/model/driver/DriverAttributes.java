package org.gproman.model.driver;

import java.io.Serializable;

public class DriverAttributes implements Serializable {
    private static final long serialVersionUID = 3155541126064450480L;
    
    private int overall;
    private int concentration;
    private int talent;
    private int aggressiveness;
    private int experience;
    private int techInsight;
    private int stamina;
    private int charisma;
    private int motivation;
    private int reputation;
    private int weight;
    private int age;

    public DriverAttributes() {
    }

    public DriverAttributes(int overall,
                            int concentration,
                            int talent,
                            int aggressiveness,
                            int experience,
                            int techInsight,
                            int stamina,
                            int charisma,
                            int motivation,
                            int reputation,
                            int weight,
                            int age) {
        this.overall = overall;
        this.concentration = concentration;
        this.talent = talent;
        this.aggressiveness = aggressiveness;
        this.experience = experience;
        this.techInsight = techInsight;
        this.stamina = stamina;
        this.charisma = charisma;
        this.motivation = motivation;
        this.reputation = reputation;
        this.weight = weight;
        this.age = age;
    }

    public int getOverall() {
        return overall;
    }

    public void setOverall(int overall) {
        this.overall = overall;
    }

    public int getConcentration() {
        return concentration;
    }

    public void setConcentration(int concentration) {
        this.concentration = concentration;
    }

    public int getTalent() {
        return talent;
    }

    public void setTalent(int talent) {
        this.talent = talent;
    }

    public int getAggressiveness() {
        return aggressiveness;
    }

    public void setAggressiveness(int aggressiveness) {
        this.aggressiveness = aggressiveness;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getTechInsight() {
        return techInsight;
    }

    public void setTechInsight(int techInsight) {
        this.techInsight = techInsight;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }

    public int getMotivation() {
        return motivation;
    }

    public void setMotivation(int motivation) {
        this.motivation = motivation;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getCalcOverall() {
        // @formatter:off
        return (this.concentration * 8 +
                this.talent * 12 +
                this.aggressiveness * 7 +
                this.experience * 4 +
                this.techInsight * 6 +
                this.stamina * 7 +
                this.charisma * 4 +
                this.motivation * 4 - this.weight * 4) / 48.0;
        // @formatter:on
    }

    public double getSatisfactionZone() {
        return 135.0107-(0.10172*experience)-(0.30014*techInsight);
    }

    @Override
    public String toString() {
        // @formatter:off
        return "DriverAttributes [overall=" + overall +
               ", concentration=" + concentration +
               ", talent=" + talent +
               ", aggressiveness=" + aggressiveness +
               ", experience=" + experience +
               ", techInsight=" + techInsight +
               ", stamina=" + stamina +
               ", charisma=" + charisma +
               ", motivation=" + motivation +
               ", reputation=" + reputation +
               ", weight=" + weight +
               ", age=" + age + "]";
        // @formatter:on
    }


}
