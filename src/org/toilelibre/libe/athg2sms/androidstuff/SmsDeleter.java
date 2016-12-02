package org.toilelibre.libe.athg2sms.androidstuff;

import android.content.ContentResolver;
import android.net.Uri;

import java.net.URI;

public class SmsDeleter {
    public int delete(URI uri, String where, String[] strings, ContextHolder<?> context) {
        return context.get(ContentResolver.class).delete(Uri.parse(uri.toString()), where, strings);
    }
}
