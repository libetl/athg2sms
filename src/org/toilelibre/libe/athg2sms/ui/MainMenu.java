package org.toilelibre.libe.athg2sms.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.SmsApplicationToggle;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;
import org.toilelibre.libe.athg2sms.preferences.AppPreferences;

public class MainMenu extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        this.setContentView (R.layout.main);
        this.findViewById (R.id.manageconvsets).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                final Intent intent = new Intent (MainMenu.this, PatternListing.class);
                MainMenu.this.startActivity (intent);
            }
        });
        this.findViewById (R.id.conversionform).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                final Intent intent = new Intent (MainMenu.this, ConversionForm.class);
                MainMenu.this.startActivity (intent);
            }
        });

        this.findViewById (R.id.export).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                final Intent intent = new Intent (MainMenu.this, ExportForm.class);
                MainMenu.this.startActivity (intent);
            }
        });
        this.findViewById (R.id.exit).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                new AppPreferences(MainMenu.this.getSharedPreferences ("athg2sms", 0)).saveFormats(FormatSettings.getInstance().getFormats());
                MainMenu.this.finish ();
                final Intent intent = new Intent (Intent.ACTION_MAIN);
                intent.addCategory (Intent.CATEGORY_HOME);
                intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                MainMenu.this.startActivity (intent);
            }
        });
        this.findViewById (R.id.toggledefaultapp).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                new SmsApplicationToggle().toggleDefault(MainMenu.this);
            }
        });
    }
}
