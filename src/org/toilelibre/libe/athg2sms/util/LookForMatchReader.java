package org.toilelibre.libe.athg2sms.util;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.toilelibre.libe.athg2sms.pattern.SmsResult;
import org.toilelibre.libe.athg2sms.settings.SettingsCommon;

public class LookForMatchReader extends Reader {

    private final Map<String, Pattern> patterns;
    
	private final Reader	     reader;

	private final StringBuffer	 sb;

	private final SettingsCommon	settings;

	public LookForMatchReader (Reader reader, SettingsCommon settings1) {
		super ();
		this.reader = reader;
		this.sb = new StringBuffer ();
		this.settings = settings1;
		this.patterns = new HashMap<String, Pattern> ();
	}

	@Override
	public void close () throws IOException {
		this.reader.close ();
	}

	@Override
	public int read (char [] buf, int offset, int count) throws IOException {
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
		} catch (final IOException e) {
			e.printStackTrace ();
		}
		return -1;
	}

	private SmsResult tryMatch (String value) {
	    char lastChar = value.length () == 0 ? 'A' : value.charAt (value.length () - 1);
	    if (lastCharIsAlphaNum(lastChar)) {
	        return null;
	    }
		for (final String key : this.settings.getValPatternsKeySet ()) {
			final String pattern = this.settings.getValPattern (key);
			final Matcher m = this.getPattern(pattern).matcher (value);
			if (m.find ()) {
				return new SmsResult (m, key, value);
			}
		}
		return null;
	}

    private boolean lastCharIsAlphaNum (char lastChar) {
        return (lastChar >= 'A' && lastChar <= 'Z') ||
                (lastChar >= 'a' && lastChar <= 'z') ||
                (lastChar >= '0' && lastChar <= '9');
    }

    private Pattern getPattern (String pattern) {
        if (this.patterns.get (pattern) == null) {
            this.patterns.put (pattern, Pattern.compile (pattern));
        }
        return this.patterns.get (pattern);
    }

}
