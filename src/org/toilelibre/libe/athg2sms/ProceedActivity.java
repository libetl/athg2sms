package org.toilelibre.libe.athg2sms;

import java.io.FileNotFoundException;
import java.lang.Thread.State;
import java.util.Arrays;

import org.toilelibre.libe.athg2sms.bp.ConvertListener;
import org.toilelibre.libe.athg2sms.bp.ConvertThread;
import org.toilelibre.libe.athg2sms.bp.DefaultConvertListener;
import org.toilelibre.libe.athg2sms.settings.DefaultSettings;
import org.toilelibre.libe.athg2sms.settings.Settings;
import org.toilelibre.libe.athg2sms.status.Done;
import org.toilelibre.libe.athg2sms.status.Error;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProceedActivity extends Activity {
    
    private static String filename;
    private static String pattern;
    
    public static String getFilename () {
        return ProceedActivity.filename;
    }
    
    public static void setFilename (final String filename) {
        ProceedActivity.filename = filename;
    }
    
    public static void setPattern (final String key) {
        ProceedActivity.pattern = key;
    }
    
    private static ProceedHandler handler;
    private static ConvertThread convert;
    private static DefaultConvertListener convertListener;
    
    @SuppressLint ("InlinedApi")
    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.proceed);
        DefaultSettings.ensureSpIsPresent (this.getSharedPreferences ("athg2sms", 0));
        ((TextView) this.findViewById (R.id.filename)).setText ("Filename : " + ProceedActivity.filename);
        
        if (convert != null && convert.getState() == State.RUNNABLE) {
            handler.setProgressBar((ProgressBar) this.findViewById (R.id.progress));
            handler.setCurrent((TextView) this.findViewById (R.id.current));
            handler.setInserted((TextView) this.findViewById (R.id.inserted));
            convert.setHandler (handler);
            convertListener.setProceedHandler(handler);
            ((ProgressBar) this.findViewById (R.id.progress)).setMax(convertListener.getMax());
        	return;
        }
        
        handler = new ProceedHandler ((ProgressBar) this.findViewById (R.id.progress),
                (TextView) this.findViewById (R.id.current), (TextView) this.findViewById (R.id.inserted));
        
        convert = Settings.getConvertThreadInstance ();
        convert.setPatternName (pattern);
        convertListener = new DefaultConvertListener (this, Done.class, Error.class, convert, handler);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            try {
                this.checkPermissions (Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS,
                        Manifest.permission.SEND_SMS);
            }catch (SecurityException se) {
                convert.setException (se);
                convertListener.end ();
                return;
            }
        }else {
            this.startConvertProcess ();
        }
    }

    private void startConvertProcess () {
        final String content = this.getContentFromFileName (convert, convertListener);
        if (content == null) {
            return;
        }
        convert.setContentToBeParsed (content);
        convert.setConvertListener (convertListener);
        convert.setHandler (handler);
        convert.start ();
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
        DefaultSettings.saveAskedPermissions (permissions);
        this.startConvertProcess ();
    }

    private void checkPermissions (String... permissions) {
        if (DefaultSettings.getAskedPermissions ().containsAll (Arrays.asList (permissions))) {
            return;
        }
        ActivityCompat.requestPermissions(this,permissions, 0);
    }

    private String getContentFromFileName (ConvertThread convert, ConvertListener convertListener) {
        try {
            return FileRetriever.getFile (this, filename);
        } catch (FileNotFoundException e) {
            convert.setException (e);
            convertListener.end ();
            return null;
        }
    }
}
