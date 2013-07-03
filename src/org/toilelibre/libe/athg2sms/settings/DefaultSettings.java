package org.toilelibre.libe.athg2sms.settings;

import java.util.HashMap;
import java.util.Map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DefaultSettings {

    private static SharedPreferences sp;

    public static void load (Map<String, Map<String, String>> sets) {
        if (DefaultSettings.sp == null
                || DefaultSettings.sp.getAll ().size () == 0) {
            DefaultSettings.loadDefaults (sets);
        } else {
            DefaultSettings.loadFromSettings (sets);
        }

    }

    public static void loadDefaults (Map<String, Map<String, String>> sets) {
        Map<String, String> nokiaCsv = new HashMap<String, String> ();
        nokiaCsv
                .put (
                        "inbox",
                        "sms;deliver;\"$(address)\";\"\";\"\";\"$(dateyyyy.MM.dd hh:mm)\";\"\";\"$(body)\"\n");
        nokiaCsv
                .put (
                        "sent",
                        "sms;submit;\"\";\"$(address)\";\"\";\"$(dateyyyy.MM.dd hh:mm)\";\"\";\"$(body)\"\n");
        sets.put ("Nokia Csv", nokiaCsv);
        Map<String, String> iPhoneCsv = new HashMap<String, String> ();
        iPhoneCsv.put (
                "inbox",
                "\"Received\",\"$(dateM/d/yy)\",\"$(dateh:mm a)\",\"$(address)\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"$(body)\",\"[^\"]*\"\n");
        iPhoneCsv.put (
                "sent",
                "\"Sent\",\"$(dateM/d/yy)\",\"$(dateh:mm a)\",\"$(address)\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"$(body)\",\"[^\"]*\"\n");
        sets.put ("iPhone Csv", iPhoneCsv);
        Map<String, String> blackberryCsv = new HashMap<String, String> ();
        blackberryCsv.put (
                "inbox",
                "[^,]*,[^,]*,$(dateEEE MMM d HH:mm:ss zzz yyyy),false,$(address),\"$(body)\"\n");
        blackberryCsv.put (
                "sent",
                "[^,]*,$(dateEEE MMM d HH:mm:ss zzz yyyy),[^,]*,true,$(address),\"$(body)\"\n");
        sets.put ("Blackberry Csv", blackberryCsv);
    }

    private static void loadFromSettings (Map<String, Map<String, String>> sets) {
        Map<String, ?> map = DefaultSettings.sp.getAll ();
        for (String key : map.keySet ()) {
            String [] location = key.split ("#");
            if (sets.get (location [0]) == null) {
                sets.put (location [0], new HashMap<String, String> ());
            }
            sets.get (location [0]).put (location [1], (String) map.get (key));
        }

    }

    public static void save (Map<String, Map<String, String>> sets) {
        Editor editor = DefaultSettings.sp.edit ();
        editor.clear ();
        for (String key1 : sets.keySet ()) {
            Map<String, String> map = sets.get (key1);
            for (String key2 : map.keySet ()) {
                editor.putString (key1 + "#" + key2, map.get (key2));
            }
        }
        editor.commit ();
    }

    public static void setSp (SharedPreferences sp) {
        DefaultSettings.sp = sp;
    }

}
