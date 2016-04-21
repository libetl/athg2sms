package org.toilelibre.libe.athg2sms;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.toilelibre.libe.athg2sms.bp.ConvertListener;
import org.toilelibre.libe.athg2sms.bp.ConvertThread;
import org.toilelibre.libe.athg2sms.settings.SettingsFactory;
import org.toilelibre.libe.athg2sms.status.Done;
import org.toilelibre.libe.athg2sms.status.Error;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProceedActivity extends Activity implements ConvertListener {

	private static String	filename;

	public static String getFilename () {
		return ProceedActivity.filename;
	}

	public static void setFilename (String filename) {
		ProceedActivity.filename = filename;
	}

	private ConvertThread	convert;
	private Handler	      handler;

	private ProgressBar	  progress;

	private TextView	  current;
	private TextView	  inserted;

	public void displayInserted (int inserted, int dupl) {
		this.inserted.setText ("Inserted SMS : " + inserted + " (" + dupl + " duplicate(s))");
	}

	public void end () {
		Intent resultIntent = new Intent (this, Done.class);
		if (this.convert.getException () != null) {
			Error.setLastError (this.convert.getException ());
			resultIntent = new Intent (this, Error.class);
		} else if (this.convert.getInserted () == 0) {
			Error.setLastError (new Exception ("No SMS Imported !"));
			resultIntent = new Intent (this, Error.class);
		}
		this.startActivity (resultIntent);
		this.finish ();
	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		this.setContentView (R.layout.proceed);
		this.progress = (ProgressBar) this.findViewById (R.id.progress);
		this.current = (TextView) this.findViewById (R.id.current);
		this.inserted = (TextView) this.findViewById (R.id.inserted);
		((TextView) this.findViewById (R.id.filename)).setText ("Filename : "
		        + ProceedActivity.filename);
		this.handler = new Handler ();
		final File f = new File (ProceedActivity.filename);
		this.convert = SettingsFactory.asV3 ().getConvertThreadInstance ();
        try {
            Scanner scan = new Scanner(f);
            scan.useDelimiter("\\Z");  
            String content = scan.next(); 
            this.convert.setInputStream (new ByteArrayInputStream (content.getBytes ()));
            this.convert.setConvertListener (this);
            this.convert.setHandler (this.handler);
            this.convert.start ();
        } catch (FileNotFoundException e) {
            throw new RuntimeException (e);
        }  
	}

	public void sayIPrepareTheList (int size) {
		this.progress.setIndeterminate (true);
		this.current.setText ("Preparing write for sms # : " + size);

	}

	public void setMax (int nb) {
		this.progress.setIndeterminate (false);
		this.progress.setMax (nb);
	}

	public void updateProgress (int i, int nb) {
		this.current.setText ("Writing sms # : " + i + " / " + nb);
		this.progress.setProgress (i);
	}

    public int delete (URI uriDelete, String where, String [] strings) {
        return this.getContentResolver ().delete (Uri.parse (uriDelete.toString ()), where, strings);
    }

    public void insert (URI uri, Map<String, Object> values) {
        ContentValues values2 = new ContentValues ();
        for (Entry<String, Object> entry : values.entrySet ()) {
            this.putEntry (values2, entry);
        }
        this.getContentResolver ().insert (Uri.parse (uri.toString ()), values2);
    }

    private void putEntry (ContentValues values, Entry<String, Object> entry) {
        if (entry.getValue () instanceof Boolean) {
            
        }else if (entry.getValue () instanceof Boolean) {
            values.put (entry.getKey (), (Boolean)entry.getValue ());
            
        }else if (entry.getValue () instanceof Byte) {
            values.put (entry.getKey (), (Byte)entry.getValue ());
            
        }else if (entry.getValue () instanceof byte[]) {
            values.put (entry.getKey (), (byte[])entry.getValue ());
            
        }else if (entry.getValue () instanceof Double) {
            values.put (entry.getKey (), (Double)entry.getValue ());
            
        }else if (entry.getValue () instanceof Float) {
            values.put (entry.getKey (), (Float)entry.getValue ());
            
        }else if (entry.getValue () instanceof Integer) {
            values.put (entry.getKey (), (Integer)entry.getValue ());
            
        }else if (entry.getValue () instanceof Long) {
            values.put (entry.getKey (), (Long)entry.getValue ());
            
        }else if (entry.getValue () instanceof Short) {
            values.put (entry.getKey (), (Short)entry.getValue ());
            
        }else if (entry.getValue () instanceof String) {
            values.put (entry.getKey (), (String)entry.getValue ());
            
        }
    }
}
