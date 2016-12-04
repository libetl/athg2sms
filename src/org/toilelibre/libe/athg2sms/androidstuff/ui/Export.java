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
import android.widget.Toast;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.api.storage.SharedPreferencesHolder;
import org.toilelibre.libe.athg2sms.business.convert.ConvertListener;
import org.toilelibre.libe.athg2sms.business.preferences.AppPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import static android.widget.Toast.LENGTH_LONG;
import static org.toilelibre.libe.athg2sms.androidstuff.api.storage.PreferencesBinding.BINDING_GLOBAL_NAME;

public class Export extends Activity {

    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.proceed);

        retryExportOperation();
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

    private void retryExportOperation() {
        String pattern = this.getIntent().getStringExtra("pattern");
        ((TextView) this.findViewById (R.id.filename)).setText ("Pattern : " + pattern);
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED)){
            this.startExportService();
            return;
        }

        ActivityCompat.requestPermissions(this, new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_SMS}, 0);

    }

    private void startExportService() {

        final ProceedHandler handler = new ProceedHandler ((ProgressBar) this.findViewById (R.id.progress),
                (TextView) this.findViewById (R.id.current), (TextView) this.findViewById (R.id.inserted));
        final ConvertListener convertListener = new ProcessRealTimeFeedback(handler);
        final Intent intent = new Intent (this, ExportService.class);

        intent.putExtra("pattern", this.getIntent().getStringExtra("pattern"));

        sendOrSaveFile();

        if (convertListener.bind() == convertListener) {
            this.startService (intent);
        } else {
            ProcessRealTimeFeedback.getInstance().updateHandler(handler);
        }
    }

    private void sendOrSaveFile() {
        if (Build.VERSION.SDK_INT < 16) {
            copyTempFile();
            return;
        }

        sendTextData();
    }

    private void sendTextData() {
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
    }

    private void copyTempFile() {
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                File parentDir = Export.this.getExternalFilesDir(null);
                if (parentDir == null) {
                    return;
                }
                parentDir.mkdir();
                String destFilename = parentDir.getAbsolutePath() + File.separator + "export" +
                        System.currentTimeMillis() + ".txt";
                try {
                    FileInputStream inStream = new FileInputStream(new File(intent.getStringExtra("result")));
                    FileOutputStream outStream = new FileOutputStream(new File(destFilename));
                    FileChannel inChannel = inStream.getChannel();
                    FileChannel outChannel = outStream.getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                    inStream.close();
                    outStream.close();

                    Toast.makeText(Export.this, "Your Android version is below 4.0. Unable to let you choose between actions." +
                            "Therefore your export was stored in the file " + destFilename, LENGTH_LONG).show();

                } catch (IOException e) {
                    Toast.makeText(Export.this, "Because of a problem while trying to save the export file, the" +
                            " export data was lost. " + e.getMessage(), LENGTH_LONG).show();
                } finally {
                    Export.this.finish();
                    ProcessRealTimeFeedback.unbind();
                }
            }
        }, new IntentFilter("stopExport"));

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
