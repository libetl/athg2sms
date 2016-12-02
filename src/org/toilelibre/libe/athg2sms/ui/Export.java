package org.toilelibre.libe.athg2sms.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.ContextHolder;
import org.toilelibre.libe.athg2sms.business.export.Exporter;
import org.toilelibre.libe.athg2sms.preferences.AppPreferences;

import java.util.Arrays;

/**
 * Created by lionel on 30/11/16.
 */
public class Export extends Activity {

    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.proceed);
        String result = exportSmsWithPattern(this.getIntent().getStringExtra("patternName"));

        final Intent intent = new Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, result)
                .setType("text/plain");
        startActivity(Intent.createChooser(intent, "Save as..."));
    }

    private String exportSmsWithPattern(String patternName) {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return new Exporter().export (new ContextHolder<>(this), patternName);
        }
        try {
            this.checkPermissions (Manifest.permission.READ_SMS);
        }catch (SecurityException se) {
        }
        return new Exporter().export (new ContextHolder<>(this), patternName);
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult (int requestCode, String [] permissions, int [] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                throw new SecurityException ("Permission Denied : " + permission);
            }
        }
        new AppPreferences(this.getSharedPreferences ("athg2sms", 0) )
                .saveAskedPermissions (permissions);

        exportSmsWithPattern(this.getIntent().getStringExtra("patternName"));
    }

    private void checkPermissions (String... permissions) {
        if (new AppPreferences(this.getSharedPreferences ("athg2sms", 0) )
                .getAskedPermissions ().containsAll (Arrays.asList (permissions))) {
            onRequestPermissionsResult(0, new String[0], new int[0]);
            return;
        }
        ActivityCompat.requestPermissions(this, permissions, 0);
    }
}
