package org.toilelibre.libe.athg2sms.util;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.toilelibre.libe.athg2sms.pattern.SmsResult;
import org.toilelibre.libe.athg2sms.settings.SettingsCommon;

public class LookForMatchReader extends Reader {

	private final Reader	     reader;

	private final StringBuffer	 sb;

	private final SettingsCommon	settings;

	public LookForMatchReader (Reader reader, SettingsCommon settings1) {
		super ();
		this.reader = reader;
		this.sb = new StringBuffer ();
		this.settings = settings1;
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
		for (final String key : this.settings.getValPatternsKeySet ()) {
			final String pattern = this.settings.getValPattern (key);
			final Matcher m = Pattern.compile (pattern).matcher (value);
			if (m.find ()) {
				return new SmsResult (m, key, value);
			}
		}
		return null;
	}

}
