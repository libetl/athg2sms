package org.toilelibre.libe.athg2sms;

import org.toilelibre.libe.athg2sms.settings.DefaultSettings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.Button;

public class Athg2SmsStartActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		this.setContentView (R.layout.notdefaultapp);

		DefaultSettings.setSp (this.getSharedPreferences ("athg2sms", 0));

	}

	@Override
	@SuppressLint ("InlinedApi")
	protected void onResume () {
		super.onResume ();
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			final String myPackageName = this.getPackageName ();
			if (!Telephony.Sms.getDefaultSmsPackage (this).equals (
			        myPackageName)) {
				// App is not default.
				// Show the "not currently set as the default SMS app" interface
				DefaultSettings.saveDefaultSmsApp (Telephony.Sms
				        .getDefaultSmsPackage (this));
				final View viewGroup = this.findViewById (R.id.not_default_app);
				viewGroup.setVisibility (View.VISIBLE);

				// Set up a button that allows the user to change the default
				// SMS app
				final Button button = (Button) this.findViewById (R.id.oic);
				button.setOnClickListener (new View.OnClickListener () {
					public void onClick (View v) {
						final Intent intentMain = new Intent (
						        Athg2SmsStartActivity.this,
						        Athg2SmsMainActivity.class);
						Athg2SmsStartActivity.this.startActivity (intentMain);
						Athg2SmsStartActivity.this.finish ();
					}
				});
			} else {
				this.startActivity (new Intent (this,
				        Athg2SmsMainActivity.class));
			}
		} else {
			this.startActivity (new Intent (this, Athg2SmsMainActivity.class));
		}
	}
}
