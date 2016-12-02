package org.toilelibre.libe.athg2sms.androidstuff;

import android.content.ContentResolver;
import android.provider.Telephony;

public class SmsFinder {

    public CursorHolder<?> pickThemAll(ContextHolder<?> contextHolder) {
        return new CursorHolder<>(contextHolder.get(ContentResolver.class).query(Telephony.Sms.Inbox.CONTENT_URI, null, null, null, null));
    }
}
