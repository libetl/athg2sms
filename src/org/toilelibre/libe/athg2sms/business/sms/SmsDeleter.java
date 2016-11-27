package org.toilelibre.libe.athg2sms.business.sms;

import android.content.ContentResolver;
import android.net.Uri;

public class SmsDeleter {
    public int delete(Uri uri, String where, String[] strings, ContentResolver contentResolver) {
        return contentResolver.delete(uri, where, strings);
    }
}
