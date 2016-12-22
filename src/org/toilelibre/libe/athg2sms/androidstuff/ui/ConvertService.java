package org.toilelibre.libe.athg2sms.androidstuff.ui;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.toilelibre.libe.athg2sms.actions.Actions;
import org.toilelibre.libe.athg2sms.actions.ErrorHandler;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class ConvertService extends IntentService {

    public ConvertService() {
        super(ConvertService.class.getSimpleName());
    }

    private ErrorHandler error = new ErrorHandler() {
        @Override
        public void run(Exception e) {
            final Intent intent = new Intent(getBaseContext(), Error.class);
            intent.putExtra("errorMessage", e.toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY | FLAG_ACTIVITY_CLEAR_TOP);
            if (e.getCause() != null) {
                intent.putExtra("secondErrorMessage", e.getCause().toString());
            }
            LocalBroadcastManager.getInstance(ConvertService.this).sendBroadcast(new Intent("stopConvert"));
            ConvertService.this.startActivity(intent);
        }
    };

    private Runnable done = new Runnable() {
        @Override
        public void run() {
            final Intent intent = new Intent(getBaseContext(), Done.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY | FLAG_ACTIVITY_CLEAR_TOP);
            LocalBroadcastManager.getInstance(ConvertService.this).sendBroadcast(new Intent ("stopConvert"));
            ConvertService.this.startActivity(intent);
        }
    };

    @Override
    protected void onHandleIntent(final Intent intent) {
        new Actions().runConversionNow(new ContextHolder<>(this), done, error, intent.getStringExtra("filename"), intent.getStringExtra("pattern"));
    }
}

