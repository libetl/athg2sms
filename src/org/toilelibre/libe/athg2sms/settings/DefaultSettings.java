package org.toilelibre.libe.athg2sms.settings;

import java.util.HashMap;
import java.util.Map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class DefaultSettings {

	private static SharedPreferences	sp;

	public static String getDefaultSmsApp () {
		return DefaultSettings.sp
		        .getString ("defaultSmsApp", "com.android.mms");
	}

	public static void load (Map<String, Map<String, String>> sets) {
		if ( (DefaultSettings.sp == null)
		        || (DefaultSettings.sp.getAll ().size () <= 1)) {
			DefaultSettings.loadDefaults (sets);
		} else {
			DefaultSettings.loadFromSettings (sets);
		}

	}

	public static void loadDefaults (Map<String, Map<String, String>> sets) {
		final Map<String, String> nokiaCsv = new HashMap<String, String> ();
		nokiaCsv.put (
		        "inbox",
		        "sms;deliver;\"$(address)\";\"\";\"\";\"$(dateyyyy.MM.dd hh:mm)\";\"\";\"$(body)\"\n");
		nokiaCsv.put (
		        "sent",
		        "sms;submit;\"\";\"$(address)\";\"\";\"$(dateyyyy.MM.dd hh:mm)\";\"\";\"$(body)\"\n");
		sets.put ("Nokia Csv", nokiaCsv);
		final Map<String, String> iPhoneCsv = new HashMap<String, String> ();
		iPhoneCsv
		        .put ("inbox",
		                "\"Received\",\"$(dateM/d/yy)\",\"$(dateh:mm a)\",\"$(address)\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"$(body)\",\"[^\"]*\"\n");
		iPhoneCsv
		        .put ("sent",
		                "\"Sent\",\"$(dateM/d/yy)\",\"$(dateh:mm a)\",\"$(address)\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"$(body)\",\"[^\"]*\"\n");
		sets.put ("iPhone Csv", iPhoneCsv);
		final Map<String, String> blackberryCsv = new HashMap<String, String> ();
		blackberryCsv
		        .put ("inbox",
		                "[^,]*,[^,]*,$(dateEEE MMM d HH:mm:ss zzz yyyy),false,$(address),\"$(body)\"\n");
		blackberryCsv
		        .put ("sent",
		                "[^,]*,$(dateEEE MMM d HH:mm:ss zzz yyyy),[^,]*,true,$(address),\"$(body)\"\n");
		sets.put ("Blackberry Csv", blackberryCsv);
		final Map<String, String> additionalCsv20161 = new HashMap<String, String> ();
        additionalCsv20161
        .put ("inbox","[\r\n]*$(dateM/d/yy HH:mm:ss a);from;$(address);\"\";\"$(body)\"[\r\n]+");
        additionalCsv20161
        .put ("sent",
                "[\r\n]*$(dateM/d/yy hh:mm:ss a);to;$(address);\"\";\"$(body)\"[\r\n]+");
        sets.put ("Date+'from'+address+body", additionalCsv20161);
        final Map<String, String> additionalCsv20162 = new HashMap<String, String> ();
        additionalCsv20162
        .put ("inbox","[\r\n]*\"$(dateyy-M-d HH:mm:ss)\",\"$(address)\",\"\",\"$(body)\",\"INBOX\"[\r\n]+");
        additionalCsv20162
        .put ("sent","[\r\n]*\"$(dateyy-M-d HH:mm:ss)\",\"$(address)\",\"\",\"$(body)\",\"SENT\"[\r\n]+");
        sets.put ("Date+address+body+INBOX", additionalCsv20162);
	}

	private static void loadFromSettings (Map<String, Map<String, String>> sets) {
		final Map<String, ?> map = DefaultSettings.sp.getAll ();
		for (final String key : map.keySet ()) {
			final String [] location = key.split ("#");
			if (location.length > 1) {
				if (sets.get (location [0]) == null) {
					sets.put (location [0], new HashMap<String, String> ());
				}
				sets.get (location [0]).put (location [1],
				        (String) map.get (key));
			}
		}

	}

	public static void save (Map<String, Map<String, String>> sets) {
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

	public static void saveDefaultSmsApp (String packageName) {
		final Editor editor = DefaultSettings.sp.edit ();
		editor.putString ("defaultSmsApp", packageName);
		editor.commit ();
	}

	public static void setSp (SharedPreferences sp) {
		DefaultSettings.sp = sp;
	}

}
