package org.toilelibre.libe.athg2sms.status;

import org.toilelibre.libe.athg2sms.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Error extends Activity {

    private static Exception lastError;

    public static Exception getLastError () {
        return Error.lastError;
    }

    public static void setLastError (Object object) {
        Error.lastError = (Exception) object;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.error);
        ((TextView) this.findViewById (R.id.exception)).setText (Error
                .getLastError ().toString ());

        ((Button) this.findViewById (R.id.backtoconvform))
                .setOnClickListener (new OnClickListener () {

                    public void onClick (View v) {
                        Error.this.finish ();
                    }

                });
    }
}
