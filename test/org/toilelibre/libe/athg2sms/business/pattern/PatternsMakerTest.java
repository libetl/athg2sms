package org.toilelibre.libe.athg2sms.business.pattern;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by lionel on 06/03/17.
 */
public class PatternsMakerTest {

    @Test
    public void makePreparedPattern() {
        PatternsMaker patternsMaker = new PatternsMaker();
        patternsMaker.makePreparedPattern(new Format("foo", "$()$(folder)", "$()$(folder)", "foo", "bar").getRegex());
    }

}