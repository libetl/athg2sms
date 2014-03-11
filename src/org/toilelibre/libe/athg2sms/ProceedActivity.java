package org.toilelibre.libe.athg2sms;

import java.io.File;

import org.toilelibre.libe.athg2sms.bp.ConvertListener;
import org.toilelibre.libe.athg2sms.bp.ConvertThread;
import org.toilelibre.libe.athg2sms.settings.SettingsFactory;
import org.toilelibre.libe.athg2sms.status.Done;
import org.toilelibre.libe.athg2sms.status.Error;

import android.app.Activity;
import android.content.Intent;
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
		this.convert.setFile (f);
		this.convert.setConvertListener (this);
		this.convert.setHandler (this.handler);
		this.convert.start ();
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
}
