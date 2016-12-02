package org.toilelibre.libe.athg2sms.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import org.toilelibre.libe.athg2sms.R;

public class Done extends Activity {

    @Override
    public void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.done);

        this.findViewById (R.id.backtoconvform).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                Done.this.finish ();
            }

        });
    }

}
