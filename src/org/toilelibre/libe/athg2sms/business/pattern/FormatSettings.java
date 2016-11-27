package org.toilelibre.libe.athg2sms.business.pattern;

import android.content.SharedPreferences;

import java.util.Map;

public class FormatSettings {

    private static final FormatSettings INSTANCE = new FormatSettings();
    private final Map<String, Format> formats = new FormatLoader().loadWithoutPreferences();

    public static FormatSettings getInstance () {
        return FormatSettings.INSTANCE;
    }

    private FormatSettings() {
    }

    public Map<String, Format> getFormats() {
        return this.formats;
    }

    public PreparedPattern preparePattern (String key) {
        return PreparedPattern.fromFormat (this.formats.get (key).getRegex());
    }

    public void addOrChangeFormats(final Format... formats) {
        for (Format format : formats) {
            this.formats.put(format.getName(), format);
        }
    }

    public void loadFrom(SharedPreferences sharedPreferences) {
        Map<String, Format> preferencesFormats = new FormatLoader().loadFrom(sharedPreferences);
        for (Map.Entry<String, Format> preferenceFormat : preferencesFormats.entrySet()) {
            if (!this.formats.containsKey(preferenceFormat.getKey())) {
                this.formats.put(preferenceFormat.getKey(), preferenceFormat.getValue());
            }
        }
    }
}
