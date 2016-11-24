package org.toilelibre.libe.athg2sms;

import org.toilelibre.libe.athg2sms.help.ExportFormActivity;
import org.toilelibre.libe.athg2sms.kitkatwrapper.Sms;
import org.toilelibre.libe.athg2sms.settings.DefaultSettings;
import org.toilelibre.libe.athg2sms.settings.Settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Athg2SmsMainActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        this.setContentView (R.layout.main);
        this.findViewById (R.id.manageconvsets).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                final Intent intent = new Intent (Athg2SmsMainActivity.this, ConvSetsListActivity.class);
                Athg2SmsMainActivity.this.startActivity (intent);
            }
        });
        this.findViewById (R.id.conversionform).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                final Intent intent = new Intent (Athg2SmsMainActivity.this, ConversionFormActivity.class);
                Athg2SmsMainActivity.this.startActivity (intent);
            }
        });

        this.findViewById (R.id.export).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                final Intent intent = new Intent (Athg2SmsMainActivity.this, ExportFormActivity.class);
                Athg2SmsMainActivity.this.startActivity (intent);
            }
        });
        this.findViewById (R.id.exit).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                DefaultSettings.save (Athg2SmsMainActivity.this.getSharedPreferences ("athg2sms", 0), Settings.getSets ());
                Athg2SmsMainActivity.this.finish ();
                final Intent intent = new Intent (Intent.ACTION_MAIN);
                intent.addCategory (Intent.CATEGORY_HOME);
                intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                Athg2SmsMainActivity.this.startActivity (intent);
            }
        });
        this.findViewById (R.id.toggledefaultapp).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                if (android.os.Build.VERSION.SDK_INT >= 19) {
                    final String myPackageName = Athg2SmsMainActivity.this.getPackageName ();
                    if (!Sms.getDefaultSmsPackage (Athg2SmsMainActivity.this).equals (myPackageName)) {
                        DefaultSettings.saveDefaultSmsApp (Athg2SmsMainActivity.this.getSharedPreferences ("athg2sms", 0), 
                                Sms.getDefaultSmsPackage (Athg2SmsMainActivity.this));
                        final Intent intentSetDefault = new Intent (Sms.Intents.ACTION_CHANGE_DEFAULT);
                        intentSetDefault.putExtra (Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
                        Athg2SmsMainActivity.this.startActivity (intentSetDefault);
                    } else {
                        final String packageName = DefaultSettings.getDefaultSmsApp (
                                Athg2SmsMainActivity.this.getSharedPreferences ("athg2sms", 0));
                        final Intent intentSetDefault = new Intent (Sms.Intents.ACTION_CHANGE_DEFAULT);
                        intentSetDefault.putExtra (Sms.Intents.EXTRA_PACKAGE_NAME, packageName);
                        Athg2SmsMainActivity.this.startActivity (intentSetDefault);

                    }
                }

            }
        });
    }
}
