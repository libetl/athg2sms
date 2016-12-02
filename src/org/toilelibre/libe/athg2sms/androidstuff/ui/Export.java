package org.toilelibre.libe.athg2sms.androidstuff.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.toilelibre.libe.athg2sms.EntryPoint;
import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.androidstuff.api.storage.SharedPreferencesHolder;
import org.toilelibre.libe.athg2sms.business.convert.ConvertListener;
import org.toilelibre.libe.athg2sms.business.export.Exporter;
import org.toilelibre.libe.athg2sms.business.preferences.AppPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import static org.toilelibre.libe.athg2sms.androidstuff.api.storage.PreferencesBinding.BINDING_GLOBAL_NAME;

/**
 * Created by lionel on 30/11/16.
 */
public class Export extends Activity {

    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.proceed);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            this.startExportService();
            return;
        }
        try {
            this.checkPermissions (Manifest.permission.READ_SMS);
        }catch (SecurityException se) {
        }
        this.startExportService();
    }

    private void startExportService() {

        final ProceedHandler handler = new ProceedHandler ((ProgressBar) this.findViewById (R.id.progress),
                (TextView) this.findViewById (R.id.current), (TextView) this.findViewById (R.id.inserted));
        final ConvertListener convertListener = new ProcessRealTimeFeedback(handler);
        final Intent intent = new Intent (this, ExportService.class);

        intent.putExtra("pattern", this.getIntent().getStringExtra("pattern"));

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final Intent sendToIntent = new Intent(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(intent.getStringExtra("result"))))
                        .setType("text/plain");
                startActivity(Intent.createChooser(sendToIntent, "Save as..."));
                Export.this.finish();
                ProcessRealTimeFeedback.unbind();
            }
        }, new IntentFilter("stopExport"));

        if (convertListener.bind() == convertListener) {
            this.startService (intent);
        } else {
            ProcessRealTimeFeedback.getInstance().updateHandler(handler);
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult (int requestCode, String [] permissions, int [] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);

        SharedPreferencesHolder<SharedPreferences> preferences =
                new SharedPreferencesHolder<>(this.getSharedPreferences (BINDING_GLOBAL_NAME, 0));

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                throw new SecurityException ("Permission Denied : " + permission);
            }
        }
        new AppPreferences(preferences).saveAskedPermissions (permissions);

        this.startExportService();
    }

    private void checkPermissions (String... permissions) {
        SharedPreferencesHolder<SharedPreferences> preferences =
                new SharedPreferencesHolder<>(this.getSharedPreferences (BINDING_GLOBAL_NAME, 0));
        if (new AppPreferences(preferences).getAskedPermissions ().containsAll (Arrays.asList (permissions))) {
            onRequestPermissionsResult(0, new String[0], new int[0]);
            return;
        }
        ActivityCompat.requestPermissions(this, permissions, 0);
    }
}
