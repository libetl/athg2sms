package org.toilelibre.libe.athg2sms.settings;

import java.util.Map;

public class PreparedPattern {
    private final Map<String, String> pattern;
    private final Map<String, String> valPattern;
    
    public PreparedPattern (Map<String, String> pattern, Map<String, String> valPattern) {
        super ();
        this.pattern = pattern;
        this.valPattern = valPattern;
    }
    public Map<String, String> getPattern () {
        return pattern;
    }
    public Map<String, String> getValPattern () {
        return valPattern;
    }
    
    
}