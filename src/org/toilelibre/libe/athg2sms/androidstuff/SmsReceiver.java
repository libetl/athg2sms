package org.toilelibre.libe.athg2sms.androidstuff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive (Context context, Intent intent) {
        Log.i ("athg2sms", "received Sms : " + intent);
    }

}
