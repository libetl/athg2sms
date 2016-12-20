package org.toilelibre.libe.athg2sms.androidstuff.ui;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;

import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.HandlerHolder;
import org.toilelibre.libe.athg2sms.business.convert.ConvertException;
import org.toilelibre.libe.athg2sms.business.export.Exporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportService extends IntentService {

    public ExportService() {
        super(ExportService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ProcessRealTimeFeedback convertListener = ProcessRealTimeFeedback.getInstance();
        final String result = new Exporter().export(new ContextHolder<>(this),
                new HandlerHolder<>(convertListener.getHandler()),
                intent.getStringExtra("pattern"), convertListener);

        try {
            final File file = File.createTempFile("athg2sms", ".txt", this.getTargetDir());
            FileWriter fw = new FileWriter(file);
            fw.write(result);
            fw.close();

            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("stopExport")
                    .putExtra("result", file.getAbsolutePath()));
        } catch (IOException e) {
            throw new ConvertException("Exporting the sms did not work because of a problem with the storage", e);
        }

    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public File getTargetDir() {
        return Build.VERSION.SDK_INT < 8 ? this.getCacheDir() : this.getExternalCacheDir();
    }
}
