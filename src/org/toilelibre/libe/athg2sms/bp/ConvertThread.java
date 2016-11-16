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
import org.toilelibre.libe.athg2sms.settings.PreparedPattern;
import org.toilelibre.libe.athg2sms.settings.Settings;
import org.toilelibre.libe.athg2sms.util.MatchesScanner;
import org.toilelibre.libe.athg2sms.util.QuotedPrintable;

public class ConvertThread extends Thread {
    private static String    folder = "content://sms/";

    private String           content;
    private ConvertListener  convertListener;
    private ConvertException exception;
    private Object           handler;
    private String           key;
    private int              inserted;

    public ConvertThread () {
        super ();
        this.inserted = 0;
        this.exception = null;
        this.key = null;
    }

    public void convertNow () {
        
        PreparedPattern preparedPattern = Settings.preparePattern (key);
        
        final Matcher matcher = new MatchesScanner (preparedPattern, this.content).matcher ();
        
        if (matcher == null) {
            throw new ConvertException ("The selected conversion set does not work, sorry", new IllegalArgumentException ());
        }
        
        this.insertAllMatcherOccurences (preparedPattern, matcher);

    }

    private Map<String, Object> buildMessageFromString (final String folder, final SmsResult sms) {
        final List<String> varNames = Settings.getVarNames (key);
        final Map<String, Object> values = new HashMap<String, Object> ();
        boolean quotedPrintable = false;
        for (int i = 0 ; i < varNames.size () ; i++) {
            final String var = varNames.get (i);
            final String val = sms.group (i);
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
                    throw new ConvertException ("Problem while trying to build a Sms from a text row", e);
                }
            }else if (var.equals ("encoding") && "QUOTED-PRINTABLE".equalsIgnoreCase (val)) {
                quotedPrintable = true;
            }else if (var.equals ("charset")) {
            }else if (var.equals ("body") && quotedPrintable) {
                
                values.put (var, new String (QuotedPrintable.decodeQuotedPrintable (val.getBytes ())));
            }else{
                values.put (var, val);
            }
        }
        values.put ("folder", sms.getFolder ());
        return values;
    }

    private void dispatchAnotherSmsFoundEvent (final int newSize) {
        if (this.handler instanceof android.os.Handler) {
            ((android.os.Handler) this.handler).post (new Runnable () {

                public void run () {
                    ConvertThread.this.convertListener.sayIPrepareTheList (newSize);
                }

            });
        }
    }

    private void dispatchNewSmsInsertionEvent (final int nb, final int dupl, final int ins, final int i2) {
        if (this.handler instanceof android.os.Handler) {
            ((android.os.Handler) this.handler).post (new Runnable () {

                public void run () {
                    ConvertThread.this.convertListener.updateProgress (i2, nb);
                    ConvertThread.this.convertListener.displayInserted (ins, dupl);
                }

            });
        }
    }

    private void dispatchNumberOfSmsRowsKnownEvent (final int nb) {
        if (this.handler instanceof android.os.Handler) {
            ((android.os.Handler) this.handler).post (new Runnable () {

                public void run () {
                    ConvertThread.this.convertListener.setMax (nb);
                }
            });
        }
    }

    public ConvertException getException () {
        return this.exception;
    }

    public void setException (Exception e) {
        this.exception = new ConvertException ("Problem outside the conversion process : " + e.getMessage (), e);
    }

    public void setPatternName (String key1) {
        this.key = key1;
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

    private int proceedToInsertionAndReturnNumberOfDuplicates (final SmsResult sms) {
        int nbDuplicate = 0;
        final String suffix = sms.getFolder ();
        if (suffix != null) {
            this.inserted++;
            final URI uri;
            final URI uriDelete;
            try {
                uri = new URI (ConvertThread.folder + suffix + "/");
                uriDelete = new URI (ConvertThread.folder);
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

    private void insertAllMatcherOccurences (PreparedPattern preparedPattern, Matcher matcher) {
        final List<SmsResult> matchedSms = new LinkedList<SmsResult> ();
        while (matcher.find ()) {
            final String smsAsText = matcher.group ();
            matchedSms.add (new SmsResult (matcher, preparedPattern, smsAsText));
            this.dispatchAnotherSmsFoundEvent (matchedSms.size ());
        }
        final int numberOfFoundSms = matchedSms.size ();
        int nbDuplicate = 0;

        this.dispatchNumberOfSmsRowsKnownEvent (numberOfFoundSms);

        for (int i = 0 ; i < matchedSms.size () ; i++) {
            this.dispatchNewSmsInsertionEvent (numberOfFoundSms, nbDuplicate, this.inserted, i);

            nbDuplicate += this.proceedToInsertionAndReturnNumberOfDuplicates (matchedSms.get (i));
        }
        
    }

    private void end () {
        if (this.handler instanceof android.os.Handler) {
            ((android.os.Handler) this.handler).post (new Runnable () {

                public void run () {
                    ConvertThread.this.convertListener.end ();
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
