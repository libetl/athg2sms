package org.toilelibre.libe.athg2sms.androidstuff.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.toilelibre.libe.athg2sms.actions.Actions;
import org.toilelibre.libe.athg2sms.actions.ErrorHandler;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;

public class ConvertService extends IntentService {

    public ConvertService() {
        super(ConvertService.class.getSimpleName());
    }

    private ErrorHandler error = new ErrorHandler() {
        @Override
        public void run(Exception e) {
            Intent intent = new Intent("errorDuringConvert");
            intent.putExtra("errorMessage", e.getMessage());
            if (e.getCause() != null && e.getCause() != e) {
                intent.putExtra("secondErrorMessage", e.getCause().getMessage());
            }
            LocalBroadcastManager.getInstance(ConvertService.this).sendBroadcast(intent);
        }
    };

    private Runnable done = new Runnable() {
        @Override
        public void run() {
            LocalBroadcastManager.getInstance(ConvertService.this).sendBroadcast(new Intent ("stopConvert"));
        }
    };

    @Override
    protected void onHandleIntent(final Intent intent) {
        new Actions().runConversionNow(new ContextHolder<>(this), done, error, intent.getStringExtra("filename"), intent.getStringExtra("pattern"));
    }
}

