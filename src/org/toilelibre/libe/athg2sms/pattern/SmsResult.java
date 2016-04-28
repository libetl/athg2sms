package org.toilelibre.libe.athg2sms.pattern;

import java.util.regex.Matcher;

import org.toilelibre.libe.athg2sms.settings.DefaultSettings;
import org.toilelibre.libe.athg2sms.settings.SettingsCommon;

public class SmsResult {

    private final Matcher matcher;
    private final String  folder;
    private final String  catched;

    public SmsResult (SettingsCommon settings, final Matcher matcher, final String catched) {
        super ();
        this.matcher = matcher;
        this.catched = catched;
        int correctGroupNumber = Integer.parseInt (settings.getPattern (DefaultSettings.INDEX_OF_FOLDER_CAPTURING_GROUP));
        this.folder = settings.getPattern (DefaultSettings.INBOX_KEYWORD).equals (matcher.group (correctGroupNumber)) ? 
                DefaultSettings.INBOX : DefaultSettings.SENT;
    }

    public String getFolder () {
        return folder;
    }

    public String getCatched () {
        return this.catched;
    }

    public Matcher getMatcher () {
        return this.matcher;
    }

}
