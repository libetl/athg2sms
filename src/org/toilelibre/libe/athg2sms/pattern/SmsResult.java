package org.toilelibre.libe.athg2sms.pattern;

import java.util.regex.Matcher;

import org.toilelibre.libe.athg2sms.settings.DefaultSettings;
import org.toilelibre.libe.athg2sms.settings.SettingsCommon;

public class SmsResult {

    private final String [] groups;
    private final String  folder;
    private final String  catched;
    private final Matcher matcher;

    public SmsResult (SettingsCommon settings, final Matcher matcher, final String catched) {
        this.groups = new String [matcher.groupCount ()];
        this.catched = catched;
        this.matcher = matcher;
        int correctGroupNumber = Integer.parseInt (settings.getPattern (DefaultSettings.INDEX_OF_FOLDER_CAPTURING_GROUP));
        this.folder = settings.getPattern (DefaultSettings.INBOX_KEYWORD).equals (matcher.group (correctGroupNumber)) ? 
                DefaultSettings.INBOX : DefaultSettings.SENT;
        for (int i = 1 ; i <= matcher.groupCount () ; i++) {
            this.groups [i - 1] = matcher.group (i);
        }
    }

    public String getFolder () {
        return folder;
    }

    public String getCatched () {
        return this.catched;
    }
    public String group (int i) {
        return this.groups [i];
    }

    public Matcher getMatcher () {
        return this.matcher;
    }

}
