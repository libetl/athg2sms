package org.toilelibre.libe.athg2sms.settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.toilelibre.libe.athg2sms.bp.ConvertThread;
import org.toilelibre.libe.athg2sms.pattern.MakePatterns;

public abstract class Settings implements SettingsV4 {

    @Deprecated
    private static String delimiter = "\n";

    private static Map<String, String> formats = new HashMap<String, String> ();
    
    private static Map<String, List<String>> varNames = new HashMap<String, List<String>> ();

    private static ConvertThread thread = null;

    private static Map<String, String> patterns = new HashMap<String, String> ();

    private static Map<String, Map<String, String>> sets = new HashMap<String, Map<String, String>> ();

    private static Map<String, String> valPatterns = new HashMap<String, String> ();

    private static List<String> varNamesForConvSet;

    static {
        DefaultSettings.load (Settings.sets, Settings.varNames);
    }

    public void chooseSet (final String set) {
        Settings.formats = Settings.sets.get (set);
        Settings.varNamesForConvSet = Settings.varNames.get (set);
    }

    public ConvertThread getConvertThreadInstance () {
        Settings.thread = this.getConvertThread ();
        return Settings.thread;

    }

    public String getDelimiter () {
        return Settings.delimiter;
    }

    public List<String> getVarNames (String convSet) {
        return Settings.varNames.get (convSet);
    }

    public List<String> getVarNamesForConvSet () {
        return Settings.varNamesForConvSet;
    }

    public String getFormat (final String key) {
        return Settings.formats.get (key);
    }

    public String getPattern (final String key) {
        return Settings.patterns.get (key);
    }

    public Set<String> getPatternsKeySet () {
        return Settings.patterns.keySet ();
    }

    public Map<String, String> getSet (final String set) {
        return Settings.sets.get (set);
    }

    public Map<String, Map<String, String>> getSets () {
        return Settings.sets;
    }

    public Set<String> getSetsKeySet () {
        return Settings.sets.keySet ();
    }

    public String getValPattern (final String key) {
        return Settings.valPatterns.get (key);
    }

    public Set<String> getValPatternsKeySet () {
        return Settings.valPatterns.keySet ();
    }

    public void makePatterns () {
        MakePatterns.doAll (Settings.formats, Settings.patterns, Settings.valPatterns);
    }

    public void putSet (final String setName, final Map<String, String> data) {
        Settings.sets.put (setName, data);
    }

}
