package org.toilelibre.libe.athg2sms.settings;

import java.util.HashMap;
import java.util.Map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DefaultSettings {

    private static SharedPreferences sp;

    public static String getDefaultSmsApp () {
        return DefaultSettings.sp.getString ("defaultSmsApp", "com.android.mms");
    }

    public static void load (final Map<String, Map<String, String>> sets) {
        if ( (DefaultSettings.sp == null) || (DefaultSettings.sp.getAll ().size () <= 1)) {
            DefaultSettings.loadDefaults (sets);
        } else {
            DefaultSettings.loadFromSettings (sets);
        }

    }

    public static void loadDefaults (final Map<String, Map<String, String>> sets) {
        final Map<String, String> nokiaCsv = new HashMap<String, String> ();
        nokiaCsv.put ("inbox", "[\r\n\t]*sms;deliver;\"$(address)\";\"\";\"\";\"$(dateyyyy.MM.dd hh:mm)\";\"\";\"$(body)\"[\r\n\t]+");
        nokiaCsv.put ("sent", "[\r\n\t]*sms;submit;\"\";\"$(address)\";\"\";\"$(dateyyyy.MM.dd hh:mm)\";\"\";\"$(body)\"[\r\n\t]+");
        sets.put ("Nokia Csv", nokiaCsv);
        final Map<String, String> iPhoneCsv = new HashMap<String, String> ();
        iPhoneCsv.put ("inbox", "[\r\n\t]*\"Received\",\"$(dateM/d/yy)\",\"$(dateh:mm a)\",\"$(address)\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"$(body)\",\"[^\"]*\"[\r\n\t]+");
        iPhoneCsv.put ("sent", "[\r\n\t]*\"Sent\",\"$(dateM/d/yy)\",\"$(dateh:mm a)\",\"$(address)\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"$(body)\",\"[^\"]*\"[\r\n\t]+");
        sets.put ("iPhone Csv", iPhoneCsv);
        final Map<String, String> blackberryCsv = new HashMap<String, String> ();
        blackberryCsv.put ("inbox", "[\r\n\t]*[^,]*,[^,]*,$(dateEEE MMM d HH:mm:ss zzz yyyy),false,$(address),\"$(body)\"[\r\n\t]+");
        blackberryCsv.put ("sent", "[\r\n\t]*[^,]*,$(dateEEE MMM d HH:mm:ss zzz yyyy),[^,]*,true,$(address),\"$(body)\"[\r\n\t]+");
        sets.put ("Blackberry Csv", blackberryCsv);
        final Map<String, String> additionalCsv20161 = new HashMap<String, String> ();
        additionalCsv20161.put ("inbox", "[\r\n\t]*$(dateM/d/yy HH:mm:ss a);from;$(address);\"\";\"$(body)\"[\r\n\t]+");
        additionalCsv20161.put ("sent", "[\r\n\t]*$(dateM/d/yy hh:mm:ss a);to;$(address);\"\";\"$(body)\"[\r\n\t]+");
        sets.put ("Date+'from'+address+body", additionalCsv20161);
        final Map<String, String> additionalCsv20162 = new HashMap<String, String> ();
        additionalCsv20162.put ("inbox", "[\r\n\t]*\"$(dateyy-M-d HH:mm:ss)\",\"$(address)\",\"\",\"$(body)\",\"INBOX\"[\r\n\t]+");
        additionalCsv20162.put ("sent", "[\r\n\t]*\"$(dateyy-M-d HH:mm:ss)\",\"$(address)\",\"\",\"$(body)\",\"SENT\"[\r\n\t]+");
        sets.put ("Date+address+body+INBOX", additionalCsv20162);
    }

    private static void loadFromSettings (final Map<String, Map<String, String>> sets) {
        final Map<String, ?> map = DefaultSettings.sp.getAll ();
        for (final String key : map.keySet ()) {
            final String [] location = key.split ("#");
            if (location.length > 1) {
                if (sets.get (location [0]) == null) {
                    sets.put (location [0], new HashMap<String, String> ());
                }
                sets.get (location [0]).put (location [1], (String) map.get (key));
            }
        }

    }

    public static void save (final Map<String, Map<String, String>> sets) {
        final String smsApp = DefaultSettings.getDefaultSmsApp ();
        final Editor editor = DefaultSettings.sp.edit ();
        editor.clear ();
        DefaultSettings.saveDefaultSmsApp (smsApp);
        for (final String key1 : sets.keySet ()) {
            final Map<String, String> map = sets.get (key1);
            for (final String key2 : map.keySet ()) {
                editor.putString (key1 + "#" + key2, map.get (key2));
            }
        }
        editor.commit ();
    }

    public static void saveDefaultSmsApp (final String packageName) {
        final Editor editor = DefaultSettings.sp.edit ();
        editor.putString ("defaultSmsApp", packageName);
        editor.commit ();
    }

    public static void setSp (final SharedPreferences sp) {
        DefaultSettings.sp = sp;
    }

}
