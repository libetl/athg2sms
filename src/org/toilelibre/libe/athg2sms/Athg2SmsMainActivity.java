package org.toilelibre.libe.athg2sms;

import org.toilelibre.libe.athg2sms.help.HelpActivity;
import org.toilelibre.libe.athg2sms.settings.DefaultSettings;
import org.toilelibre.libe.athg2sms.settings.SettingsFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Athg2SmsMainActivity extends Activity {

	private void missingOIFMDialog () {
		final AlertDialog.Builder builder = new AlertDialog.Builder (this);
		builder.setMessage ("Please install OI File Manager first.")
		        .setIcon (android.R.drawable.ic_dialog_alert)
		        .setCancelable (false)
		        .setPositiveButton ("Ok",
		                new DialogInterface.OnClickListener () {
			                public void onClick (DialogInterface dialog, int id) {
				                dialog.dismiss ();
			                }
		                }).show ();
		builder.create ();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		this.setContentView (R.layout.main);
		this.findViewById (R.id.manageconvsets).setOnClickListener (
		        new OnClickListener () {

			        public void onClick (View v) {
				        final Intent intent = new Intent (
				                Athg2SmsMainActivity.this,
				                ConvSetsListActivity.class);
				        Athg2SmsMainActivity.this.startActivity (intent);
			        }
		        });
		this.findViewById (R.id.conversionform).setOnClickListener (
		        new OnClickListener () {

			        public void onClick (View v) {
				        final Intent intent = new Intent (
				                Athg2SmsMainActivity.this,
				                ConversionFormActivity.class);
				        Athg2SmsMainActivity.this.startActivity (intent);
			        }
		        });

		this.findViewById (R.id.helpme).setOnClickListener (
		        new OnClickListener () {

			        public void onClick (View v) {
				        final Intent intent = new Intent (
				                Athg2SmsMainActivity.this, HelpActivity.class);
				        Athg2SmsMainActivity.this.startActivity (intent);
			        }
		        });
		this.findViewById (R.id.exit).setOnClickListener (
		        new OnClickListener () {

			        public void onClick (View v) {
				        DefaultSettings.save (SettingsFactory.common ()
				                .getSets ());
				        Athg2SmsMainActivity.this.finish ();
			        }
		        });

		DefaultSettings.setSp (this.getSharedPreferences ("athg2sms", 0));

		try {
			final Intent intent = new Intent (
			        "org.openintents.action.PICK_FILE");
			this.startActivityForResult (intent, 10);
			this.finishActivity (10);
		} catch (final RuntimeException e) {
			this.missingOIFMDialog ();
			this.findViewById (R.id.conversionform).setEnabled (false);
		}
	}
}
