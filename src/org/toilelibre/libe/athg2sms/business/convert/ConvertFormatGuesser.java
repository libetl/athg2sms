package org.toilelibre.libe.athg2sms.business.convert;

import org.toilelibre.libe.athg2sms.business.pattern.Format;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;
import org.toilelibre.libe.athg2sms.business.sms.RawMatcherResult;
import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Map;
import java.util.regex.Matcher;

public class ConvertFormatGuesser {

    public String guessNow (String content) throws FileNotFoundException {

        Matcher matcher = null;
        String key = null;
        for (Map.Entry<String, Format> patternEntrySet : FormatSettings.getInstance().getFormats().entrySet()) {
            key = patternEntrySet.getKey();
            matcher = new MatchesScanner(FormatSettings.getInstance().preparePattern (key), content).matcher ();

            Format.FormatVarNamesRepresentation varNames = patternEntrySet.getValue().getVarNames();
            Format.FormatRegexRepresentation regex = patternEntrySet.getValue().getRegex();
            if (matcher != null && matcher.find()) {
                final String smsAsText = matcher.group ();
                try {
                    new Sms(varNames, new RawMatcherResult(matcher, regex, smsAsText));
                } catch (ParseException e) {
                    continue;
                }
                break;
            }

        }

        return matcher == null ? null : key;
    }
    
}
