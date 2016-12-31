package org.toilelibre.libe.athg2sms.androidstuff.sms;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.ProcessRealTimeFeedback;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.HandlerHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;

import static org.toilelibre.libe.athg2sms.business.concurrent.ConditionWatcher.weAreAskedToStopNowBecauseOfThe;

public class SmsFinder {

    public List<Map<String, Object>> pickThemAll(final ContextHolder<?> contextHolder, final HandlerHolder<?> handler, final ProcessRealTimeFeedback convertListener, final Condition stopMonitor) {
        final List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        final Cursor cursorInbox = query(getSmsInboxFolder(), contextHolder);
        final Cursor cursorSent = query(getSmsSentFolder(), contextHolder);

        final List<Map<String, Object>> inboxElements =
                iterateFor("inbox", cursorInbox, contextHolder, handler, convertListener, stopMonitor);

        if (inboxElements == null) {cursorInbox.close();cursorSent.close();return null;}

        final List<Map<String, Object>> sentElements =
                iterateFor("sent", cursorSent, contextHolder, handler, convertListener, stopMonitor);

        if (sentElements == null) {cursorInbox.close();cursorSent.close();return null;}

        result.addAll(inboxElements);
        result.addAll(sentElements);

        cursorInbox.close();
        cursorSent.close();
        return result;
    }

    private Cursor query(Uri smsFolder, ContextHolder<?> contextHolder) {
        final ContentResolver contentResolver = contextHolder.get(ContentResolver.class);
        return contentResolver.query(smsFolder, null, null, null, null);
    }

    private List<Map<String, Object>> iterateFor(final String folderName, final Cursor cursor, final ContextHolder<?> contextHolder, final HandlerHolder<?> handler, final ProcessRealTimeFeedback convertListener, final Condition stopMonitor) {

        if (cursor == null) return Collections.emptyList();

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(cursor.getCount());

        cursor.moveToFirst();
        handler.postForHandler(new Runnable() {
            @Override
            public void run() {
                if (!cursor.isClosed()) {
                    convertListener.setMax(cursor.getCount());
                }
            }
        });
        for (int msgIndex = 0 ; msgIndex < cursor.getCount() ; msgIndex++) {
            if (weAreAskedToStopNowBecauseOfThe(stopMonitor)) return null;
            final int thisMsgIndex = msgIndex;

            handler.postForHandler(new Runnable() {
                @Override
                public void run() {
                    if (!cursor.isClosed() && thisMsgIndex % 100 == 0) {
                        convertListener.updateProgress(contextHolder.getString(R.string.pickingfrom, folderName),
                                thisMsgIndex, cursor.getCount());
                    }
                }
            });
            Map<String, Object> values = new HashMap<String, Object>();
            for (String columnName : cursor.getColumnNames()) {
                values.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
            }
            values.put("folder", folderName);
            result.add(values);
            cursor.moveToNext();
        }

        return result;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Uri getSmsSentFolder() {
        return Build.VERSION.SDK_INT < 19 ? Uri.parse("content://sms/sent") : Telephony.Sms.Sent.CONTENT_URI;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Uri getSmsInboxFolder() {
        return Build.VERSION.SDK_INT < 19 ? Uri.parse("content://sms/inbox") : Telephony.Sms.Inbox.CONTENT_URI;
    }
}
