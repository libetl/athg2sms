package org.toilelibre.libe.athg2sms.bp;

import java.net.URI;
import java.text.ParseException;
import java.util.Map;

import org.toilelibre.libe.athg2sms.ProceedHandler;
import org.toilelibre.libe.athg2sms.status.Error;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class DefaultConvertListener implements ConvertListener {
    
    private Activity sourceActivity;
    private ConvertThread convert;
    private ProceedHandler proceedHandler;
    private Class<? extends Activity> doneActivityClass;
    private Class<? extends Activity> errorActivityClass;
    private int max;

    public DefaultConvertListener (Activity sourceActivity1, Class<? extends Activity> doneActivityClass1,
            Class<? extends Activity> errorActivityClass1, ConvertThread convert1, ProceedHandler proceedHandler) {
        this.sourceActivity = sourceActivity1;
        this.convert = convert1;
        this.proceedHandler = proceedHandler;
        this.doneActivityClass = doneActivityClass1;
        this.errorActivityClass = errorActivityClass1;
    }

    public int delete (final URI uriDelete, final String where, final String [] strings) {
        return this.sourceActivity.getContentResolver ().delete (Uri.parse (uriDelete.toString ()), where, strings);
    }

    public void displayInserted (final int inserted, final int dupl) {
        this.proceedHandler.getInserted().setText ("Inserted SMS : " + inserted + " (" + dupl + " duplicate(s))");
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
    	this.proceedHandler.getProgressBar().setIndeterminate (true);
    	this.proceedHandler.getCurrent().setText ("Preparing write for sms # : " + size);

    }

    public void setMax (final int nb) {
    	this.proceedHandler.getProgressBar().setIndeterminate (false);
        this.proceedHandler.getProgressBar().setMax (nb);
        this.max = nb;
    }


    public void updateProgress (final int i, final int nb) {
        this.proceedHandler.getCurrent().setText ("Writing sms # : " + i + " / " + nb);
        this.proceedHandler.getProgressBar().setProgress (i);
    }

	public void setProceedHandler(ProceedHandler handler) {
		this.proceedHandler = handler;
	}

	public int getMax() {
		return max;
	}

}
