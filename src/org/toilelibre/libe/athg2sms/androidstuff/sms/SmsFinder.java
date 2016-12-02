package org.toilelibre.libe.athg2sms.androidstuff.sms;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;

import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.HandlerHolder;
import org.toilelibre.libe.athg2sms.business.convert.ConvertListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsFinder {

    public List<Map<String, Object>> pickThemAll( final ContextHolder<?> contextHolder,  final HandlerHolder<?> handler, final ConvertListener convertListener) {
        final ContentResolver contentResolver = contextHolder.get(ContentResolver.class);
        final Cursor cursor = contentResolver.query(getSmsFolder(), null, null, null, null);

        if (cursor == null) return Collections.emptyList();

        List<Map<String, Object>> result = new ArrayList<>(cursor.getCount());

        handler.postForHandler(new Runnable() {
            @Override
            public void run() {
                convertListener.setMax(cursor.getCount());
            }
        });
        cursor.moveToFirst();
        for (int msgIndex = 0 ; msgIndex < cursor.getCount() ; msgIndex++) {
            final int thisMsgIndex = msgIndex;
            handler.postForHandler(new Runnable() {
                @Override
                public void run() {
                    if (!cursor.isClosed()) {
                        convertListener.updateProgress(thisMsgIndex, cursor.getCount());
                    }
                }
            });
            Map<String, Object> values = new HashMap<>();
            for (String columnName : cursor.getColumnNames()) {
                values.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
            }
            result.add(values);
            cursor.moveToNext();
        }

        cursor.close();
        return result;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Uri getSmsFolder() {
        return Build.VERSION.SDK_INT < 19 ? Uri.parse("content://sms/inbox") : Telephony.Sms.Inbox.CONTENT_URI;
    }
}
