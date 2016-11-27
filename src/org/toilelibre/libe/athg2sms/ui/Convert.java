package org.toilelibre.libe.athg2sms.ui;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Arrays;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.toilelibre.libe.athg2sms.androidstuff.SmsApplicationToggle;
import org.toilelibre.libe.athg2sms.business.convert.ConvertException;
import org.toilelibre.libe.athg2sms.business.convert.ConvertListener;
import org.toilelibre.libe.athg2sms.business.convert.Converter;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;
import org.toilelibre.libe.athg2sms.business.sms.SmsDeleter;
import org.toilelibre.libe.athg2sms.business.sms.SmsInserter;
import org.toilelibre.libe.athg2sms.preferences.AppPreferences;
import org.toilelibre.libe.athg2sms.business.files.FileRetriever;
import org.toilelibre.libe.athg2sms.R;

public class Convert extends Activity {

    private ProceedHandler handler;

    @SuppressLint ("InlinedApi")
    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.proceed);
        String filename = this.getIntent().getStringExtra("filename");
        ((TextView) this.findViewById (R.id.filename)).setText ("Filename : " + filename);

        try {
            detectIfAlreadyRunning ();
        } catch (IllegalStateException ise ) {
            return;
        }

        this.handler = new ProceedHandler ((ProgressBar) this.findViewById (R.id.progress),
                (TextView) this.findViewById (R.id.current), (TextView) this.findViewById (R.id.inserted));


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

    private void detectIfAlreadyRunning() {
        Converter converter = null;
        if (converter != null) {
            handler.setProgressBar((ProgressBar) this.findViewById (R.id.progress));
            handler.setCurrent((TextView) this.findViewById (R.id.current));
            handler.setInserted((TextView) this.findViewById (R.id.inserted));
            ConversionRealTimeFeedback convertListener = new ConversionRealTimeFeedback(handler);
            convertListener.setProceedHandler(handler);
            ((ProgressBar) this.findViewById (R.id.progress)).setMax(convertListener.getMax());
        }
    }

    private void startConvertProcess (String filename) {
        ConvertListener convertListener = new ConversionRealTimeFeedback(handler);
        final String content = this.getContentFromFileName (convertListener, filename);
        if (content == null) {
            return;
        }
        final Converter thisConverter = new Converter();
        final ConvertListener thisConvertListener = convertListener;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT &&
                !MainMenu.class.getPackage().getName().equals(
                        new SmsApplicationToggle().getDefaultSmsPackage(this))) {
            Toast.makeText(this,
                    "BTW... this app is not toggled to insert SMS messages right now." +
                            "This will be a dry run.", Toast.LENGTH_LONG).show();

        }
        new Thread(){
            void error(Exception e) {
                Error.setLastError (e);
                Convert.this.startActivity (new Intent(Convert.this, Error.class));
                Convert.this.finish ();
            }

            void done() {
                Convert.this.startActivity (new Intent(Convert.this, Done.class));
                Convert.this.finish ();
            }

            @Override
            public void run() {
                boolean atLeastOneConverted = false;
                try {
                    atLeastOneConverted = thisConverter.convertNow(FormatSettings.getInstance().getFormats().get(
                            Convert.this.getIntent().getStringExtra("pattern")), content,
                            thisConvertListener, handler, Convert.this, new SmsInserter(), new SmsDeleter());
                } catch (ConvertException ce) {
                    error(ce);
                }
                if (!atLeastOneConverted)
                    error(new ParseException("No SMS Imported !\nThe selected conversion set does not match the input", 0));
                else
                    done();
            }
        }.start();
    }

    @TargetApi (23)
    @Override
    public void onRequestPermissionsResult (int requestCode, String [] permissions, int [] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                throw new SecurityException ("Permission Denied : " + permission);
            }
        }
        new AppPreferences(Convert.this.getSharedPreferences ("athg2sms", 0) )
                .saveAskedPermissions (permissions);
        this.startConvertProcess (this.getIntent().getStringExtra("filename"));
    }

    private void checkPermissions (String... permissions) {
        if (new AppPreferences(Convert.this.getSharedPreferences ("athg2sms", 0) )
                .getAskedPermissions ().containsAll (Arrays.asList (permissions))) {
            onRequestPermissionsResult(0, new String[0], new int[0]);
            return;
        }
        ActivityCompat.requestPermissions(this, permissions, 0);
    }

    private String getContentFromFileName (ConvertListener convertListener, String filename) {
        try {
            return FileRetriever.getFile (this, filename);
        } catch (FileNotFoundException e) {
            convertListener.end ();
            return null;
        }
    }
}
