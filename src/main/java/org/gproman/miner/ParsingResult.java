package org.gproman.miner;

import java.util.HashMap;
import java.util.Map;

import org.gproman.model.everest.NormalizedRace;

public class ParsingResult {

    private String                 url;
    private NormalizedRace         race;
    private String                 content;
    private Map<String, Exception> errors = new HashMap<String, Exception>();

    public ParsingResult() {
        super();
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }

    public NormalizedRace getRace() {
        return race;
    }

    public void setRace(NormalizedRace race) {
        this.race = race;
    }

    public void addError(String msg, Exception e) {
        this.errors.put(msg, e);
    }

    public Map<String, Exception> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, Exception> errors) {
        this.errors = errors;
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    public String getContent() {
        return this.content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }

}