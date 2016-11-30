package org.toilelibre.libe.athg2sms.business.sms;

import org.toilelibre.libe.athg2sms.business.convert.ConvertException;
import org.toilelibre.libe.athg2sms.business.pattern.Format;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Sms {

    private final Map<String, Object> values = new HashMap<String, Object>();

    public Sms(Map<String, Object> valuesToPick) {
        this.values.putAll(valuesToPick);
    }

    public Sms(Format.FormatVarNamesRepresentation varNames, RawMatcherResult result) {
        boolean quotedPrintable = false;
        for (int i = 0 ; i < varNames.size () ; i++) {
            final String var = varNames.getVarNames().get (i);
            final String val = result.group (i);
            if (var.startsWith ("date")) {
                final SimpleDateFormat df = new SimpleDateFormat (var.substring ("date".length ()), Locale.US);
                try {
                    if (values.get ("date") == null) {
                        values.put ("date", df.parse (val).getTime ());
                    } else {
                        long l = Long.parseLong ("" + values.get ("date"));
                        l += df.parse (val).getTime ();
                        values.put ("date", l);
                    }
                } catch (final ParseException e) {
                    throw new ConvertException("Problem while trying to build a sms from a text row", e);
                }
            }else if (var.equals ("encoding") && "QUOTED-PRINTABLE".equalsIgnoreCase (val)) {
                quotedPrintable = true;
            }else if (var.equals ("charset")) {
            }else if (var.equals ("body") && quotedPrintable) {

                values.put (var, new String (new QuotedPrintable().decodeQuotedPrintable (val.getBytes ())));
            }else{
                values.put (var, val);
            }
        }
        values.put ("folder", result.getFolder());
    }

    public long getDate () {
        return Long.valueOf(values.get("date").toString());
    }

    public String getFolder () {
        return (String) values.get("folder");
    }

    public String getBody () {
        return (String) values.get("body");
    }

    public String getCharset () {
        return (String) values.get("charset");
    }

    public String getEncoding () {
        return (String) values.get("encoding");
    }

    public Map<String, Object> getValues () {
        return Collections.unmodifiableMap(this.values);
    }

    public CharSequence getAddress() {
        return values.get("address").toString();
    }
}
