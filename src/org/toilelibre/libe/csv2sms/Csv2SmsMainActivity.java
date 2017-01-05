package org.toilelibre.libe.csv2sms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Csv2SmsMainActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Intent intent = new Intent("org.openintents.action.PICK_FILE");
		intent.putExtra ("org.openintents.extra.TITLE", "Nokia Csv -> Sms");
		intent.putExtra ("org.openintents.extra.BUTTON_TEXT", "Proceed");
		this.startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1){
		  Intent proceedIntent = new Intent (this, ProceedActivity.class);
		  ProceedActivity.setFilename (data.getDataString().substring(7));
		  this.startActivityForResult(proceedIntent, 2);
		}else {
			this.finish ();
		}
	}

}
