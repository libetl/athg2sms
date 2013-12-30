package org.toilelibre.libe.athg2sms.pattern;

import java.util.regex.Matcher;

public class SmsResult {

	private Matcher matcher;
	private String key;
	private String catched;
	
	public SmsResult (Matcher matcher, String key, String catched) {
	    super ();
	    this.matcher = matcher;
	    this.key = key;
	    this.catched = catched;
    }
	
	public Matcher getMatcher () {
		return matcher;
	}
	
	public String getKey () {
		return key;
	}
	
	public String getCatched () {
		return catched;
	}
	
	
}
