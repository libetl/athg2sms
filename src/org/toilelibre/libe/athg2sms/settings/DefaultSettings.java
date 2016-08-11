package org.toilelibre.libe.athg2sms.settings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.annotation.SuppressLint;
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
    	NokiaSuite ("Nokia Suite 3.8"), NokiaVmgInbox ("Nokia Vmg Inbox"), NokiaVmgSent ("Nokia Vmg Sent"), NokiaCsv ("Nokia Csv"), IPhoneCsv ("iPhone Csv"), BlackberryCsv ("Blackberry Csv"), DateAndFromAndAddressAndbody ("Date+'from'+address+body"), DateAndAddressAndBodyAndINBOX ("Date+address+body+INBOX"), NokiaCsvWithQuotes ("Nokia Csv with quotes"), NokiaCsvWithCommas ("Nokia Csv with commas");

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
        DefaultSettings.insertConversionSet (BuiltInConversionSets.NokiaVmgInbox.value, sets, varNames, "[\\s]*BEGIN:VENV\nBEGIN:VCARD\nVERSION:[0-9]+(?:.[0-9]+)\nN:$(folder)\nTEL:$(address)\nEND:VCARD\nBEGIN:VENV\nBEGIN:VBODY\nDate:$(datedd-MM-yyyy HH:mm:ss)\n$(body)\nEND:VBODY\nEND:VENV\nEND:VENV\n", "", "?");
        DefaultSettings.insertConversionSet (BuiltInConversionSets.NokiaVmgSent.value, sets, varNames, "[\\s]*BEGIN:VENV\nBEGIN:VCARD\nVERSION:[0-9]+(?:.[0-9]+)\nN:$(folder)\nTEL:$(address)\nEND:VCARD\nBEGIN:VENV\nBEGIN:VBODY\nDate:$(datedd-MM-yyyy HH:mm:ss)\n$(body)\nEND:VBODY\nEND:VENV\nEND:VENV\n", "", "?");
        DefaultSettings.insertConversionSet (BuiltInConversionSets.NokiaCsvWithQuotes.value, sets, varNames, "[\\s]*\"sms\";\"$(folder)\";(?:\"\";)?\"$(address)\";\"\";(?:\"\";)?\"$(dateyyyy.MM.dd hh:mm)\";\"\";\"$(body)\"[\\s]+", "deliver", "submit");
        DefaultSettings.insertConversionSet (BuiltInConversionSets.NokiaCsvWithCommas.value, sets, varNames, "[\\s]*sms,$(folder),(?:\"\",)?\"$(address)\",\"\",(?:\"\",)?\"$(dateyyyy.MM.dd hh:mm)\",\"\",\"$(body)\"[\\s]+", "deliver", "submit");
        DefaultSettings.insertConversionSet (BuiltInConversionSets.IPhoneCsv.value, sets, varNames, "[\\s]*\"$(folder)\",\"$(dateM/d/yy)\",\"$(dateh:mm a)\",\"$(address)\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"$(body)\",\"[^\"]*\"[\\s]+", "Received", "Sent");
        DefaultSettings.insertConversionSet (BuiltInConversionSets.BlackberryCsv.value, sets, varNames, "[\\s]*[^,]*,(?:,)?$(dateEEE MMM d HH:mm:ss zzz yyyy),(?:,)?$(folder),$(address),\"$(body)\"[\\s]+", "false", "true");
        DefaultSettings.insertConversionSet (BuiltInConversionSets.DateAndFromAndAddressAndbody.value, sets, varNames, "[\\s]*$(dateM/d/yy HH:mm:ss a);$(folder);$(address);\"\";\"$(body)\"[\\s]+", "from", "to");
        DefaultSettings.insertConversionSet (BuiltInConversionSets.DateAndAddressAndBodyAndINBOX.value, sets, varNames, "[\\s]*\"$(dateyy-M-d HH:mm:ss)\",\"$(address)\",\"\",\"$(body)\",\"$(folder)\"[\\s]+", "INBOX", "SENT");
        DefaultSettings.insertConversionSet (BuiltInConversionSets.NokiaCsv.value, sets, varNames, "[\\s]*sms;$(folder);(?:\"\";)?\"$(address)\";\"\";(?:\"\";)?\"$(dateyyyy.MM.dd hh:mm)\";\"\";\"$(body)\"[\\s]+", "deliver", "submit");
        DefaultSettings.insertConversionSet (BuiltInConversionSets.NokiaSuite.value, sets, varNames, "[\\s]*\"sms\",\"$(folder)\",(?:\"\",)?\"$(address)\",(?:\"\",)?\"\",\"$(dateyyyy.MM.dd hh:mm)\",\"\",\"$(body)\"[\\s]+", "READ,RECEIVED", "SENT");
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
        for (final String entry : prefs.keySet ()) {if (entry.indexOf ('#') != -1)convSetNames.add(entry.split ("#") [0]);}
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
    
    @SuppressLint("NewApi")
	public static void saveAskedPermissions (final String... permissions) {
        final Editor editor = DefaultSettings.sp.edit ();
        editor.putStringSet ("permissions", new HashSet<String>(Arrays.asList (permissions)));
        editor.commit ();
    }
    
    @SuppressLint("NewApi")
	public static Set<String> getAskedPermissions () {
        return DefaultSettings.sp.getStringSet ("permissions", new HashSet<String>());
    }

    public static void setSp (final SharedPreferences sp) {
        DefaultSettings.sp = sp;
    }

    public static void ensureSpIsPresent (SharedPreferences sharedPreferences) {
        if (DefaultSettings.sp == null) {
            setSp (sharedPreferences);
        }
        
    }

}
