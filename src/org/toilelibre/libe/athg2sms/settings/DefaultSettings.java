package org.toilelibre.libe.athg2sms.settings;

import java.util.Arrays;
import java.util.Collections;
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
    public static final String   EXPORT_FORMAT                   = "exportFormat";
    public static final String   INBOX_KEYWORD                   = "inboxKeyword";
    public static final String   SENT_KEYWORD                    = "sentKeyword";
    public static final String   INDEX_OF_FOLDER_CAPTURING_GROUP = "indexOfFolderCapturingGroup";
    private static final Pattern VARIABLE_PATTERN                = Pattern.compile ("\\$\\(([^)]+)\\)");

    public enum BuiltInConversionSets {
    	NokiaSuite ("Nokia Suite 3.8"), NokiaVmgInbox ("Nokia Vmg Inbox"), NokiaVmgSent ("Nokia Vmg Sent"), NokiaCsv ("Nokia Csv"), IPhoneCsv ("iPhone Csv"), BlackberryCsv ("Blackberry Csv"), DateAndFromAndAddressAndbody ("Date+'from'+address+body"), 
    	DateAndAddressAndBodyAndINBOX ("Date+address+body+INBOX"), NokiaCsvWithQuotes ("Nokia Csv with quotes"), NokiaCsvWithCommas ("Nokia Csv with commas"), UnknownSmsFormat1 ("Unknown Sms Format 1"), LumiaVmg ("Lumia Vmg");

        private final String value;

        BuiltInConversionSets (final String value) {
            this.value = value;
        }

        public String getValue () {
            return this.value;
        }
    }

    private static SharedPreferences sp;

    public static String getDefaultSmsApp (SharedPreferences sharedPreferences) {
        ensureSpIsPresent (sharedPreferences);
        if (DefaultSettings.sp == null)return "com.android.mms";
        return DefaultSettings.sp.getString ("defaultSmsApp", "com.android.mms");
    }

    static void load (SharedPreferences sharedPreferences, final Map<String, Map<String, String>> sets, final Map<String, List<String>> varNames) {
        if (sharedPreferences != null) {
            DefaultSettings.loadFromSettings (sharedPreferences, sets, varNames);
            return;
        }
        DefaultSettings.loadDefaults (sets, varNames);
    }

    static void loadDefaults (final Map<String, Map<String, String>> sets, final Map<String, List<String>> varNames) {
        DefaultSettings.insertPattern(BuiltInConversionSets.LumiaVmg.value, sets, varNames, "[\\s]*BEGIN:VMSG\\s+VERSION:[ ]*[0-9]+(?:.[0-9]+)\\r\\nBEGIN:VCARD\\r\\nTEL:$(address..\\r\\nEND:VCARD)\\r\\nEND:VCARD\\r\\nBEGIN:VBODY\\r\\nX-BOX:$(folder..\\r\\nX-READ)\\r\\nX-READ:[^\\s]+\\r\\nX-SIMID:[^\\s]+\\r\\nX-LOCKED:[^\\s]+\\r\\nX-TYPE:[^\\s]+\\r\\nDate:$(dateyyyy/MM/dd HH:mm:ss..\\r\\nSubject)\\r\\nSubject;ENCODING=$(encoding);CHARSET=$(charset):$(body..\\r\\nEND:VBODY)\\r\\nEND:VBODY\\r\\nEND:VMSG\\r\\n", 
                "BEGIN:VMSG\r\nVERSION: 1.1\r\nBEGIN:VCARD\r\nTEL:$(address)\r\nEND:VCARD\r\nBEGIN:VBODY\r\nX-BOX:$(folder)\r\nX-READ:READ\r\nX-SIMID:0\r\nX-LOCKED:UNLOCKED\r\nX-TYPE:SMS\r\nDate:$(dateyyyy/MM/dd HH:mm:ss)\r\nSubject;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8:$(body)\r\nEND:VBODY\r\nEND:VMSG\r\n", "INBOX", "SENDBOX");
        DefaultSettings.insertPattern(BuiltInConversionSets.NokiaVmgInbox.value, sets, varNames, "[\\s]*BEGIN:VENV\nBEGIN:VCARD\nVERSION:[0-9]+(?:.[0-9]+)\nN:$(folder)\nTEL:$(address)\nEND:VCARD\nBEGIN:VENV\nBEGIN:VBODY\nDate:$(datedd-MM-yyyy HH:mm:ss)\n$(body)\nEND:VBODY\nEND:VENV\nEND:VENV\n",
                "BEGIN:VENV\nBEGIN:VCARD\nVERSION:[0-9]+(?:.[0-9]+)\nN:$(folder)\nTEL:$(address)\nEND:VCARD\nBEGIN:VENV\nBEGIN:VBODY\nDate:$(datedd-MM-yyyy HH:mm:ss)\n$(body)\nEND:VBODY\nEND:VENV\nEND:VENV\n", "", "?");
        DefaultSettings.insertPattern(BuiltInConversionSets.NokiaVmgSent.value, sets, varNames, "[\\s]*BEGIN:VENV\nBEGIN:VCARD\nVERSION:[0-9]+(?:.[0-9]+)\nN:$(folder)\nTEL:$(address)\nEND:VCARD\nBEGIN:VENV\nBEGIN:VBODY\nDate:$(datedd-MM-yyyy HH:mm:ss)\n$(body)\nEND:VBODY\nEND:VENV\nEND:VENV\n", 
                "BEGIN:VENV\nBEGIN:VCARD\nVERSION:[0-9]+(?:.[0-9]+)\nN:$(folder)\nTEL:$(address)\nEND:VCARD\nBEGIN:VENV\nBEGIN:VBODY\nDate:$(datedd-MM-yyyy HH:mm:ss)\n$(body)\nEND:VBODY\nEND:VENV\nEND:VENV\n", "", "?");
        DefaultSettings.insertPattern(BuiltInConversionSets.NokiaCsvWithQuotes.value, sets, varNames, "[\\s]*\"sms\";\"$(folder)\";(?:\"\";)?\"$(address)\";\"\";(?:\"\";)?\"$(dateyyyy.MM.dd HH:mm)\";\"\";\"$(body)\"[\\s]+", 
                "\"sms\";\"$(folder)\";\"$(inbox:address)\";\"$(sent:address)\";\"\";\"$(dateyyyy.MM.dd HH:mm)\";\"\";\"$(body)\"\n", "deliver", "submit");
        DefaultSettings.insertPattern(BuiltInConversionSets.NokiaCsvWithCommas.value, sets, varNames, "[\\s]*sms,$(folder),(?:\"\",)?\"$(address)\",\"\",(?:\"\",)?\"$(dateyyyy.MM.dd HH:mm)\",\"\",\"$(body)\"[\\s]+", 
                "sms,$(folder),\"$(inbox:address)\",\"$(sent:address)\";\"\";\"$(dateyyyy.MM.dd HH:mm)\",\"\",\"$(body)\"\n", "deliver", "submit");
        DefaultSettings.insertPattern(BuiltInConversionSets.IPhoneCsv.value, sets, varNames, "[\\s]*\"$(folder)\",\"$(dateM/d/yy)\",\"$(dateH:mm a)\",\"$(address)\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"$(body)\",\"[^\"]*\"[\\s]+", 
                "\"$(folder)\",\"$(dateM/d/yy)\",\"$(dateH:mm a)\",\"$(address)\",\"\",\"\",\"\",\"$(body)\",\"\"\n", "Received", "Sent");
        DefaultSettings.insertPattern(BuiltInConversionSets.BlackberryCsv.value, sets, varNames, "[\\s]*[^,]*,(?:,)?$(dateEEE MMM d HH:mm:ss zzz yyyy),(?:,)?$(folder),$(address),\"$(body)\"[\\s]+", 
                "1,$(dateEEE MMM d HH:mm:ss zzz yyyy),$(folder),$(address),\"$(body)\"\n", "false", "true");
        DefaultSettings.insertPattern(BuiltInConversionSets.DateAndFromAndAddressAndbody.value, sets, varNames, "[\\s]*$(dateM/d/yy HH:mm:ss a);$(folder);$(address);\"\";\"$(body)\"[\\s]+", 
                "$(dateM/d/yy HH:mm:ss a);$(folder);$(address);\"\";\"$(body)\"\n", "from", "to");
        DefaultSettings.insertPattern(BuiltInConversionSets.DateAndAddressAndBodyAndINBOX.value, sets, varNames, "[\\s]*\"$(dateyy-M-d HH:mm:ss)\",\"$(address)\",\"\",\"$(body)\",\"$(folder)\"[\\s]+", 
                "\"$(dateyy-M-d HH:mm:ss)\",\"$(address)\",\"\",\"$(body)\",\"$(folder)\"\n", "INBOX", "SENT");
        DefaultSettings.insertPattern(BuiltInConversionSets.NokiaCsv.value, sets, varNames, "[\\s]*sms;$(folder);(?:\"\";)?\"$(address)\";\"\";(?:\"\";)?\"$(dateyyyy.MM.dd HH:mm)\";\"\";\"$(body)\"[\\s]+", 
                "sms;$(folder);\"$(inbox:address)\";\"$(sent:address)\";\"\";\"$(dateyyyy.MM.dd HH:mm)\";\"\";\"$(body)\"\n", "deliver", "submit");
        DefaultSettings.insertPattern(BuiltInConversionSets.NokiaSuite.value, sets, varNames, "[\\s]*\"sms\",\"$(folder)\",(?:\"\",)?\"$(address)\",(?:\"\",)?\"\",\"$(dateyyyy.MM.dd HH:mm)\",\"\",\"$(body)\"[\\s]+", 
                "\"sms\",\"$(folder)\",\"$(inbox:address)\",\"$(sent:address)\",\"\",\"$(dateyyyy.MM.dd HH:mm)\",\"\",\"$(body)\"\n", "READ,RECEIVED", "SENT");
        DefaultSettings.insertPattern(BuiltInConversionSets.UnknownSmsFormat1.value, sets, varNames, "[\\s]*\"$(address)\",\"$(dateyyyy-MM-dd HH:mm)\",\"SMS\",\"$(folder)\",\"$(body)\"[\\s]+", 
                "\"$(address)\",\"$(dateyyyy-MM-dd HH:mm)\",\"SMS\",\"$(folder)\",\"$(body)\"\n", "0", "1");
    }

    private static void insertPattern(final String conversionSetName, final Map<String, Map<String, String>> sets, final Map<String, List<String>> varNames, final String regexp, final String exportFormat, final String inboxKeyword, final String sentKeyword) {
        final Map<String, String> subSet = new HashMap<String, String> (5);
        subSet.put (DefaultSettings.COMMON, regexp);
        subSet.put (DefaultSettings.INBOX_KEYWORD, inboxKeyword);
        subSet.put (DefaultSettings.SENT_KEYWORD, sentKeyword);
        subSet.put (DefaultSettings.EXPORT_FORMAT, exportFormat);

        final Matcher findVariablesNames = DefaultSettings.VARIABLE_PATTERN.matcher (regexp);
        varNames.put (conversionSetName, new LinkedList<String> ());
        int index = 1;
        while (findVariablesNames.find ()) {
            String fullVarNameWithEndToken = findVariablesNames.group (1);
            String realVariableName = !fullVarNameWithEndToken.contains("..") ? fullVarNameWithEndToken :
                fullVarNameWithEndToken.substring (0, fullVarNameWithEndToken.indexOf (".."));
            if ("folder".equals (realVariableName)) {
                subSet.put (DefaultSettings.INDEX_OF_FOLDER_CAPTURING_GROUP, "" + index);
            }
            varNames.get (conversionSetName).add (realVariableName);
            index++;
        }
        sets.put (conversionSetName, subSet);
    }

    private static void loadFromSettings (SharedPreferences sharedPreferences, final Map<String, Map<String, String>> sets, final Map<String, List<String>> varNames) {
        ensureSpIsPresent (sharedPreferences);
        if (DefaultSettings.sp == null)return;
        final Map<String, ?> prefs = DefaultSettings.sp.getAll ();
        Set<String> convSetNames = new HashSet<String> ();
        for (final String entry : prefs.keySet ()) {if (entry.indexOf ('#') != -1)convSetNames.add(entry.split ("#") [0]);}
        for (final String convSetName : convSetNames) {
            DefaultSettings.insertPattern(convSetName, sets, varNames, (String) prefs.get (convSetName + '#' + DefaultSettings.COMMON),
                    (String) prefs.get (convSetName + '#' + DefaultSettings.EXPORT_FORMAT),
                    (String) prefs.get (convSetName + '#' + DefaultSettings.INBOX_KEYWORD), (String) prefs.get (convSetName + '#' + DefaultSettings.SENT_KEYWORD));
        }
    }

    public static void save (SharedPreferences sharedPreferences, final Map<String, Map<String, String>> sets) {
        ensureSpIsPresent (sharedPreferences);
        if (DefaultSettings.sp == null)return;
        final String smsApp = DefaultSettings.getDefaultSmsApp (sharedPreferences);
        final Editor editor = DefaultSettings.sp.edit ();
        editor.clear ();
        DefaultSettings.saveDefaultSmsApp (sharedPreferences, smsApp);
        for (final Entry<String, Map<String,String>> entry : sets.entrySet ()) {
            editor.putString (entry.getKey () + "#" + DefaultSettings.COMMON, entry.getValue ().get (DefaultSettings.COMMON));
            editor.putString (entry.getKey () + "#" + DefaultSettings.INBOX_KEYWORD, entry.getValue ().get (DefaultSettings.INBOX_KEYWORD));
            editor.putString (entry.getKey () + "#" + DefaultSettings.SENT_KEYWORD, entry.getValue ().get (DefaultSettings.SENT_KEYWORD));
        }
        editor.commit ();
    }

    public static void saveDefaultSmsApp (SharedPreferences sharedPreferences, final String packageName) {
        ensureSpIsPresent (sharedPreferences);
        if (DefaultSettings.sp == null)return;
        final Editor editor = DefaultSettings.sp.edit ();
        editor.putString ("defaultSmsApp", packageName);
        editor.commit ();
    }
    
    @SuppressLint("NewApi")
	public static void saveAskedPermissions (SharedPreferences sharedPreferences, final String... permissions) {
        ensureSpIsPresent (sharedPreferences);
        if (DefaultSettings.sp == null)return;
        final Editor editor = DefaultSettings.sp.edit ();
        editor.putStringSet ("permissions", new HashSet<String>(Arrays.asList (permissions)));
        editor.commit ();
    }
    
    @SuppressLint("NewApi")
	public static Set<String> getAskedPermissions (SharedPreferences sharedPreferences) {
        ensureSpIsPresent (sharedPreferences);
        if (DefaultSettings.sp == null)return Collections.emptySet ();
        return DefaultSettings.sp.getStringSet ("permissions", new HashSet<String>());
    }

    static void setSp (final SharedPreferences sp) {
        DefaultSettings.sp = sp;
    }

    public static void ensureSpIsPresent (SharedPreferences sharedPreferences) {
        if (DefaultSettings.sp == null || DefaultSettings.sp.getAll ().isEmpty ()) {
            setSp (sharedPreferences);
        }
        
    }

}
