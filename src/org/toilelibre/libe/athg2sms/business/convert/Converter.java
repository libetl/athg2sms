package org.toilelibre.libe.athg2sms.business.convert;

import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.HandlerHolder;
import org.toilelibre.libe.athg2sms.androidstuff.sms.SmsDeleter;
import org.toilelibre.libe.athg2sms.androidstuff.sms.SmsInserter;
import org.toilelibre.libe.athg2sms.business.pattern.Format;
import org.toilelibre.libe.athg2sms.business.pattern.PreparedPattern;
import org.toilelibre.libe.athg2sms.business.sms.RawMatcherResult;
import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;

public class Converter {
    private final static String FOLDER = "content://sms/";

    public boolean convertNow (Format format,
                               String content, ConvertListener convertListener, HandlerHolder<?> convertHandler, ContextHolder<?> contextHolder,
                               SmsInserter inserter, SmsDeleter deleter) {
        try {
            return this.runConversion (format, content, convertListener, convertHandler, contextHolder, inserter, deleter);
        } finally {
            this.end (convertListener, convertHandler);
        }
    }

    private boolean runConversion (Format format, String content, ConvertListener convertListener, HandlerHolder<?> convertHandler, ContextHolder<?> contextHolder,
                           SmsInserter inserter, SmsDeleter deleter) {

        PreparedPattern preparedPattern = PreparedPattern.fromFormat(format.getRegex());

        final Matcher matcher = new MatchesScanner(preparedPattern, content).matcher ();

        if (matcher == null) {
            throw new ConvertException ("The selected conversion set does not work, sorry", new IllegalArgumentException ());
        }

        return this.insertAllMatcherOccurences (format, matcher, convertListener, convertHandler, contextHolder, inserter, deleter);
    }

    private void dispatchAnotherSmsFoundEvent (final int newSize,
                                               final ConvertListener convertListener,
                                               final HandlerHolder<?> holder) {
        if (holder == null) return;
        holder.postForHandler (new Runnable () {

            public void run () {
                convertListener.sayIPrepareTheList (newSize);
            }

        });
    }

    private void dispatchNewSmsInsertionEvent (final int nb, final int dupl, final int ins, final int i2,
                                               final ConvertListener convertListener,
                                               final HandlerHolder<?> holder) {
        if (holder == null) return;
        holder.postForHandler (new Runnable () {

            public void run () {
                convertListener.updateProgress (i2, nb);
                convertListener.displayInserted (ins, dupl);
            }

        });
    }

    private void dispatchNumberOfSmsRowsKnownEvent (final int nb, final ConvertListener convertListener,
                                                    final HandlerHolder<?> holder) {
        if (holder == null) return;
        holder.postForHandler (new Runnable () {

            public void run () {
                convertListener.setMax (nb);
            }
        });
    }

    private String getWhere (final Sms sms) {
        final StringBuilder sb = new StringBuilder ();
        for (final Entry<String, Object> entry : sms.getValues().entrySet ()) {
            if ("FOLDER".equals (entry.getKey ())) {
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

    private int proceedToInsertionAndReturnNumberOfDuplicates (final Format format,
                                                               final RawMatcherResult result,
                                                               final ConvertListener convertListener,
                                                               final ContextHolder<?> contextHolder,
                                                               final SmsInserter inserter,
                                                               final SmsDeleter deleter) {
        if (result.getFolder () == null) return 0;
        int nbDuplicate = 0;
        final URI uri;
        final URI uriDelete;
        try {
            uri = new URI (Converter.FOLDER + result.getFolder () + "/");
            uriDelete = new URI (Converter.FOLDER);
        } catch (final URISyntaxException e) {
            throw new ConvertException ("Cannot happen", e);
        }
        try {
            final Format.FormatVarNamesRepresentation varNames = format.getVarNames();
            final Sms sms = new Sms(varNames, result);
            if (sms.isEmpty ()) return 0;
            final String where = this.getWhere (sms);
            nbDuplicate += convertListener.delete (uriDelete, where, new String [0]);
            if (deleter != null)deleter.delete(uriDelete, where, new String [0], contextHolder);
            convertListener.insert (uri, sms);
            if(inserter != null)inserter.insert (uri, sms, contextHolder);
        } catch (final IllegalStateException ise) {
            throw new ConvertException ("Problem during one insertion", ise);
        }
        return nbDuplicate;
    }

    private boolean insertAllMatcherOccurences (Format format, Matcher matcher,
                                             ConvertListener convertListener, HandlerHolder<?> holder,
                                             ContextHolder<?> contextHolder, SmsInserter inserter, SmsDeleter deleter) {
        int inserted = 0;
        final List<RawMatcherResult> matchedSms = new LinkedList<> ();
        while (matcher.find ()) {
            final String smsAsText = matcher.group ();
            matchedSms.add (new RawMatcherResult(matcher, format.getRegex(), smsAsText));
            this.dispatchAnotherSmsFoundEvent (matchedSms.size (), convertListener, holder);
        }
        final int numberOfFoundSms = matchedSms.size ();
        int nbDuplicate = 0;

        this.dispatchNumberOfSmsRowsKnownEvent (numberOfFoundSms, convertListener, holder);

        for (int i = 0 ; i < matchedSms.size () ; i++) {
            this.dispatchNewSmsInsertionEvent (numberOfFoundSms, nbDuplicate, inserted, i,
                    convertListener, holder);

            nbDuplicate += this.proceedToInsertionAndReturnNumberOfDuplicates (format, matchedSms.get (i),
                    convertListener, contextHolder, inserter, deleter);
            inserted++;
        }

        return inserted > 0;
    }

    private void end (final ConvertListener convertListener, final HandlerHolder<?> holder) {
        if (holder == null) return;
        holder.postForHandler (new Runnable () {

            public void run () {
                convertListener.end ();
            }

        });
    }

}
