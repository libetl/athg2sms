package org.toilelibre.libe.athg2sms.androidstuff.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import org.toilelibre.libe.athg2sms.EntryPoint;
import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.actions.Actions;
import org.toilelibre.libe.athg2sms.androidstuff.api.storage.SharedPreferencesHolder;
import org.toilelibre.libe.athg2sms.androidstuff.sms.SmsApplicationToggle;

import static org.toilelibre.libe.athg2sms.androidstuff.api.storage.PreferencesBinding.BINDING_GLOBAL_NAME;

public class MainMenu extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        final SharedPreferencesHolder<SharedPreferences> preferences =
                new SharedPreferencesHolder<>(this.getSharedPreferences (BINDING_GLOBAL_NAME, 0));

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
                new Actions().saveFormats(preferences);
                MainMenu.this.finish ();
                final Intent intent = new Intent (Intent.ACTION_MAIN);
                intent.addCategory (Intent.CATEGORY_HOME);
                intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                MainMenu.this.startActivity (intent);
                System.exit(0);
            }
        });
        this.findViewById (R.id.toggledefaultapp).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                new SmsApplicationToggle().toggleDefault(MainMenu.this);
            }
        });
    }
}
