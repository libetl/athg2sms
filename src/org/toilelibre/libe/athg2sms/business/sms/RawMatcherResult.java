package org.toilelibre.libe.athg2sms.business.sms;

import org.toilelibre.libe.athg2sms.business.pattern.Format;

import java.util.regex.Matcher;

public class RawMatcherResult {
    public static final String   SENT                  = "sent";
    public static final String   INBOX                 = "inbox";

    private final String [] groups;
    private final String    folder;
    private final String    catched;
    private final Matcher   matcher;

    public RawMatcherResult(final Matcher matcher, final Format.FormatRegexRepresentation regexRepresentation, final String catched) {
        this.groups = new String [matcher.groupCount ()];
        this.catched = catched;
        this.matcher = matcher;
        final int correctGroupNumber = regexRepresentation.getIndexOfFolderCapturingGroup();
        this.folder = regexRepresentation.getInboxKeyword().equals (matcher.group (correctGroupNumber)) ? INBOX : SENT;
        for (int i = 1 ; i <= matcher.groupCount () ; i++) {
            this.groups [i - 1] = matcher.group (i);
        }
    }

    public String getFolder () {
        return this.folder;
    }

    public String getCatched () {
        return this.catched;
    }

    public String group (final int i) {
        return this.groups [i];
    }

    public Matcher getMatcher () {
        return this.matcher;
    }

}
