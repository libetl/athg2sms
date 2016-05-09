package org.toilelibre.libe.athg2sms;

import java.io.FileNotFoundException;

import org.toilelibre.libe.athg2sms.bp.ConvertListener;
import org.toilelibre.libe.athg2sms.bp.ConvertThread;
import org.toilelibre.libe.athg2sms.bp.DefaultConvertListener;
import org.toilelibre.libe.athg2sms.settings.SettingsFactory;
import org.toilelibre.libe.athg2sms.status.Done;
import org.toilelibre.libe.athg2sms.status.Error;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProceedActivity extends Activity {
    
    private static String filename;
    
    public static String getFilename () {
        return ProceedActivity.filename;
    }
    
    public static void setFilename (final String filename) {
        ProceedActivity.filename = filename;
    }
    
    private Handler handler;
    
    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.proceed);
        ((TextView) this.findViewById (R.id.filename)).setText ("Filename : " + ProceedActivity.filename);
        this.handler = new Handler ();
        
        ConvertThread convert = SettingsFactory.asV4 ().getConvertThreadInstance ();
        ConvertListener convertListener = new DefaultConvertListener (this, Done.class, Error.class, convert, (ProgressBar) this.findViewById (R.id.progress),
                (TextView) this.findViewById (R.id.current), (TextView) this.findViewById (R.id.inserted));
        final String content = this.getContentFromFileName (convert, convertListener);
        if (content == null) {
            return;
        }
        convert.setContentToBeParsed (content);
        convert.setConvertListener (convertListener);
        convert.setHandler (this.handler);
        convert.start ();
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
