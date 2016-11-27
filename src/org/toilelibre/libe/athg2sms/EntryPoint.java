package org.toilelibre.libe.athg2sms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.toilelibre.libe.athg2sms.androidstuff.SmsApplicationToggle;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;
import org.toilelibre.libe.athg2sms.preferences.AppPreferences;
import org.toilelibre.libe.athg2sms.ui.MainMenu;

public class EntryPoint extends Activity {

    @Override
    public void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.notdefaultapp);
    }

    @Override
    protected void onResume () {
        super.onResume ();

        FormatSettings.getInstance().loadFrom(EntryPoint.this.getSharedPreferences ("athg2sms", 0));

        if (android.os.Build.VERSION.SDK_INT < 19) {
            this.startActivity(new Intent(this, MainMenu.class));
            return;
        }
        final String myPackageName = this.getPackageName ();
        if (new SmsApplicationToggle().getDefaultSmsPackage (this).equals (myPackageName)) {
            this.startActivity(new Intent(this, MainMenu.class));
            return;
        }
        // App is not default.
        // Show the "not currently set as the default SMS app" interface
        new AppPreferences(EntryPoint.this.getSharedPreferences ("athg2sms", 0))
                .saveDefaultSmsApp(new SmsApplicationToggle().getDefaultSmsPackage (this));
        final View viewGroup = this.findViewById (R.id.not_default_app);
        viewGroup.setVisibility (View.VISIBLE);

        // Set up a button that allows the user to change the default
        // SMS app
        final Button button = (Button) this.findViewById (R.id.oic);
        button.setOnClickListener (new View.OnClickListener () {
            public void onClick (final View v) {
                final Intent intentMain = new Intent (EntryPoint.this, MainMenu.class);
                EntryPoint.this.startActivity (intentMain);
                EntryPoint.this.finish ();
            }
        });
    }
}
