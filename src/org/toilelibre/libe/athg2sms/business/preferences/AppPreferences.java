package org.toilelibre.libe.athg2sms.business.preferences;

import android.annotation.SuppressLint;

import org.toilelibre.libe.athg2sms.androidstuff.api.storage.SharedPreferencesHolder;
import org.toilelibre.libe.athg2sms.business.pattern.Format;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AppPreferences {

    private static final String   COMMON        = "common";
    private static final String   INBOX_KEYWORD = "inboxKeyword";
    private static final String   SENT_KEYWORD  = "sentKeyword";
    private final SharedPreferencesHolder<?> sharedPreferences;

    public AppPreferences(SharedPreferencesHolder sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String getDefaultSmsApp () {
        return sharedPreferences.getString ("defaultSmsApp", "com.android.mms");
    }

    public void saveDefaultSmsApp (final String packageName) {
        sharedPreferences.edit ().putString ("defaultSmsApp", packageName).commit ();
    }
    
    @SuppressLint("NewApi")
	public void saveAskedPermissions (final String... permissions) {
        sharedPreferences.edit ().putStringSet ("permissions", new HashSet<String>(Arrays.asList (permissions))).commit ();
    }
    
    @SuppressLint("NewApi")
	public Set<String> getAskedPermissions () {
        return sharedPreferences.getStringSet ("permissions", new HashSet<String>());
    }

    public void saveFormats(final Map<String, Format> formats) {
        final String smsApp = this.getDefaultSmsApp ();
        final SharedPreferencesHolder.EditorHolder<?> editor = sharedPreferences.edit ();
        editor.clear ();
        this.saveDefaultSmsApp (smsApp);
        for (final Entry<String, Format> entry : formats.entrySet ()) {
            editor.putString (entry.getKey () + "#" + AppPreferences.COMMON, entry.getValue ().getRegex().getCommonRegex());
            editor.putString (entry.getKey () + "#" + AppPreferences.INBOX_KEYWORD, entry.getValue ().getRegex().getInboxKeyword());
            editor.putString (entry.getKey () + "#" + AppPreferences.SENT_KEYWORD, entry.getValue ().getRegex().getSentKeyword());
        }
        editor.commit ();
    }

}
