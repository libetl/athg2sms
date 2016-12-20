package org.toilelibre.libe.athg2sms.business.convert;

import org.toilelibre.libe.athg2sms.business.pattern.PreparedPattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MatchesScanner {

    private final String               content;

    private final PreparedPattern preparedPattern;

    MatchesScanner (PreparedPattern preparedPattern, final String content) {
        super ();
        this.content = content;
        this.preparedPattern = preparedPattern;
    }

    Matcher matcher () {
        final String pattern = preparedPattern.getValPattern ();
        final String sampleOfTheContent = this.content.length () > 2048 ? this.content.substring (0, 2048) : this.content;
        // sample it to test it
        final Pattern patternObject = Pattern.compile (pattern);
        final Matcher m = patternObject.matcher (sampleOfTheContent);
        
        return m.find () ? patternObject.matcher (this.content + '\n') : null;
    }

}
