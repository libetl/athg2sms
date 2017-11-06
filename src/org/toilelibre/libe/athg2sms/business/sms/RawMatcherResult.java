package org.toilelibre.libe.athg2sms.business.sms;

import org.toilelibre.libe.athg2sms.business.pattern.Format;

import java.util.Arrays;
import java.util.regex.Matcher;

import static org.toilelibre.libe.athg2sms.business.sms.Folder.INBOX;
import static org.toilelibre.libe.athg2sms.business.sms.Folder.SENT;

public class RawMatcherResult {

    private final String [] groups;
    private final Folder    folder;
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

    public Folder getFolder () {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RawMatcherResult that = (RawMatcherResult) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(groups, that.groups)) return false;
        if (folder != that.folder) return false;
        if (catched != null ? !catched.equals(that.catched) : that.catched != null) return false;
        return matcher != null ? matcher.equals(that.matcher) : that.matcher == null;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(groups);
        result = 31 * result + (folder != null ? folder.hashCode() : 0);
        result = 31 * result + (catched != null ? catched.hashCode() : 0);
        result = 31 * result + (matcher != null ? matcher.hashCode() : 0);
        return result;
    }
}
