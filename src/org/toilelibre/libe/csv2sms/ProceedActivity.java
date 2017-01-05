package org.toilelibre.libe.csv2sms;

import java.io.File;

import org.toilelibre.libe.csv2sms.bp.Convert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProceedActivity extends Activity {

	private static String filename;
	private Convert convert;
	private Handler handler;

	private ProgressBar progress;
	private TextView current;
	
	public static String getFilename() {
		return filename;
	}

	public static void setFilename(String filename) {
		ProceedActivity.filename = filename;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.proceed);
		this.progress = (ProgressBar) 
        this.findViewById(R.id.progress);
		this.current = (TextView) 
        this.findViewById(R.id.current);
		((TextView)
				this.findViewById(R.id.filename))
				    .setText("Filename : " + 
				    		ProceedActivity.filename);
		this.handler = new Handler();
	}
		
	protected void onStart() {
		super.onStart();
		File f = new File(ProceedActivity.filename);
		this.convert = new Convert (f, this, handler);
		this.convert.start();
	}
	
	public void end(){
		Intent resultIntent = new Intent (this, Done.class);
		if (convert.getException() != null){
		  Error.setLastError(convert.getException());
		  resultIntent  = new Intent (this, Error.class);
		}
		this.startActivityForResult (resultIntent, 3);
	}


	public void setMax (int nb){
	  progress.setMax(nb);
	}
	
	public void updateProgress (int i, int nb){
		current.setText("Writing sms # : " + i + " / " + nb);
		progress.setProgress(i);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.finish();
	}	
}
