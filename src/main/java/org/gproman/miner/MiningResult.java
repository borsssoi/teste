package org.gproman.miner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.gproman.model.everest.NormalizedRace.RaceStatus;


public class MiningResult {
    private int posts = 0;
    private int duplicates = 0;
    private int errors = 0;
    private String topic = "";
    private Map<String, Map<String, List<ParsingResult>>> results = new TreeMap<String, Map<String, List<ParsingResult>>>();
    
    public int getPosts() {
        return posts;
    }
    
    public void setPosts(int posts) {
        this.posts = posts;
    }
    
    public Map<String, Map<String, List<ParsingResult>>> getResults() {
        return results;
    }
    
    public void setResults(Map<String, Map<String, List<ParsingResult>>> results) {
        this.results = results;
    }
    
    public int getDuplicates() {
        return duplicates;
    }
    
    public void setDuplicates(int duplicates) {
        this.duplicates = duplicates;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void addContent(String tool, String name, ParsingResult result) {
        Map<String, List<ParsingResult>> map = results.get(tool);
        if (map == null) {
            map = new HashMap<String, List<ParsingResult>>();
            results.put(tool, map);
        }
        List<ParsingResult> list = map.get(name);
        if( list == null ) {
            list = new ArrayList<ParsingResult>();
            map.put(name, list);
        }
        list.add(result);
        if( ! result.getErrors().isEmpty() || result.getRace().getRaceStatus() == RaceStatus.ERROR ) {
            errors++;
        } else if( list.size() > 1 ) {
            duplicates++;
        }
        posts++;
    }
    
    public List<ParsingResult> getSuccessfulResults() {
        List<ParsingResult> successful = new ArrayList<ParsingResult>();
        for( Map<String, List<ParsingResult>> managers : results.values() ) {
            for( List<ParsingResult> parsings : managers.values() ) {
                for( ParsingResult r : parsings ) {
                    if( ! r.hasErrors() && r.getRace().getRaceStatus() != RaceStatus.ERROR ) {
                        successful.add(r);
                        break;
                    }
                }
            }
        }
        return successful;
    }
    
    public Set<String> getMemberNames() {
        Set<String> members = new HashSet<String>();
        for( Map<String, List<ParsingResult>> managers : results.values() ) {
            members.addAll(managers.keySet());
        }
        return members;
    }
    
    public String getReport() {
        return topic+" ("+posts+" posts, "+(posts-duplicates-errors)+" successful, "+duplicates+" duplicates, "+errors+" errors)";
    }
    
    public String getReportBr() {
        return "     - "+topic+"\n"
                + "          . "+posts+" telemetrias\n"
                + "          . "+(posts-duplicates-errors)+" com sucesso\n"
                + "          . "+duplicates+" duplicadas\n"
                + "          . "+errors+" com erro";
    }
    
    @Override
    public String toString() {
        Map<String,List<ParsingResult>> duplicatesList = new HashMap<String, List<ParsingResult>>();
        String result = getReport()+"\n";
        for( Map.Entry<String, Map<String, List<ParsingResult>>> entry : results.entrySet() ) {
            result += "    "+entry.getKey()+" ==> "+entry.getValue().size()+"\n";
            for( Map.Entry<String, List<ParsingResult>> e2 : entry.getValue().entrySet() ) {
                if( e2.getValue().size() > 1 ) {
                    duplicatesList.put( e2.getKey(), e2.getValue() );
                }
            }
        }
        result += "    DUPLICATES:\n";
        for( Map.Entry<String, List<ParsingResult>> entry : duplicatesList.entrySet() ) {
            result += "        "+entry.getKey()+" ==> "+entry.getValue().size()+" \n";
        }
        return result;
    }

}
