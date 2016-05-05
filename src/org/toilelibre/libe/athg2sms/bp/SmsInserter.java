package org.toilelibre.libe.athg2sms.bp;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;

import org.toilelibre.libe.athg2sms.util.SMS2PDU;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsManager;

public class SmsInserter {
    
    private static void putEntry (final ContentValues values, final Entry<String, Object> entry) {
        if (entry.getValue () instanceof Boolean) {
            values.put (entry.getKey (), (Boolean) entry.getValue ());
            
        } else if (entry.getValue () instanceof Byte) {
            values.put (entry.getKey (), (Byte) entry.getValue ());
            
        } else if (entry.getValue () instanceof byte []) {
            values.put (entry.getKey (), (byte []) entry.getValue ());
            
        } else if (entry.getValue () instanceof Double) {
            values.put (entry.getKey (), (Double) entry.getValue ());
            
        } else if (entry.getValue () instanceof Float) {
            values.put (entry.getKey (), (Float) entry.getValue ());
            
        } else if (entry.getValue () instanceof Integer) {
            values.put (entry.getKey (), (Integer) entry.getValue ());
            
        } else if (entry.getValue () instanceof Long) {
            values.put (entry.getKey (), (Long) entry.getValue ());
            
        } else if (entry.getValue () instanceof Short) {
            values.put (entry.getKey (), (Short) entry.getValue ());
            
        } else if (entry.getValue () instanceof String) {
            values.put (entry.getKey (), (String) entry.getValue ());
        }
    }
    
    @SuppressLint ("NewApi")
    public static void insert (URI uri, Map<String, Object> values, Context context, ContentResolver contentResolver) {
        
        if (Build.VERSION.SDK_INT < 10 * Build.VERSION_CODES.LOLLIPOP) {
            final ContentValues values2 = new ContentValues ();
            for (final Entry<String, Object> entry : values.entrySet ()) {
                if (!"folder".equals (entry.getKey ())) {
                    SmsInserter.putEntry (values2, entry);
                }
            }
            contentResolver.insert (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                    ? ("sent".equals (values.get ("folder")) ? Telephony.Sms.Sent.CONTENT_URI : Telephony.Sms.Inbox.CONTENT_URI) : Uri.parse (uri.toString ()), values2);
        }else {
            byte [] pdu = SMS2PDU.getPDU (values.get ("address").toString (), values.get ("body").toString (), 
                    values.get ("address").toString ().trim().charAt (0) != '+');
            Intent receivedIntent = getReceivedIntent (pdu);
            PendingIntent pendingIntent = getPendingIntent (context, receivedIntent);
            context.sendBroadcast (receivedIntent);
            SmsManager.getDefault ().injectSmsPdu (pdu, "3gpp2", pendingIntent);
        }
        
    }

    private static Intent getReceivedIntent (byte [] pdu) {
        Intent intent = new Intent ();
        intent.setClassName("com.android.mms", ".transaction.PrivilegedSmsReceiver");
        intent.setAction("android.provider.Telephony.SMS_DELIVER");
        intent.putExtra("pdus", new Object[] { pdu });
        intent.putExtra("format", "3gpp");
        return intent;
    }
    
    @SuppressLint ("NewApi")
    private static PendingIntent getPendingIntent (Context context, Intent intent) {
        return PendingIntent.getBroadcast (context, 1, intent, PendingIntent.FLAG_ONE_SHOT);
    }
}
