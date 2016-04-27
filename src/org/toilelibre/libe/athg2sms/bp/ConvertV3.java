package org.toilelibre.libe.athg2sms.bp;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.toilelibre.libe.athg2sms.pattern.SmsResult;
import org.toilelibre.libe.athg2sms.settings.SettingsFactory;
import org.toilelibre.libe.athg2sms.settings.SettingsV3;
import org.toilelibre.libe.athg2sms.util.LookForMatchReader;

public class ConvertV3 extends Thread implements ConvertThread {
    private static String folder = "content://sms/";

    private String                 f;
    private ConvertListener        convertListener;
    private final ConvertException exception;
    private Object                 handler;
    private int                    inserted;
    private final SettingsV3       settings;

    public ConvertV3 () {
        super ();
        this.inserted = 0;
        this.exception = null;
        this.settings = SettingsFactory.asV3 ();
    }

    private Map<String, Object> buildMessageFromString (final String key, final SmsResult sms) {
        final Map<String, Object> values = new HashMap<String, Object> ();
        final String format = this.settings.getFormat (key);
        final String pattern = this.settings.getPattern (key);
        final String valPattern = this.settings.getValPattern (key);
        final String value = sms.getCatched ();
        final Pattern pVar = Pattern.compile (pattern);
        final Matcher mVar = pVar.matcher (format);
        final Pattern pVal = Pattern.compile (valPattern);
        final Matcher mVal = pVal.matcher (value);
        DateFormat df = null;
        mVar.find ();
        mVal.find ();
        final int groupCount = mVar.groupCount ();
        for (int i = 1 ; i <= groupCount ; i++) {
            final String var = mVar.group (i);
            final String val = mVal.group (i);
            if (!var.startsWith ("date")) {
                values.put (var, val);
            } else {
                df = new SimpleDateFormat (var.substring ("date".length ()), Locale.US);
                try {
                    if (values.get ("date") == null) {
                        values.put ("date", df.parse (val).getTime ());
                    } else {
                        long l = Long.parseLong ("" + values.get ("date"));
                        l += df.parse (val).getTime ();
                        values.put ("date", l);
                    }
                } catch (final ParseException e) {
                    throw new ConvertException ("Problem while trying to build a Sms from a text row", e);
                }
            }
        }
        return values;
    }

    private String determineSubFolder (final SmsResult sms) {
        return sms.getKey ();
    }

    private void dispatchAnotherSmsFoundEvent (final List<SmsResult> matcher) {
        if (this.handler instanceof android.os.Handler) {
            ((android.os.Handler) this.handler).post (new Runnable () {

                public void run () {
                    ConvertV3.this.convertListener.sayIPrepareTheList (matcher.size ());
                }

            });
        }
    }

    private void dispatchNewSmsInsertionEvent (final int nb, final int dupl, final int ins, final int i2) {
        if (this.handler instanceof android.os.Handler) {
            ((android.os.Handler) this.handler).post (new Runnable () {

                public void run () {
                    ConvertV3.this.convertListener.updateProgress (i2, nb);
                    ConvertV3.this.convertListener.displayInserted (ins, dupl);
                }

            });
        }
    }

    private void dispatchNumberOfSmsRowsKnownEvent (final int nb) {
        if (this.handler instanceof android.os.Handler) {
            ((android.os.Handler) this.handler).post (new Runnable () {

                public void run () {
                    ConvertV3.this.convertListener.setMax (nb);
                }
            });
        }
    }

    public ConvertException getException () {
        return this.exception;
    }

    public int getInserted () {
        return this.inserted;
    }

    private String getWhere (final Map<String, Object> values) {
        final StringBuffer sb = new StringBuffer ();
        for (final Entry<String, Object> entry : values.entrySet ()) {
            sb.append (entry.getKey ());
            sb.append (" = ");
            if (entry.getValue () instanceof String) {
                sb.append ("'");
                sb.append (entry.getValue ().toString ().replace ("'", "''"));
                sb.append ("'");
            } else {
                sb.append (entry.getValue ());
            }
            sb.append (" and ");
        }
        return sb.substring (0, sb.length () - " and ".length ());
    }

    private int proceedToInsertion (final SmsResult sms) {
        int nbDuplicate = 0;
        final String suffix = this.determineSubFolder (sms);
        if (suffix != null) {
            this.inserted++;
            final URI uri;
            final URI uriDelete;
            try {
                uri = new URI (ConvertV3.folder + suffix + "/");
                uriDelete = new URI (ConvertV3.folder);
            } catch (final URISyntaxException e) {
                throw new ConvertException ("Cannot happen", e);
            }
            try {
                final Map<String, Object> values = this.buildMessageFromString (suffix, sms);
                final String where = this.getWhere (values);
                nbDuplicate += this.convertListener.delete (uriDelete, where, new String [0]);
                this.convertListener.insert (uri, values);
            } catch (final IllegalStateException ise) {
                throw new ConvertException ("Problem during one insertion", ise);
            }
        }
        return nbDuplicate;
    }

    @Override
    public void run () {
        this.settings.makePatterns ();
        final StringReader fr = new StringReader (this.f);
        final LookForMatchReader lfsr = new LookForMatchReader (fr, this.settings);
        final List<SmsResult> matcher = new LinkedList<SmsResult> ();
        SmsResult sms = new SmsResult (null, null, null);
        while (sms != null) {
            sms = lfsr.readUntilNextResult ();
            if (sms != null) {
                matcher.add (sms);
            }
            this.dispatchAnotherSmsFoundEvent (matcher);
        }
        final int nb = matcher.size ();
        int nbDuplicate = 0;

        this.dispatchNumberOfSmsRowsKnownEvent (nb);

        for (int i = 0 ; i < matcher.size () ; i++) {
            final int dupl = nbDuplicate;
            final int ins = this.inserted;
            sms = matcher.get (i);
            final int i2 = i;
            this.dispatchNewSmsInsertionEvent (nb, dupl, ins, i2);

            nbDuplicate += this.proceedToInsertion (sms);
        }
        lfsr.close ();

        if (this.handler instanceof android.os.Handler) {
            ((android.os.Handler) this.handler).post (new Runnable () {

                public void run () {
                    ConvertV3.this.convertListener.end ();
                }

            });
        }
    }

    public void setContentToBeParsed (final String f) {
        this.f = f;
    }

    public void setConvertListener (final ConvertListener cl) {
        this.convertListener = cl;
    }

    public void setHandler (final Object handler) {
        this.handler = handler;
    }

}
