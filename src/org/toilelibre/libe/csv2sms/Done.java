package org.toilelibre.libe.csv2sms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class Done extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.done);
		
		((Button)this.findViewById(R.id.again)).setOnClickListener(
				new OnClickListener (){

					public void onClick(View v) {
						Intent intent = new Intent (v.getContext(),
								Csv2SmsMainActivity.class);
						Done.this.startActivityForResult(intent, 0);
					}
					
				});
		((Button)this.findViewById(R.id.exit)).setOnClickListener(
				new OnClickListener (){

					public void onClick(View v) {
						Done.this.finish();
					}
					
				});
	}

	@Override
	protected void onActivityResult(int requestCode, 
			int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.finish();
	}
	
	
}
