package org.toilelibre.libe.athg2sms.settings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DefaultSettings {

    public static final String   SENT                            = "sent";
    public static final String   INBOX                           = "inbox";
    public static final String   COMMON                          = "common";
    public static final String   INBOX_KEYWORD                   = "inboxKeyword";
    public static final String   SENT_KEYWORD                    = "sentKeyword";
    public static final String   INDEX_OF_FOLDER_CAPTURING_GROUP = "indexOfFolderCapturingGroup";
    private static final Pattern VARIABLE_PATTERN                = Pattern.compile ("\\$\\(([^)]+)\\)");

    public enum BuiltInConversionSets {
        NokiaCsv ("Nokia Csv"), IPhoneCsv ("iPhone Csv"), BlackberryCsv ("Blackberry Csv"), DateAndFromAndAddressAndbody ("Date+'from'+address+body"), DateAndAddressAndBodyAndINBOX ("Date+address+body+INBOX"), NokiaCsvWithQuotes ("Nokia Csv with quotes");

        private final String value;

        BuiltInConversionSets (final String value) {
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

    public static void load (final Map<String, Map<String, String>> sets, final Map<String, List<String>> varNames) {
        if (DefaultSettings.sp == null || DefaultSettings.sp.getAll ().size () <= 1) {
            DefaultSettings.loadDefaults (sets, varNames);
        } else {
            DefaultSettings.loadFromSettings (sets, varNames);
        }
    }

    public static void loadDefaults (final Map<String, Map<String, String>> sets, final Map<String, List<String>> varNames) {
        DefaultSettings.insertConversionSet (BuiltInConversionSets.NokiaCsv.value, sets, varNames, "[\r\n\t]*sms;$(folder);(?:\"\";)?\"$(address)\";\"\";(?:\"\";)?\"$(dateyyyy.MM.dd hh:mm)\";\"\";\"$(body)\"[\r\n\t]+", "deliver", "submit");
        DefaultSettings.insertConversionSet (BuiltInConversionSets.NokiaCsvWithQuotes.value, sets, varNames, "[\r\n\t]*\"sms\";\"$(folder)\";(?:\"\";)?\"$(address)\";\"\";(?:\"\";)?\"$(dateyyyy.MM.dd hh:mm)\";\"\";\"$(body)\"[\r\n\t]+", "deliver", "submit");
        DefaultSettings.insertConversionSet (BuiltInConversionSets.IPhoneCsv.value, sets, varNames, "[\r\n\t]*\"$(folder)\",\"$(dateM/d/yy)\",\"$(dateh:mm a)\",\"$(address)\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"$(body)\",\"[^\"]*\"[\r\n\t]+", "Received", "Sent");
        DefaultSettings.insertConversionSet (BuiltInConversionSets.BlackberryCsv.value, sets, varNames, "[\r\n\t]*[^,]*,(?:,)?$(dateEEE MMM d HH:mm:ss zzz yyyy),(?:,)?$(folder),$(address),\"$(body)\"[\r\n\t]+", "false", "true");
        DefaultSettings.insertConversionSet (BuiltInConversionSets.DateAndFromAndAddressAndbody.value, sets, varNames, "[\r\n\t]*$(dateM/d/yy HH:mm:ss a);$(folder);$(address);\"\";\"$(body)\"[\r\n\t]+", "from", "to");
        DefaultSettings.insertConversionSet (BuiltInConversionSets.DateAndAddressAndBodyAndINBOX.value, sets, varNames, "[\r\n\t]*\"$(dateyy-M-d HH:mm:ss)\",\"$(address)\",\"\",\"$(body)\",\"$(folder)\"[\r\n\t]+", "INBOX", "SENT");
    }

    private static void insertConversionSet (final String conversionSetName, final Map<String, Map<String, String>> sets, final Map<String, List<String>> varNames, final String regexp, final String inboxKeyword, final String sentKeyword) {
        final Map<String, String> subSet = new HashMap<String, String> (5);
        subSet.put (DefaultSettings.COMMON, regexp);
        subSet.put (DefaultSettings.INBOX_KEYWORD, inboxKeyword);
        subSet.put (DefaultSettings.SENT_KEYWORD, sentKeyword);

        final Matcher findVariablesNames = DefaultSettings.VARIABLE_PATTERN.matcher (regexp);
        varNames.put (conversionSetName, new LinkedList<String> ());
        int index = 1;
        while (findVariablesNames.find ()) {
            if ("folder".equals (findVariablesNames.group (1))) {
                subSet.put (DefaultSettings.INDEX_OF_FOLDER_CAPTURING_GROUP, "" + index);
            }
            varNames.get (conversionSetName).add (findVariablesNames.group (1));
            index++;
        }
        sets.put (conversionSetName, subSet);
    }

    private static void loadFromSettings (final Map<String, Map<String, String>> sets, final Map<String, List<String>> varNames) {
        final Map<String, ?> prefs = DefaultSettings.sp.getAll ();
        Set<String> convSetNames = new HashSet<String> ();
        for (final String entry : prefs.keySet ()) {convSetNames.add(entry);}
        for (final String convSetName : convSetNames) {
            DefaultSettings.insertConversionSet (convSetName, sets, varNames, (String) prefs.get (convSetName + '#' + DefaultSettings.COMMON), 
                    (String) prefs.get (convSetName + '#' + DefaultSettings.INBOX_KEYWORD), (String) prefs.get (convSetName + '#' + DefaultSettings.SENT_KEYWORD));
        }
    }

    public static void save (final Map<String, Map<String, String>> sets) {
        final String smsApp = DefaultSettings.getDefaultSmsApp ();
        final Editor editor = DefaultSettings.sp.edit ();
        editor.clear ();
        DefaultSettings.saveDefaultSmsApp (smsApp);
        for (final Entry<String, Map<String,String>> entry : sets.entrySet ()) {
            editor.putString (entry.getKey () + "#" + DefaultSettings.COMMON, entry.getValue ().get (DefaultSettings.COMMON));
            editor.putString (entry.getKey () + "#" + DefaultSettings.INBOX_KEYWORD, entry.getValue ().get (DefaultSettings.INBOX_KEYWORD));
            editor.putString (entry.getKey () + "#" + DefaultSettings.SENT_KEYWORD, entry.getValue ().get (DefaultSettings.SENT_KEYWORD));
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
