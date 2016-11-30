package org.toilelibre.libe.athg2sms.business.sms;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

public class SmsFinder {

    public Cursor pickThemAll(ContentResolver contentResolver) {
        return contentResolver.query(Telephony.Sms.Inbox.CONTENT_URI, null, null, null, null);
    }
}
