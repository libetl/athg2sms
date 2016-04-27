package org.toilelibre.libe.athg2sms.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.toilelibre.libe.athg2sms.settings.SettingsCommon;

public class LookForAllMatches {

    private final Map<String, Pattern> patterns;
    
	private final String    	     content;

	private final SettingsCommon	settings;

    private String correctPattern;

	public LookForAllMatches (String content, SettingsCommon settings1) {
		super ();
		this.content = content;
		this.settings = settings1;
		this.patterns = new HashMap<String, Pattern> ();
	}

	public void close () {
	    //Useless in this impl
	}

	public Matcher matcher () {
        for (final String key : this.settings.getValPatternsKeySet ()) {
            final String pattern = this.settings.getValPattern (key);
	            final Matcher m = this.getPattern(pattern).matcher (content);
	            if (m.find ()) {
	                this.correctPattern = key;
	                return m.reset ();
	            }
	        }
        return null;
	}


    public String getCorrectPattern () {
        return correctPattern;
    }

    private Pattern getPattern (String pattern) {
        if (this.patterns.get (pattern) == null) {
            this.patterns.put (pattern, Pattern.compile (pattern));
        }
        return this.patterns.get (pattern);
    }

}
