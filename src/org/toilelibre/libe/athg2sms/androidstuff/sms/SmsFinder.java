package org.toilelibre.libe.athg2sms.androidstuff.sms;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;

import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.HandlerHolder;
import org.toilelibre.libe.athg2sms.actions.ProcessRealTimeFeedback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsFinder {

    public List<Map<String, Object>> pickThemAll( final ContextHolder<?> contextHolder,  final HandlerHolder<?> handler, final ProcessRealTimeFeedback convertListener) {
        final List<Map<String, Object>> result = new ArrayList<>();

        final Cursor cursorInbox = query(getSmsInboxFolder(), contextHolder);
        final Cursor cursorSent = query(getSmsSentFolder(), contextHolder);

        result.addAll(iterateFor("inbox", cursorInbox, handler, convertListener));
        result.addAll(iterateFor("sent", cursorSent, handler, convertListener));

        cursorInbox.close();
        cursorSent.close();
        return result;
    }

    private Cursor query(Uri smsFolder, ContextHolder<?> contextHolder) {
        final ContentResolver contentResolver = contextHolder.get(ContentResolver.class);
        return contentResolver.query(smsFolder, null, null, null, null);
    }

    private List<Map<String, Object>> iterateFor(final String folderName, final Cursor cursor, final HandlerHolder<?> handler, final ProcessRealTimeFeedback convertListener) {

        if (cursor == null) return Collections.emptyList();

        List<Map<String, Object>> result = new ArrayList<>(cursor.getCount());

        cursor.moveToFirst();
        for (int msgIndex = 0 ; msgIndex < cursor.getCount() ; msgIndex++) {
            final int thisMsgIndex = msgIndex;

            handler.postForHandler(new Runnable() {
                @Override
                public void run() {
                    if (!cursor.isClosed()) {
                        convertListener.setMax(cursor.getCount());
                    }
                }
            });
            handler.postForHandler(new Runnable() {
                @Override
                public void run() {
                    if (!cursor.isClosed()) {
                        convertListener.updateProgress("Picking a sms from " + folderName + " : ",
                                thisMsgIndex, cursor.getCount());
                    }
                }
            });
            Map<String, Object> values = new HashMap<>();
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
