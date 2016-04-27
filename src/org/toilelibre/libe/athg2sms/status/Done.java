package org.toilelibre.libe.athg2sms.status;

import org.toilelibre.libe.athg2sms.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Done extends Activity {

    @Override
    public void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.done);

        ((Button) this.findViewById (R.id.backtoconvform)).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                Done.this.finish ();
            }

        });
    }

}
