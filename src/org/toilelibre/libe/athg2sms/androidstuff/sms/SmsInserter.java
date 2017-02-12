package org.toilelibre.libe.athg2sms.androidstuff.sms;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;

import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.business.sms.Folder;
import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;

public class SmsInserter {

    @SuppressLint ("NewApi")
    public void insert (URI uri, Map<Sms.Part, Object> smsValues, ContextHolder<?> contextHolder) {

        final ContentValues values2 = new ContentValues ();
        for (final Entry<Sms.Part, Object> entry : smsValues.entrySet ()) {
            if (entry.getKey () == Sms.Part.FOLDER) {
                this.putEntry (values2, entry);
            }
        }
        contextHolder.get(ContentResolver.class).insert (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                ? (Folder.SENT == smsValues.get (Sms.Part.FOLDER) ? Telephony.Sms.Sent.CONTENT_URI : Telephony.Sms.Inbox.CONTENT_URI) : Uri.parse (uri.toString ()), values2);


    }

    private void putEntry (final ContentValues values, final Entry<Sms.Part, Object> entry) {
        if (entry.getValue () instanceof Boolean) {
            values.put (entry.getKey ().getPartName(), (Boolean) entry.getValue ());

        } else if (entry.getValue () instanceof Byte) {
            values.put (entry.getKey ().getPartName(), (Byte) entry.getValue ());

        } else if (entry.getValue () instanceof byte []) {
            values.put (entry.getKey ().getPartName(), (byte []) entry.getValue ());

        } else if (entry.getValue () instanceof Double) {
            values.put (entry.getKey ().getPartName(), (Double) entry.getValue ());

        } else if (entry.getValue () instanceof Float) {
            values.put (entry.getKey ().getPartName(), (Float) entry.getValue ());

        } else if (entry.getValue () instanceof Integer) {
            values.put (entry.getKey ().getPartName(), (Integer) entry.getValue ());

        } else if (entry.getValue () instanceof Long) {
            values.put (entry.getKey ().getPartName(), (Long) entry.getValue ());

        } else if (entry.getValue () instanceof Short) {
            values.put (entry.getKey ().getPartName(), (Short) entry.getValue ());

        } else if (entry.getValue () instanceof String) {
            values.put (entry.getKey ().getPartName(), (String) entry.getValue ());
        }
    }
}
