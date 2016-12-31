package org.toilelibre.libe.athg2sms.androidstuff.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.toilelibre.libe.athg2sms.actions.Actions;
import org.toilelibre.libe.athg2sms.actions.ErrorHandler;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConvertService extends IntentService {

    private final Lock LOCK = new ReentrantLock();
    private final Condition STOP_MONITOR = LOCK.newCondition();

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

    private Runnable abort = new Runnable() {
        @Override
        public void run() {
            LocalBroadcastManager.getInstance(ConvertService.this).sendBroadcast(new Intent ("abortConvert"));
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getExtras().getString("stopNow") != null) {
            LOCK.lock();
            STOP_MONITOR.signalAll();
            LOCK.unlock();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        if (intent.getExtras().getString("stopNow") != null) return;
        LOCK.lock();
        try {
            new Actions().runConversionNow(new ContextHolder<Object>(this), done, error, abort, intent.getStringExtra("filename"), intent.getStringExtra("pattern"), STOP_MONITOR);
        } finally {
            LOCK.unlock();
        }
    }
}

