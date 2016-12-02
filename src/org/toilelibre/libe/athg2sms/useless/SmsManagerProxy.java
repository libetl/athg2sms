package org.toilelibre.libe.athg2sms.useless;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import org.toilelibre.libe.athg2sms.business.convert.ConvertException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class SmsManagerProxy {

    public static void insert (Context context, URI uri, Map<String, Object> values) {
        try {
            SmsManagerProxy.insertInternal (context, uri, values);
        } catch (NoSuchMethodException e) {
            Log.e ("Athg2Sms", "error while inserting", e);
            throw new ConvertException("error while inserting", e);
        } catch (ClassNotFoundException e) {
            Log.e ("Athg2Sms", "error while inserting", e);
            throw new ConvertException ("error while inserting", e);
        } catch (IllegalAccessException e) {
            Log.e ("Athg2Sms", "error while inserting", e);
            throw new ConvertException ("error while inserting", e);
        } catch (IllegalArgumentException e) {
            Log.e ("Athg2Sms", "error while inserting", e);
            throw new ConvertException ("error while inserting", e);
        } catch (InvocationTargetException e) {
            Log.e ("Athg2Sms", "error while inserting", e);
            throw new ConvertException ("error while inserting", e);
        }

    }

    private static void insertInternal (Context context, URI uri, Map<String, Object> values)
            throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String sender = values.get ("address").toString ();
        String body = values.get ("body").toString ();
        byte [] pdu = null;
        byte [] scBytes = PhoneNumberUtils.networkPortionToCalledPartyBCD ("0000000000");
        byte [] senderBytes = PhoneNumberUtils.networkPortionToCalledPartyBCD (sender);
        int lsmcs = scBytes.length;
        byte [] dateBytes = new byte [7];
        Calendar calendar = new GregorianCalendar ();
        dateBytes [0] = reverseByte ((byte) (calendar.get (Calendar.YEAR)));
        dateBytes [1] = reverseByte ((byte) (calendar.get (Calendar.MONTH) + 1));
        dateBytes [2] = reverseByte ((byte) (calendar.get (Calendar.DAY_OF_MONTH)));
        dateBytes [3] = reverseByte ((byte) (calendar.get (Calendar.HOUR_OF_DAY)));
        dateBytes [4] = reverseByte ((byte) (calendar.get (Calendar.MINUTE)));
        dateBytes [5] = reverseByte ((byte) (calendar.get (Calendar.SECOND)));
        dateBytes [6] = reverseByte ((byte) ( (calendar.get (Calendar.ZONE_OFFSET) + calendar.get (Calendar.DST_OFFSET)) / (60 * 1000 * 15)));
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream ();
            bo.write (lsmcs);
            bo.write (scBytes);
            bo.write (0x04);
            bo.write ((byte) sender.length ());
            bo.write (senderBytes);
            bo.write (0x00);
            bo.write (0x00); // encoding: 0 for default 7bit
            bo.write (dateBytes);
            String sReflectedClassName = "com.android.internal.telephony.GsmAlphabet";
            Class<?> cReflectedNFCExtras = Class.forName (sReflectedClassName);
            Method stringToGsm7BitPacked = cReflectedNFCExtras.getMethod ("stringToGsm7BitPacked", new Class [] { String.class });
            stringToGsm7BitPacked.setAccessible (true);
            byte [] bodybytes = (byte []) stringToGsm7BitPacked.invoke (null, body);
            bo.write (bodybytes);

            pdu = bo.toByteArray ();
        } catch (IOException e) {
        }

        Intent intent = new Intent ();
        intent.setClassName ("com.android.mms", "com.android.mms.transaction.SmsReceiverService");
        intent.setAction ("android.provider.Telephony.SMS_RECEIVED");
        intent.putExtra ("pdus", new Object [] { pdu });
        intent.putExtra ("format", "3gpp");
        context.startService (intent);
    }

    private static byte reverseByte (byte x) {
        int intSize = 8;
        byte y = 0;
        for (int position = intSize - 1 ; position > 0 ; position--) {
            y += ( (x & 1) << position);
            x >>= 1;
        }
        return y;
    }

}
