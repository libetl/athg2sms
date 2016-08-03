package org.toilelibre.libe.athg2sms.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.toilelibre.libe.athg2sms.settings.DefaultSettings;
import org.toilelibre.libe.athg2sms.settings.PreparedPattern;

public class MatchesScanner {

    private final Map<String, Pattern> patterns;

    private final String               content;

    private final PreparedPattern preparedPattern;

    public MatchesScanner (PreparedPattern preparedPattern, final String content) {
        super ();
        this.content = content;
        this.preparedPattern = preparedPattern;
        this.patterns = new HashMap<String, Pattern> ();
    }

    public void close () {
        // Useless in this impl
    }

    private Pattern getPattern (final String pattern) {
        if (this.patterns.get (pattern) == null) {
            this.patterns.put (pattern, Pattern.compile (pattern));
        }
        return this.patterns.get (pattern);
    }

    public Matcher matcher () {
        final String pattern = preparedPattern.getValPattern ().get (DefaultSettings.COMMON);
        final String sampleOfTheContent = this.content.length () > 2048 ? this.content.substring (0, 2048) : this.content;
        // sample it to test it
        final Pattern patternObject = this.getPattern (pattern);
        final Matcher m = patternObject.matcher (sampleOfTheContent);
        
        return m.find () ? patternObject.matcher (this.content + '\n') : null;
    }

}
