package org.gproman.model.everest;

import org.gproman.model.PersistentEntity;

public class ForumTopic extends PersistentEntity {

    public static enum TopicType {
        TELEMETRY, SETUP, TEST;
    }

    private TopicType type;
    private Integer   season;
    private Integer   race;
    private String    url;

    public ForumTopic() {
        super();
    }

    public ForumTopic(Integer id) {
        super(id);
    }

    public ForumTopic(TopicType type, Integer season, Integer race, String url) {
        super();
        this.type = type;
        this.season = season;
        this.race = race;
        this.url = url;
    }

    public TopicType getType() {
        return type;
    }

    public void setType(TopicType type) {
        this.type = type;
    }

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public Integer getRace() {
        return race;
    }

    public void setRace(Integer race) {
        this.race = race;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((race == null) ? 0 : race.hashCode());
        result = prime * result + ((season == null) ? 0 : season.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ForumTopic other = (ForumTopic) obj;
        if (race == null) {
            if (other.race != null)
                return false;
        } else if (!race.equals(other.race))
            return false;
        if (season == null) {
            if (other.season != null)
                return false;
        } else if (!season.equals(other.season))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ForumTopic [type=" + type + ", season=" + season + ", race=" + race + ", url=" + url + "]";
    }

}
