package org.toilelibre.libe.athg2sms.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.toilelibre.libe.athg2sms.bp.ConvertException;
import org.toilelibre.libe.athg2sms.pattern.SmsResult;
import org.toilelibre.libe.athg2sms.settings.SettingsCommon;

public class LookForMatchReader extends Reader {

    private final Map<String, Pattern> patterns;

    private final Reader reader;

    private final StringBuffer sb;

    private final SettingsCommon settings;

    public LookForMatchReader (final Reader reader, final SettingsCommon settings1) {
        super ();
        this.reader = reader;
        this.sb = new StringBuffer ();
        this.settings = settings1;
        this.patterns = new HashMap<String, Pattern> ();
    }

    @Override
    public void close () {
        try {
            this.reader.close ();
        } catch (final IOException e) {
            throw new ConvertException ("Cannot happen if the data to be read is a simple String, please change the impl of the Reader", e);
        }
    }

    private Pattern getPattern (final String pattern) {
        if (this.patterns.get (pattern) == null) {
            this.patterns.put (pattern, Pattern.compile (pattern));
        }
        return this.patterns.get (pattern);
    }

    private boolean lastCharIsAlphaNum (final char lastChar) {
        return ( (lastChar >= 'A') && (lastChar <= 'Z')) || ( (lastChar >= 'a') && (lastChar <= 'z')) || ( (lastChar >= '0') && (lastChar <= '9'));
    }

    @Override
    public int read (final char [] buf, final int offset, final int count) throws IOException {
        return this.reader.read (buf, offset, count);
    }

    public SmsResult readUntilNextResult () {
        this.sb.delete (0, this.sb.length ());
        int firstChar = 0;
        SmsResult found = null;
        while ( (found == null) && (firstChar != -1)) {
            firstChar = this.safeRead ();
            this.sb.append ((char) firstChar);
            found = this.tryMatch (this.sb.toString ());
        }
        return found;
    }

    private int safeRead () {
        try {
            return this.reader.read ();
        } catch (final EOFException e) {
            return -1;
        } catch (final IOException e) {
            throw new ConvertException ("Stream error while trying to read the file", e);
        }
    }

    private SmsResult tryMatch (final String value) {
        final char lastChar = value.length () == 0 ? 'A' : value.charAt (value.length () - 1);
        if (this.lastCharIsAlphaNum (lastChar)) {
            return null;
        }
        for (final String key : this.settings.getValPatternsKeySet ()) {
            final String pattern = this.settings.getValPattern (key);
            final Matcher m = this.getPattern (pattern).matcher (value);
            if (m.find ()) {
                return new SmsResult (this.settings, m, value);
            }
        }
        return null;
    }

}
