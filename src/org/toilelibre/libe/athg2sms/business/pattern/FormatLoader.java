package org.toilelibre.libe.athg2sms.business.pattern;

import org.toilelibre.libe.athg2sms.androidstuff.api.storage.SharedPreferencesHolder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class FormatLoader {
    private static final String EXPORT_FORMAT = "exportFormat";
    private static final String COMMON        = "common";
    private static final String INBOX_KEYWORD = "inboxKeyword";
    private static final String SENT_KEYWORD  = "sentKeyword";

    Map<String,Format> loadWithoutPreferences() {
        return new BuiltInFormatsLoader().loadDefaults();
    }

    @SuppressWarnings("unchecked")
    Map<String, Format> loadFrom (final SharedPreferencesHolder sharedPreferences) {
        final Map<String, Format> formats = new HashMap<String, Format>();
        if (sharedPreferences == null)return formats;
        final Map<String, ?> prefs = sharedPreferences.getAll ();
        Set<String> formatNames = new HashSet<String>();
        for (final String entry : prefs.keySet ()) {if (entry.indexOf ('#') != -1)formatNames.add(entry.split ("#") [0]);}
        for (final String formatName : formatNames) {
            formats.put(formatName,
                    new Format(formatName, get(prefs, formatName, FormatLoader.COMMON),
                            get(prefs, formatName, FormatLoader.EXPORT_FORMAT),
                            get(prefs, formatName, FormatLoader.INBOX_KEYWORD),
                            (String) prefs.get (formatName + '#' + FormatLoader.SENT_KEYWORD)));
        }
        return formats;
    }

    private String get(Map<String, ?> prefs, String formatName, String suffix) {
        String result = (String)prefs.get(formatName + '#' + suffix);
        return result == null ? "" : result;
    }
}
