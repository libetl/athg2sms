package org.toilelibre.libe.athg2sms.androidstuff.ui;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;

import org.toilelibre.libe.athg2sms.actions.Actions;
import org.toilelibre.libe.athg2sms.business.convert.ConvertException;

import java.io.File;
import java.io.IOException;

public class ExportService extends IntentService {

    public ExportService() {
        super(ExportService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            final File targetDir = this.getTargetDir();
            final File tempFile = File.createTempFile("athg2sms", ".txt", targetDir);
            new Actions().exportNow(tempFile, new Runnable() {
                @Override
                public void run() {
                    LocalBroadcastManager.getInstance(ExportService.this).sendBroadcast(new Intent("stopExport")
                            .putExtra("result", tempFile.getAbsolutePath()));
                }
            }, intent.getStringExtra("pattern"));
        } catch (IOException e) {
            throw new ConvertException("error while creating temp file for export", e);
        }
    }

    private File getTargetDir() {
        return Build.VERSION.SDK_INT < 8 ? this.getCacheDir() : this.getExternalCacheDir();
    }
}
