package org.toilelibre.libe.athg2sms.settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.toilelibre.libe.athg2sms.bp.ConvertThread;
import org.toilelibre.libe.athg2sms.bp.GuesserThread;
import org.toilelibre.libe.athg2sms.pattern.MakePatterns;

public class Settings {


    
    @Deprecated
    private static String                           delimiter   = "\n";

    private static Map<String, List<String>>        varNames    = new HashMap<String, List<String>> ();

    private static ConvertThread                    thread      = null;

    private static Map<String, Map<String, String>> sets        = new HashMap<String, Map<String, String>> ();

    static {
        DefaultSettings.load (Settings.sets, Settings.varNames);
    }

    public static ConvertThread getConvertThreadInstance () {
        Settings.thread = getConvertThread ();
        return Settings.thread;

    }

    public static String getDelimiter () {
        return Settings.delimiter;
    }

    public static List<String> getVarNames (final String convSet) {
        return Settings.varNames.get (convSet);
    }

    public static Map<String, String> getSet (final String set) {
        return Settings.sets.get (set);
    }

    public static Map<String, Map<String, String>> getSets () {
        return Settings.sets;
    }

    public static Set<String> getSetsKeySet () {
        return Settings.sets.keySet ();
    }

    public static PreparedPattern preparePattern (String key) {
        PreparedPattern preparedPattern = new PreparedPattern (new HashMap<String, String> (), 
                new HashMap<String, String> ());
        MakePatterns.doAll (Settings.sets.get (key), 
                preparedPattern.getPattern (), preparedPattern.getValPattern ());
        return preparedPattern;
    }

    public static void putSet (final String setName, final Map<String, String> data) {
        Settings.sets.put (setName, data);
    }
    public static ConvertThread getConvertThread () {
        return new ConvertThread ();
    }
    public static GuesserThread getGuesserThread () {
        return new GuesserThread ();
    }

}
