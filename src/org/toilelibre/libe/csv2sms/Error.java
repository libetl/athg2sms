package org.toilelibre.libe.csv2sms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class Error extends Activity {

	private static Exception lastError;
	
	
	public static Exception getLastError() {
		return lastError;
	}


	public static void setLastError(Exception lastError) {
		Error.lastError = lastError;
	}


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.error);
		((TextView)this.findViewById(R.id.exception))
		               .setText(Error.getLastError().toString());
		
		((Button)this.findViewById(R.id.again)).setOnClickListener(
				new OnClickListener (){

					public void onClick(View v) {
						Intent intent = new Intent (v.getContext(),
								Csv2SmsMainActivity.class);
						Error.this.startActivity(intent);
					}
					
				});
		((Button)this.findViewById(R.id.exit)).setOnClickListener(
				new OnClickListener (){

					public void onClick(View v) {
						Error.this.finish();
					}
					
				});
	}
}
