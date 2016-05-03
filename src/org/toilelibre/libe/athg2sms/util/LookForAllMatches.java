package org.toilelibre.libe.athg2sms.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.toilelibre.libe.athg2sms.settings.DefaultSettings;
import org.toilelibre.libe.athg2sms.settings.SettingsCommon;

public class LookForAllMatches {

    private final Map<String, Pattern> patterns;

    private final String               content;

    private final SettingsCommon       settings;

    public LookForAllMatches (final String content, final SettingsCommon settings1) {
        super ();
        this.content = content;
        this.settings = settings1;
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
        final String pattern = this.settings.getValPattern (DefaultSettings.COMMON);
        final String sampleOfTheContent = this.content.length () > 1000 ? this.content.substring (0, 1000) : this.content;
        // sample it to test it
        final Matcher m = this.getPattern (pattern).matcher (sampleOfTheContent);
        if (m.find ()) {
            return this.getPattern (pattern).matcher (this.content + '\n');
        }
        java.util.regex.Pattern.compile("[\n\t]*((?:[^;])*);((?:[^;])*);((?:[^;])*);\"\";\"((?:[^\"]|\"\")*)\"[\n\t]+").matcher (
                "12/26/2015 6:22:03 AM;from;+654631231157;\"\";\"na go SONA kno problem hbe na ar oke phn krba go\"" +
                "12/26/2015 6:17:58 AM;from;+654631231157;\"\";\"blchi j tmr sottie kno problem hbe na to go JAAN? ar sei phn gulo deini go ar ki blche go SONAAA?\"" +
                "12/26/2015 6:14:29 AM;from;+654631231157;\"\";\"thik hai\"" +
                "12/26/2015 6:10:48 AM;from;+654631231157;\"\";\"toainaki ha ha ha ha ha\"" +
                "12/26/2015 5:25:57 AM;from;+654631231157;\"\";\"thikache go JAAN tale mongolbarei jabo go\"" +
                "12/26/2015 4:27:18 AM;from;+654631231157;\"\";\"na go SONATA mongolbare hbe na go karn maa ager dn mane mongolbare fanudr bari jabe go tale?\"" +
                "12/26/2015 4:18:23 AM;from;+654631231157;\"\";\"ami blle nebi na krbe na go jabei tale porer sombare jabe go school theke?\"" +
                "12/26/2015 4:11:36 AM;from;+654631231157;\"\";\"kintu mayer operation krar por ki ar jaua hbe go? karn tkhn to amke sb kaj krte hbe abr jdi fanu ase ta o to jaua late uthie dibe go JAAN ki je kri chhai\"" +
                "12/26/2015 4:01:11 AM;from;+654631231157;\"\";\"tau ktodner mddhe eta blen go\"" +
                "12/").find ();
        return null;
    }

}
