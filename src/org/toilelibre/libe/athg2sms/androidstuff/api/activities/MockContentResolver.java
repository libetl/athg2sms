package org.toilelibre.libe.athg2sms.androidstuff.api.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
public class MockContentResolver extends ContentResolver {
    public MockContentResolver(){this(null); }
    public MockContentResolver(Context context) {super(context); }

    @Override
    public void notifyChange(Uri uri,
                             ContentObserver observer,
                             boolean syncToNetwork) { }
}