package org.toilelibre.libe.athg2sms.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.toilelibre.libe.athg2sms.util.StringUtils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DefaultSettings {
    
    public static final String SENT = "sent";
    public static final String INBOX = "inbox";
    public static final String COMMON = "common";
    public static final String INBOX_KEYWORD = "inboxKeyword";
    public static final String SENT_KEYWORD = "sentKeyword";
    public static final String INDEX_OF_FOLDER_CAPTURING_GROUP = "indexOfFolderCapturingGroup";

    public enum BuiltInConversionSets {
        NokiaCsv("Nokia Csv"), 
        IPhoneCsv("iPhone Csv"),
        BlackberryCsv("Blackberry Csv"),
        DateAndFromAndAddressAndbody("Date+'from'+address+body"),
        DateAndAddressAndBodyAndINBOX("Date+address+body+INBOX");
        
        private String value;

        BuiltInConversionSets (String value) {
            this.value = value;
        }
        
        public String getValue () {
            return this.value;
        }
    }

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
        insertConversionSet (BuiltInConversionSets.NokiaCsv, 
                sets, "[\r\n\t]*sms;deliver;\"$(address)\";\"\";\"\";\"$(dateyyyy.MM.dd hh:mm)\";\"\";\"$(body)\"[\r\n\t]+",
                "[\r\n\t]*sms;submit;\"\";\"$(address)\";\"\";\"$(dateyyyy.MM.dd hh:mm)\";\"\";\"$(body)\"[\r\n\t]+");
        insertConversionSet (BuiltInConversionSets.IPhoneCsv, 
                sets, "[\r\n\t]*\"Received\",\"$(dateM/d/yy)\",\"$(dateh:mm a)\",\"$(address)\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"$(body)\",\"[^\"]*\"[\r\n\t]+",
                "[\r\n\t]*\"Sent\",\"$(dateM/d/yy)\",\"$(dateh:mm a)\",\"$(address)\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"$(body)\",\"[^\"]*\"[\r\n\t]+");
        insertConversionSet (BuiltInConversionSets.BlackberryCsv, 
                sets, "[\r\n\t]*[^,]*,[^,]*,$(dateEEE MMM d HH:mm:ss zzz yyyy),false,$(address),\"$(body)\"[\r\n\t]+",
                "[\r\n\t]*[^,]*,$(dateEEE MMM d HH:mm:ss zzz yyyy),[^,]*,true,$(address),\"$(body)\"[\r\n\t]+");
        insertConversionSet (BuiltInConversionSets.DateAndFromAndAddressAndbody, 
                sets, "[\r\n\t]*$(dateM/d/yy HH:mm:ss a);from;$(address);\"\";\"$(body)\"[\r\n\t]+",
                "[\r\n\t]*$(dateM/d/yy HH:mm:ss a);to;$(address);\"\";\"$(body)\"[\r\n\t]+");
        insertConversionSet (BuiltInConversionSets.DateAndAddressAndBodyAndINBOX, 
                sets, "[\r\n\t]*\"$(dateyy-M-d HH:mm:ss)\",\"$(address)\",\"\",\"$(body)\",\"INBOX\"[\r\n\t]+",
                "[\r\n\t]*\"$(dateyy-M-d HH:mm:ss)\",\"$(address)\",\"\",\"$(body)\",\"SENT\"[\r\n\t]+");
    }

    private static void insertConversionSet (BuiltInConversionSets conversionSetName, Map<String, Map<String, String>> sets, String inboxValue, String sentValue) {
        int[] ranges = StringUtils.commonPartRanges (inboxValue, sentValue);
        final Map<String, String> subSet = new HashMap<String, String> (5);
        subSet.put (INBOX, inboxValue);
        subSet.put (SENT, sentValue);
        subSet.put (COMMON, inboxValue.substring (0, ranges [0]) + "$(folder)" + inboxValue.substring (ranges [1]));    
        subSet.put (INBOX_KEYWORD, inboxValue.substring (ranges [0], ranges [1]));
        subSet.put (SENT_KEYWORD, sentValue.substring (ranges [2], ranges [3]));
        subSet.put (SENT_KEYWORD, sentValue.substring (ranges [2], ranges [3]));
        
        subSet.put (INDEX_OF_FOLDER_CAPTURING_GROUP, "" + numberOfMatches (Pattern.compile ("\\$\\([^)]+\\)").matcher (inboxValue.substring (0, ranges [0]))));
        
        sets.put (conversionSetName.getValue (), subSet);
    }

    private static int numberOfMatches (Matcher matcher) {
        int count = 1;
        while (matcher.find()) {
            count++;
        }
        return count;
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
