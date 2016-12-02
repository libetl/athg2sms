package org.toilelibre.libe.athg2sms.androidstuff.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import org.toilelibre.libe.athg2sms.androidstuff.sms.SmsApplicationToggle;
import org.toilelibre.libe.athg2sms.business.convert.ConvertListener;
import org.toilelibre.libe.athg2sms.business.preferences.AppPreferences;

import java.util.Arrays;

import static org.toilelibre.libe.athg2sms.androidstuff.api.storage.PreferencesBinding.BINDING_GLOBAL_NAME;

public class Convert extends Activity {

    @SuppressLint ("InlinedApi")
    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.proceed);
        String filename = this.getIntent().getStringExtra("filename");
        ((TextView) this.findViewById (R.id.filename)).setText ("Filename : " + filename);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            this.startConvertProcess(filename);
            return;
        }
        try {
            this.checkPermissions (Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS,
                    Manifest.permission.SEND_SMS);
        }catch (SecurityException se) {
        }
    }

    private void startConvertProcess (String filename) {

        final ProceedHandler handler = new ProceedHandler ((ProgressBar) this.findViewById (R.id.progress),
                (TextView) this.findViewById (R.id.current), (TextView) this.findViewById (R.id.inserted));
        final ConvertListener convertListener = new ProcessRealTimeFeedback(handler);

        final Intent intent = new Intent (this, ConvertService.class);

        intent.putExtra("pattern", this.getIntent().getStringExtra("pattern"));
        intent.putExtra("filename", filename);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Convert.this.finish();
            }
        }, new IntentFilter("stopConvert"));

        if (convertListener.bind() == convertListener) {
            displayIfDryRun();
            this.startService (intent);
        } else {
            ProcessRealTimeFeedback.getInstance().updateHandler(handler);
        }
    }

    private void displayIfDryRun() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT &&
                !MainMenu.class.getPackage().getName().equals(
                        new SmsApplicationToggle().getDefaultSmsPackage(this))) {
            Toast.makeText(this,
                    "BTW... this app is not toggled to insert SMS messages right now." +
                            "This will be a dry run.", Toast.LENGTH_LONG).show();

        }
    }

    @TargetApi (23)
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
        this.startConvertProcess (this.getIntent().getStringExtra("filename"));
    }

    private void checkPermissions (String... permissions) {
        if (new AppPreferences(new SharedPreferencesHolder<>(this.getSharedPreferences (BINDING_GLOBAL_NAME, 0)))
                .getAskedPermissions ().containsAll (Arrays.asList (permissions))) {
            onRequestPermissionsResult(0, new String[0], new int[0]);
            return;
        }
        ActivityCompat.requestPermissions(this, permissions, 0);
    }
}
