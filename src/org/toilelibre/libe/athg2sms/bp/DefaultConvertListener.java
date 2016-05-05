package org.toilelibre.libe.athg2sms.bp;

import java.net.URI;
import java.text.ParseException;
import java.util.Map;

import org.toilelibre.libe.athg2sms.status.Error;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DefaultConvertListener implements ConvertListener {
    
    private Activity sourceActivity;
    private ConvertThread convert;
    private ProgressBar progressBar;
    private TextView current;
    private TextView inserted;
    private Class<? extends Activity> doneActivityClass;
    private Class<? extends Activity> errorActivityClass;

    public DefaultConvertListener (Activity sourceActivity1, Class<? extends Activity> doneActivityClass1,
            Class<? extends Activity> errorActivityClass1, ConvertThread convert1, ProgressBar progressBar1, TextView current1, TextView inserted1) {
        this.sourceActivity = sourceActivity1;
        this.convert = convert1;
        this.progressBar = progressBar1;
        this.current = current1;
        this.inserted = inserted1;
        this.doneActivityClass = doneActivityClass1;
        this.errorActivityClass = errorActivityClass1;
    }

    public int delete (final URI uriDelete, final String where, final String [] strings) {
        return this.sourceActivity.getContentResolver ().delete (Uri.parse (uriDelete.toString ()), where, strings);
    }

    public void displayInserted (final int inserted, final int dupl) {
        this.inserted.setText ("Inserted SMS : " + inserted + " (" + dupl + " duplicate(s))");
    }

    public void end () {
        Intent resultIntent = new Intent (this.sourceActivity, doneActivityClass);
        if (this.convert.getException () != null) {
            Error.setLastError (this.convert.getException ());
            resultIntent = new Intent (this.sourceActivity, this.errorActivityClass);
        } else if (this.convert.getInserted () == 0) {
            Error.setLastError (new ParseException ("No SMS Imported !\nThe selected conversion set does not match the input", 0));
            resultIntent = new Intent (this.sourceActivity, this.errorActivityClass);
        }
        this.sourceActivity.startActivity (resultIntent);
        this.sourceActivity.finish ();
    }
    
    @SuppressLint ("NewApi")
    public void insert (final URI uri, final Map<String, Object> values) {
        SmsInserter.insert (uri, values, this.sourceActivity, this.sourceActivity.getContentResolver ());
    }

    public void sayIPrepareTheList (final int size) {
        this.progressBar.setIndeterminate (true);
        this.current.setText ("Preparing write for sms # : " + size);

    }

    public void setMax (final int nb) {
        this.progressBar.setIndeterminate (false);
        this.progressBar.setMax (nb);
    }


    public void updateProgress (final int i, final int nb) {
        this.current.setText ("Writing sms # : " + i + " / " + nb);
        this.progressBar.setProgress (i);
    }

}
