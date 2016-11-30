package org.toilelibre.libe.athg2sms.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.business.export.Exporter;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;
import org.toilelibre.libe.athg2sms.preferences.AppPreferences;

import java.util.Arrays;

public class ExportForm extends Activity {

    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.export);

        this.findViewById (R.id.backtomainmenu).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                ExportForm.this.finish ();
            }

        });

        this.findViewById (R.id.exportfile).setOnClickListener (new OnClickListener () {

            @SuppressLint("InlinedApi")
            public void onClick (final View v) {
                final String patternName = ((Spinner) ExportForm.this.findViewById (R.id.exportPatterns)).getSelectedItem ().toString ();
                final Intent intent = new Intent(Intent.ACTION_SEND)
                                    .putExtra(Intent.EXTRA_TEXT, ExportForm.this.exportSmsWithPattern(patternName))
                                    .setType("text/plain");
                startActivity(Intent.createChooser(intent, "Save as..."));
            }
        });

        ((Spinner) this.findViewById (R.id.exportPatterns)).setAdapter (new ArrayAdapter<> (this, android.R.layout.simple_spinner_item,
                FormatSettings.getInstance().getFormats().keySet().toArray(new String [FormatSettings.getInstance().getFormats().size ()])));

    }

    private String exportSmsWithPattern(String patternName) {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return new Exporter().export (this, patternName);
        }
        try {
            this.checkPermissions (Manifest.permission.READ_SMS);
        }catch (SecurityException se) {
        }
        return new Exporter().export (this, patternName);
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
        exportSmsWithPattern(((Spinner) ExportForm.this.findViewById (R.id.exportPatterns)).getSelectedItem ().toString ());
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
