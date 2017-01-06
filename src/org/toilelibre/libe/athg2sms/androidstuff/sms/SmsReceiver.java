package org.toilelibre.libe.athg2sms.androidstuff.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.business.convert.Converter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SmsReceiver extends BroadcastReceiver {

    private static final Set<Map<String, Object>> ALREADY_RECEIVED_SMS = new HashSet<Map<String, Object>>();
    @Override
    public void onReceive (Context context, Intent intent) {
        final Object[] pdusObj = (Object[]) intent.getExtras().get("pdus");
        if (pdusObj == null)
            return;

        for (Object pduObj : pdusObj) {

            Map<String, Object> currentMessage = buildFrom(SmsMessage.createFromPdu((byte[]) pduObj));
            try {

                if (!ALREADY_RECEIVED_SMS.contains(currentMessage))
                new SmsInserter ().insert(new URI(Converter.FOLDER + "inbox/"),
                        currentMessage, new ContextHolder<Object>(context));
                ALREADY_RECEIVED_SMS.add(currentMessage);
            } catch (URISyntaxException e) {
            }


        }
    }

    private Map<String, Object> buildFrom(SmsMessage currentMessage) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("address", currentMessage.getOriginatingAddress());
        result.put("date", currentMessage.getTimestampMillis());
        result.put("body", currentMessage.getDisplayMessageBody());
        return result;
    }

}
