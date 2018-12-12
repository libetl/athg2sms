package org.toilelibre.libe.athg2sms.business.sms;

import org.toilelibre.libe.athg2sms.business.pattern.Format;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.toilelibre.libe.athg2sms.business.sms.Sms.Part.ADDRESS;
import static org.toilelibre.libe.athg2sms.business.sms.Sms.Part.BODY;
import static org.toilelibre.libe.athg2sms.business.sms.Sms.Part.CHARSET;
import static org.toilelibre.libe.athg2sms.business.sms.Sms.Part.DATE;
import static org.toilelibre.libe.athg2sms.business.sms.Sms.Part.ENCODING;
import static org.toilelibre.libe.athg2sms.business.sms.Sms.Part.FOLDER;

public class Sms {

    public enum Part {
        ADDRESS, DATE, LOCALTIMESTAMP, ENCODING, CHARSET, BODY, FOLDER, UNKNOWN;

        public static Part parse(String input) {
            if (input.startsWith ("date")) {
                return DATE;
            }
            input = input.replaceAll("^inbox:", "").replaceAll("^sent:", "");
            try {
                return Part.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException iae) {
                return UNKNOWN;
            }
        }

        public static String [] asString () {
            List<String> partsAsString = new ArrayList<String>(Part.values().length - 2);
            for (Part part : Part.values()) {
                if (part != UNKNOWN && part != FOLDER && part != CHARSET
                        && part != ENCODING && part != LOCALTIMESTAMP) {
                    partsAsString.add(part.getPartName());
                }
            }
            return partsAsString.toArray(new String [partsAsString.size()]);
        }

        public String getPartName() {
            return this.name().toLowerCase();
        }
    }

    private static class IntermediateValue {
        String additionalInfo;
        String value;

        IntermediateValue(String additionalInfo, String value) {
            this.additionalInfo = additionalInfo.replaceAll("^inbox:", "").replaceAll("^sent:", "").replaceAll("^date", "");
            this.value = value;
        }
    }

    private final Map<Part, Object> values = new HashMap<Part, Object>();

    public Sms(Map<Part, Object> valuesToPick) {
        this.values.putAll(valuesToPick);
    }

    public Sms(Format.FormatVarNamesRepresentation varNames, RawMatcherResult result) throws ParseException {
        Map<Part, IntermediateValue> intermediateValues = new HashMap<Part, IntermediateValue>();

        for (int i = 0 ; i < varNames.size () ; i++) {
            if (result.group(i) != null)
                intermediateValues.put(Part.parse(varNames.getVarNames().get(i)), new IntermediateValue(varNames.getVarNames().get(i), result.group(i)));
        }

        values.putAll(fillInValues(intermediateValues));
        values.put (FOLDER, result.getFolder());
    }


    private long fromMicrosoftTimestamp(long value) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(value / 10000);
        calendar.add(Calendar.YEAR, -369);
        //there are 369 years difference between unix timestamp and microsoft timestamp
        return calendar.getTimeInMillis();
    }

    private Map<Part, Object> fillInValues(Map<Part, IntermediateValue> intermediateValues) throws ParseException {
        final Map<Part, Object> result = new HashMap<Part, Object>();
        boolean quotedPrintable = "QUOTED-PRINTABLE".equalsIgnoreCase (
                String.valueOf(intermediateValues.get(ENCODING) == null ? "" : intermediateValues.get(ENCODING).value));

        for (Map.Entry<Part, IntermediateValue> entry : intermediateValues.entrySet()) {
            if (Arrays.asList(ENCODING, CHARSET).contains(entry.getKey())) continue;

            switch (entry.getKey()) {
                case LOCALTIMESTAMP:
                    result.put(DATE, fromMicrosoftTimestamp(Long.parseLong(entry.getValue().value)));
                    break;
                case DATE:
                    if (entry.getValue().additionalInfo.length() == 0)
                        try {
                            result.put(DATE, Long.parseLong(entry.getValue().value));
                        }catch (NumberFormatException nfe) {
                            try {
                                result.put(DATE, Double.valueOf(entry.getValue().value).longValue());
                            } catch (NumberFormatException nfe2) {
                                throw new ParseException("Could not parse a date", 0);
                            }
                        }
                    else {
                        final SimpleDateFormat dateFormat = new SimpleDateFormat(entry.getValue().additionalInfo, Locale.US);
                        result.put (DATE, dateFormat.parse (entry.getValue().value).getTime ());
                    }
                    break;
                case BODY:
                    if (quotedPrintable)
                        result.put (BODY, new String (new QuotedPrintable().decodeQuotedPrintable (entry.getValue().value.getBytes ())));
                    else result.put(BODY, entry.getValue().value);
                    break;
                default:
                    result.put (entry.getKey(), entry.getValue().value);
            }
        }
        return result;
    }

    public boolean isEmpty () {
        for (Part value : values.keySet ()) {
            if (values.get (value) != null)
                return false;
        }
        return true;
    }

    public long getDate () {
        return Long.valueOf(values.get(DATE).toString());
    }

    public Folder getFolder () {
        return (Folder) values.get(FOLDER);
    }

    public String getBody () {
        return (String) values.get(BODY);
    }

    public String getCharset () {
        return (String) values.get(CHARSET);
    }

    public String getEncoding () {
        return (String) values.get(ENCODING);
    }

    public Map<Part, Object> getValues () {
        return Collections.unmodifiableMap(this.values);
    }

    public CharSequence getAddress() {
        return values.get(ADDRESS).toString();
    }
}
