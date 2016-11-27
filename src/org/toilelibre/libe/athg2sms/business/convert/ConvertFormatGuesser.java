package org.toilelibre.libe.athg2sms.business.convert;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;

import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;

public class ConvertFormatGuesser {

    public String guessNow (String content) throws FileNotFoundException {

        Matcher matcher = null;
        String key = null;
        for (String pattern : FormatSettings.getInstance().getFormats().keySet()) {
            key = pattern;
            matcher = new MatchesScanner(FormatSettings.getInstance().preparePattern (pattern), content).matcher ();
            if (matcher != null) {
                break;
            }
            
        }

        return matcher == null ? null : key;
    }
    
}
