package org.toilelibre.libe.athg2sms.androidstuff.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;

import org.toilelibre.libe.athg2sms.actions.Actions;
import org.toilelibre.libe.athg2sms.androidstuff.sms.SmsFinder;
import org.toilelibre.libe.athg2sms.business.convert.ConvertException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExportService extends IntentService {

    private final Lock LOCK = new ReentrantLock();
    private final Condition STOP_MONITOR = LOCK.newCondition();

    public ExportService() {
        super(ExportService.class.getSimpleName());
    }

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
    protected void onHandleIntent(Intent intent) {
        if (intent.getExtras().getString("stopNow") != null) return;
        LOCK.lock();
        try {
            final File targetDir = this.getTargetDir();
            final File tempFile = File.createTempFile("athg2sms", ".txt", targetDir);
            new Actions().exportNow(this, tempFile, new SmsFinder(), new Runnable() {
                @Override
                public void run() {
                    boolean done = true;
                    if (tempFile.length() == 0) {
                        tempFile.delete();
                        done = false;
                    }
                    LocalBroadcastManager.getInstance(ExportService.this).sendBroadcast(new Intent("stopExport")
                            .putExtra("result", done ? tempFile.getAbsolutePath() : null));
                }
            }, intent.getStringExtra("pattern"), STOP_MONITOR);
        } catch (IOException e) {
            throw new ConvertException("error while creating temp file for export", e);
        } finally {
            LOCK.unlock();
        }
    }

    private File getTargetDir() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            return this.getExternalCacheDir();
        }
        return this.getCacheDir();
    }
}
