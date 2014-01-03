package org.toilelibre.libe.athg2sms;

import org.toilelibre.libe.athg2sms.help.HelpActivity;
import org.toilelibre.libe.athg2sms.kitkatwrapper.Sms;
import org.toilelibre.libe.athg2sms.settings.DefaultSettings;
import org.toilelibre.libe.athg2sms.settings.SettingsFactory;

import android.annotation.SuppressLint;
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
				        Intent intent = new Intent(Intent.ACTION_MAIN);
				        intent.addCategory(Intent.CATEGORY_HOME);
				        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        startActivity(intent);
			        }
		        });
		this.findViewById (R.id.toggledefaultapp).setOnClickListener (
		        new OnClickListener () {

			        @SuppressLint ("NewApi")
			        public void onClick (View v) {
				        if (android.os.Build.VERSION.SDK_INT >= 19) {
					        final String myPackageName = Athg2SmsMainActivity.this
					                .getPackageName ();
					        if (!Sms.getDefaultSmsPackage (
					                Athg2SmsMainActivity.this).equals (
					                myPackageName)) {
						        DefaultSettings.saveDefaultSmsApp (Sms
						                .getDefaultSmsPackage (Athg2SmsMainActivity.this));
						        final Intent intentSetDefault = new Intent (
						        		Sms.Intents.ACTION_CHANGE_DEFAULT);
						        intentSetDefault
						                .putExtra (
						                		Sms.Intents.EXTRA_PACKAGE_NAME,
						                        myPackageName);
						        Athg2SmsMainActivity.this
						                .startActivity (intentSetDefault);
					        } else {
						        final String packageName = DefaultSettings
						                .getDefaultSmsApp ();
						        final Intent intentSetDefault = new Intent (
						        		Sms.Intents.ACTION_CHANGE_DEFAULT);
						        intentSetDefault
						                .putExtra (
						                		Sms.Intents.EXTRA_PACKAGE_NAME,
						                        packageName);
						        Athg2SmsMainActivity.this
						                .startActivity (intentSetDefault);

					        }
				        }

			        }
		        });

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
