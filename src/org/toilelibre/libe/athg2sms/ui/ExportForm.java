package org.toilelibre.libe.athg2sms.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import org.toilelibre.libe.athg2sms.R;

public class ExportForm extends Activity {

    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.export);

        this.findViewById (R.id.backtomainmenu).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                ExportForm.this.finish ();
            }

        });

    }

}
