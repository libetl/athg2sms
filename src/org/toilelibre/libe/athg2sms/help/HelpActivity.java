package org.toilelibre.libe.athg2sms.help;

import org.toilelibre.libe.athg2sms.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class HelpActivity extends Activity {

    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.help);

        this.findViewById (R.id.backtomainmenu).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                HelpActivity.this.finish ();
            }

        });

        ((TextView) this.findViewById (R.id.helpcontent)).setText (this.getString (R.string.helpcontent));
    }

}
