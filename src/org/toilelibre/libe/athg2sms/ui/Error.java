package org.toilelibre.libe.athg2sms.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import org.toilelibre.libe.athg2sms.business.convert.ConvertException;
import org.toilelibre.libe.athg2sms.R;

public class Error extends Activity {

    @Override
    public void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.error);
        final String errorMessage1 = this.getIntent().getExtras().getString("errorMessage");
        final String errorMessage2 = this.getIntent().getExtras().getString("secondErrorMessage");
        ((TextView) this.findViewById (R.id.exception)).setText (errorMessage1);
        if (errorMessage2 != null) {
            ((TextView) this.findViewById (R.id.exception2)).setText (errorMessage2);
        } else {
            ((TextView) this.findViewById (R.id.exception2)).setText ("That's all we know.");
        }

        this.findViewById (R.id.backtoconvform).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                Error.this.finish ();
            }

        });
    }
}
