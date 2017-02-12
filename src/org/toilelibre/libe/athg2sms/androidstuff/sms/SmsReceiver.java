package org.toilelibre.libe.athg2sms.androidstuff.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.business.convert.Converter;
import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SmsReceiver extends BroadcastReceiver {

    private static final Set<Map<Sms.Part, Object>> ALREADY_RECEIVED_SMS = new HashSet<Map<Sms.Part, Object>>();
    @Override
    public void onReceive (Context context, Intent intent) {
        final Object[] pdusObj = (Object[]) intent.getExtras().get("pdus");
        if (pdusObj == null)
            return;

        for (Object pduObj : pdusObj) {

            Map<Sms.Part, Object> currentMessage = buildFrom(SmsMessage.createFromPdu((byte[]) pduObj));
            try {

                if (!ALREADY_RECEIVED_SMS.contains(currentMessage))
                new SmsInserter ().insert(new URI(Converter.FOLDER + "inbox/"),
                        currentMessage, new ContextHolder<Object>(context));
                ALREADY_RECEIVED_SMS.add(currentMessage);
            } catch (URISyntaxException e) {
            }


        }
    }

    private Map<Sms.Part, Object> buildFrom(SmsMessage currentMessage) {
        Map<Sms.Part, Object> result = new HashMap<Sms.Part, Object>();
        result.put(Sms.Part.ADDRESS, currentMessage.getOriginatingAddress());
        result.put(Sms.Part.DATE, currentMessage.getTimestampMillis());
        result.put(Sms.Part.BODY, currentMessage.getDisplayMessageBody());
        return result;
    }

}
