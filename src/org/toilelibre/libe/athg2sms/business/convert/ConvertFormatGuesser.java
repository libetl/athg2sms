package org.toilelibre.libe.athg2sms.business.convert;

import org.toilelibre.libe.athg2sms.business.pattern.Format;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;
import org.toilelibre.libe.athg2sms.business.pattern.PreparedPattern;
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
                boolean readOne = false;

                while (!readOne && matcher.find()) {
                    final String smsAsText = matcher.group ();
                    try {
                        new Sms(varNames, new RawMatcherResult(matcher, regex, smsAsText));
                        readOne = true;
                    } catch (ParseException e) {
                    }
                }
                if (readOne) {
                    break;
                }
            }

        }

        return matcher == null ? null : key;
    }

    public String getFailedGuessDetailsFor(final String content, final String pattern) {
        final String beginning = escapeText(content.substring(0, Math.min(4095, content.length() - 1)));
        final String preparedPattern = escapeRegex(FormatSettings.getInstance().preparePattern (pattern).getValPattern());
        return "curl -X 'POST' " +
                "-H'Content-Type:application/json' " +
                "-d'{\"regex\":\""+preparedPattern+"\",\"flags\":\"i\",\"delimiter\":\"/\",\"flavor\":\"pcre\",\"testString\":\""+beginning+"\"}' " +
                "https://regex101.com/api/regex";
    }

    private String escape(String sequence) {
        return sequence.replaceAll("(?<!\\\\)[0-9]", "0").replaceAll("(?<!\\\\)[A-Z]", "A").replaceAll("(?<!\\\\)[a-z]", "a")
                .replaceAll("\"", "\\\\\"").replaceAll("'", "''")
                .replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r").replaceAll("\\t", "\\\\t");
    }

    private String escapeRegex(String sequence) {
        return escape(sequence).replaceAll("(?<!\\\\)\\\\(?=[a-zA-Z0-9])", "\\\\\\\\");
    }

    private String escapeText(String sequence) {
        return escape(sequence).replaceAll("[^\\x00-\\x7F]", "@").replaceAll("(?<!\\\\)\\\\(?=[^rnt\"])", "\\\\\\\\");
    }

}
