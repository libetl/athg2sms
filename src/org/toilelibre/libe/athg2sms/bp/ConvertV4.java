package org.toilelibre.libe.athg2sms.bp;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import org.toilelibre.libe.athg2sms.pattern.SmsResult;
import org.toilelibre.libe.athg2sms.settings.SettingsFactory;
import org.toilelibre.libe.athg2sms.settings.SettingsV4;
import org.toilelibre.libe.athg2sms.util.LookForAllMatches;

public class ConvertV4 extends Thread implements ConvertThread {
    private static String    folder = "content://sms/";

    private String           content;
    private ConvertListener  convertListener;
    private ConvertException exception;
    private Object           handler;
    private int              inserted;
    private final SettingsV4 settings;

    public ConvertV4 () {
        super ();
        this.inserted = 0;
        this.exception = null;
        this.settings = SettingsFactory.asV4 ();
    }

    private Map<String, Object> buildMessageFromString (final String folder, final SmsResult sms) {
        final List<String> varNames = this.settings.getVarNamesForConvSet ();
        final Map<String, Object> values = new HashMap<String, Object> ();
        for (int i = 0 ; i < varNames.size () ; i++) {
            final String var = varNames.get (i);
            final String val = sms.group (i);
            if (!var.startsWith ("date")) {
                values.put (var, val);
            } else {
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
                    throw new ConvertException ("Problem while trying to build a Sms from a text row", e);
                }
            }
        }
        return values;
    }

    private void dispatchAnotherSmsFoundEvent (final int newSize) {
        if (this.handler instanceof android.os.Handler) {
            ((android.os.Handler) this.handler).post (new Runnable () {

                public void run () {
                    ConvertV4.this.convertListener.sayIPrepareTheList (newSize);
                }

            });
        }
    }

    private void dispatchNewSmsInsertionEvent (final int nb, final int dupl, final int ins, final int i2) {
        if (this.handler instanceof android.os.Handler) {
            ((android.os.Handler) this.handler).post (new Runnable () {

                public void run () {
                    ConvertV4.this.convertListener.updateProgress (i2, nb);
                    ConvertV4.this.convertListener.displayInserted (ins, dupl);
                }

            });
        }
    }

    private void dispatchNumberOfSmsRowsKnownEvent (final int nb) {
        if (this.handler instanceof android.os.Handler) {
            ((android.os.Handler) this.handler).post (new Runnable () {

                public void run () {
                    ConvertV4.this.convertListener.setMax (nb);
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
            if ("folder".equals (entry.getKey ())) {
                continue;
            }
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
        final String suffix = sms.getFolder ();
        if (suffix != null) {
            this.inserted++;
            final URI uri;
            final URI uriDelete;
            try {
                uri = new URI (ConvertV4.folder + suffix + "/");
                uriDelete = new URI (ConvertV4.folder);
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
        try {
            this.convertNow ();
        } catch (final ConvertException ce) {
            this.exception = ce;
        } finally {
            this.end ();
        }
    }

    public void convertNow () {
        this.settings.makePatterns ();
        final LookForAllMatches lfam = new LookForAllMatches (this.content, this.settings);
        final List<SmsResult> matchedSms = new LinkedList<SmsResult> ();
        final Matcher matcher = lfam.matcher ();
        if (matcher == null) {
            throw new ConvertException ("The selected conversion set does not work, sorry", new IllegalArgumentException ());
        }
        while (matcher.find ()) {
            final String smsAsText = matcher.group ();
            matchedSms.add (new SmsResult (this.settings, matcher, smsAsText));
            this.dispatchAnotherSmsFoundEvent (matchedSms.size ());
        }
        final int nb = matchedSms.size ();
        int nbDuplicate = 0;

        this.dispatchNumberOfSmsRowsKnownEvent (nb);

        for (int i = 0 ; i < matchedSms.size () ; i++) {
            final int dupl = nbDuplicate;
            final int ins = this.inserted;
            final SmsResult sms = matchedSms.get (i);
            final int i2 = i;
            this.dispatchNewSmsInsertionEvent (nb, dupl, ins, i2);

            nbDuplicate += this.proceedToInsertion (sms);
        }

    }

    private void end () {
        if (this.handler instanceof android.os.Handler) {
            ((android.os.Handler) this.handler).post (new Runnable () {

                public void run () {
                    ConvertV4.this.convertListener.end ();
                }

            });
        }

    }

    public void setContentToBeParsed (final String f) {
        this.content = f;
    }

    public void setConvertListener (final ConvertListener cl) {
        this.convertListener = cl;
    }

    public void setHandler (final Object handler) {
        this.handler = handler;
    }

}