package org.toilelibre.libe.athg2sms.pattern;

import java.util.regex.Matcher;

public class SmsResult {

	private final Matcher	matcher;
	private final String	key;
	private final String	catched;

	public SmsResult (Matcher matcher, String key, String catched) {
		super ();
		this.matcher = matcher;
		this.key = key;
		this.catched = catched;
	}

	public String getCatched () {
		return this.catched;
	}

	public String getKey () {
		return this.key;
	}

	public Matcher getMatcher () {
		return this.matcher;
	}

}
