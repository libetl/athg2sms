package org.toilelibre.libe.athg2sms.androidstuff.sms;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.HandlerHolder;
import org.toilelibre.libe.athg2sms.business.convert.ConvertListener;
import org.toilelibre.libe.athg2sms.business.sms.Folder;
import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;

import static org.toilelibre.libe.athg2sms.business.concurrent.ConditionWatcher.weAreAskedToStopNowBecauseOfThe;

public class SmsFinder {

    public <T> List<Sms> pickThemAll(final ContextHolder<T> contextHolder, final HandlerHolder<?> handler, final ConvertListener<T> convertListener, final Condition stopMonitor) {
        final List<Sms> result = new ArrayList<Sms>();

        final Cursor cursorInbox = query(getSmsInboxFolder(), contextHolder);
        final Cursor cursorSent = query(getSmsSentFolder(), contextHolder);

        final List<Sms> inboxElements =
                iterateFor(Folder.INBOX, cursorInbox, contextHolder, handler, convertListener, stopMonitor);

        if (inboxElements == null) {cursorInbox.close();cursorSent.close();return null;}

        final List<Sms> sentElements =
                iterateFor(Folder.SENT, cursorSent, contextHolder, handler, convertListener, stopMonitor);

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

    private List<Sms> iterateFor(final Folder folder, final Cursor cursor, final ContextHolder<?> contextHolder, final HandlerHolder<?> handler, final ConvertListener<?> convertListener, final Condition stopMonitor) {

        if (cursor == null) return Collections.emptyList();

        List<Sms> result = new ArrayList<Sms>(cursor.getCount());

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
                        convertListener.updateProgress(contextHolder.getString(R.string.pickingfrom, folder.getFolderName()),
                                thisMsgIndex, cursor.getCount());
                    }
                }
            });
            Map<Sms.Part, Object> values = new HashMap<Sms.Part, Object>();
            for (String columnName : cursor.getColumnNames()) {
                values.put(Sms.Part.parse(columnName), cursor.getString(cursor.getColumnIndex(columnName)));
            }
            values.put(Sms.Part.FOLDER, folder);
            result.add(new Sms(values));
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
