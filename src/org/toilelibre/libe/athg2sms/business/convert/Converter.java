package org.toilelibre.libe.athg2sms.business.convert;

import android.database.sqlite.SQLiteException;
import org.toilelibre.libe.athg2sms.R;
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
import java.text.ParseException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.regex.Matcher;

import static org.toilelibre.libe.athg2sms.business.concurrent.ConditionWatcher.weAreAskedToStopNowBecauseOfThe;

public class Converter {
    public final static String FOLDER = "content://sms/";

    public static class ConversionResult {
        private static final ConversionResult BAD_ONE = new ConversionResult(1, 0, 0, 1);
        private static final ConversionResult NOTHING_TO_SAY = new ConversionResult(0, 0, 0, 0);
        private static final ConversionResult NOT_YET_PRESENT = new ConversionResult(1, 1, 0, 0);
        private static final ConversionResult ALREADY_PRESENT = new ConversionResult(1, 1, 1, 0);
        private int total;
        private int inserted;
        private int duplicated;
        private int failed;

        ConversionResult(int total, int inserted, int duplicated, int failed) {
            this.total = total;
            this.inserted = inserted;
            this.duplicated = duplicated;
            this.failed = failed;
        }

        public ConversionResult with(ConversionResult oldResult) {
            this.inserted = oldResult.inserted + inserted;
            this.duplicated = oldResult.duplicated + duplicated;
            this.failed = oldResult.failed + failed;
            return this;
        }

        public int getTotal() {
            return total;
        }

        public int getInserted() {
            return inserted;
        }

        public int getDuplicated() {
            return duplicated;
        }

        public int getFailed() {
            return failed;
        }
    }

    @SuppressWarnings("unchecked")
    public ConversionResult convertNow (Format format,
                                        String content, ConvertListener<?> convertListener, HandlerHolder<?> convertHandler, ContextHolder<?> contextHolder,
                                        SmsInserter inserter, SmsDeleter deleter, Condition stopMonitor) {
        final ContextHolder<Object> contextHolder1 = (ContextHolder<Object>) contextHolder;
        final ConvertListener<Object> convertListener1 = (ConvertListener<Object>) convertListener;
        final HandlerHolder<Object> convertHandler1 = (HandlerHolder<Object>) convertHandler;
        try {
            return this.runConversion (format, content, convertListener1, convertHandler1, contextHolder1, inserter, deleter, stopMonitor);
        } finally {
            this.end (convertListener, convertHandler);
        }
    }

    private ConversionResult runConversion (Format format, String content, ConvertListener<Object> convertListener, HandlerHolder<Object> convertHandler, ContextHolder<Object> contextHolder,
                                            SmsInserter inserter, SmsDeleter deleter, Condition stopMonitor) {

        PreparedPattern preparedPattern = PreparedPattern.fromFormat(format.getRegex());

        final Matcher matcher = new MatchesScanner(preparedPattern, content).matcher ();

        if (matcher == null) {
            throw new ConvertException (contextHolder.getString(R.string.theSelectedFormatDoesNotWork), new IllegalArgumentException ());
        }

        return this.insertAllMatcherOccurences (format, matcher, convertListener, convertHandler, contextHolder, inserter, deleter, stopMonitor);
    }

    private void dispatchAnotherSmsFoundEvent (final int newSize,
                                               final ConvertListener<Object> convertListener,
                                               final ContextHolder<Object> contextHolder,
                                               final HandlerHolder<?> holder) {
        if (holder == null) return;
        if (newSize % 100 != 0) return;
        holder.postForHandler (new Runnable () {

            public void run () {
                convertListener.sayIPrepareTheList (contextHolder, newSize);
            }

        });
    }

    private void dispatchNewSmsInsertionEvent (final ConversionResult result, final int i2,
                                               final ContextHolder<Object> contextHolder,
                                               final ConvertListener<Object> convertListener,
                                               final HandlerHolder<Object> holder) {
        if (holder == null) return;
        holder.postForHandler (new Runnable () {

            public void run () {
                convertListener.updateProgress (contextHolder.getString(R.string.writingSms), i2, result.total);
                convertListener.displayInserted (contextHolder, result);
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
        for (final Entry<Sms.Part, Object> entry : sms.getValues().entrySet ()) {
            if (entry.getKey () == Sms.Part.FOLDER) {
                continue;
            }
            sb.append (entry.getKey ().getPartName()).append (" = ");
            if (entry.getValue () instanceof String) {
                sb.append ("'").append (entry.getValue ().toString ().replace ("'", "''")).append ("'");
            } else {
                sb.append (entry.getValue ());
            }
            sb.append (" and ");
        }
        return sb.substring (0, sb.length () - " and ".length ());
    }

    private ConversionResult proceedToInsertion (final Format format,
                                                 final RawMatcherResult result,
                                                 final ConvertListener convertListener,
                                                 final ContextHolder<?> contextHolder,
                                                 final SmsInserter inserter,
                                                 final SmsDeleter deleter) {
        if (result.getFolder () == null) return ConversionResult.BAD_ONE;
        int nbDuplicate = 0;
        final URI uri;
        final URI uriDelete;
        try {
            uri = new URI (Converter.FOLDER + result.getFolder ().getFolderName() + "/");
            uriDelete = new URI (Converter.FOLDER);
        } catch (final URISyntaxException e) {
            throw new ConvertException ("Cannot happen", e);
        }
        try {
            final Format.FormatVarNamesRepresentation varNames = format.getVarNames();
            final Sms sms = new Sms(varNames, result);
            if (sms.isEmpty ()) return ConversionResult.BAD_ONE;
            final String where = this.getWhere (sms);
            try {
                convertListener.delete(uriDelete, where, new String[0]);
                if (deleter != null)nbDuplicate += deleter.delete(uriDelete, where, new String [0], contextHolder);
            }catch (SQLiteException sqe){/*Just ignore it*/}
            convertListener.insert (uri, sms);
            if(inserter != null)inserter.insert (uri, sms.getValues(), contextHolder);
        } catch (final IllegalStateException ise) {
            throw new ConvertException (contextHolder.getString(R.string.problem_while_writing), ise);
        } catch (final ParseException pe) {
            return ConversionResult.BAD_ONE;
        }
        return nbDuplicate == 0 ? ConversionResult.NOT_YET_PRESENT :
                nbDuplicate == 1 ? ConversionResult.ALREADY_PRESENT :
                        new ConversionResult(1, 1, nbDuplicate, 0);
    }

    private ConversionResult insertAllMatcherOccurences (Format format, Matcher matcher,
                                                         ConvertListener<Object> convertListener, HandlerHolder<Object> holder,
                                                         ContextHolder<Object> contextHolder, SmsInserter inserter, SmsDeleter deleter,
                                                         Condition stopMonitor) {
        final List<RawMatcherResult> matchedSms = new LinkedList<RawMatcherResult> ();
        while (matcher.find ()) {
            final String smsAsText = matcher.group ();
            matchedSms.add (new RawMatcherResult(matcher, format.getRegex(), smsAsText));
            if (weAreAskedToStopNowBecauseOfThe(stopMonitor)) {
                return ConversionResult.NOTHING_TO_SAY;
            }
            this.dispatchAnotherSmsFoundEvent (matchedSms.size (), convertListener, contextHolder, holder);
        }

        List<RawMatcherResult> duplicatesRemovedSms = new LinkedList<RawMatcherResult>(new HashSet<RawMatcherResult>(matchedSms));
        this.dispatchNumberOfSmsRowsKnownEvent (duplicatesRemovedSms.size (), convertListener, holder);

        ConversionResult result = new ConversionResult(duplicatesRemovedSms.size (), 0, 0, 0);
        for (int i = 0 ; i < duplicatesRemovedSms.size () ; i++) {
            this.dispatchNewSmsInsertionEvent (result, i,
                    contextHolder, convertListener, holder);

            if (weAreAskedToStopNowBecauseOfThe(stopMonitor)) {
                return result;
            }
            result = result.with(this.proceedToInsertion (format, duplicatesRemovedSms.get (i),
                    convertListener, contextHolder, inserter, deleter));
        }

        return result;
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
