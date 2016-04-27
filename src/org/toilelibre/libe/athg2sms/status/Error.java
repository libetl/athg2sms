package org.toilelibre.libe.athg2sms.status;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.bp.ConvertException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Error extends Activity {

    private static ConvertException lastError;

    public static ConvertException getLastError () {
        return Error.lastError;
    }

    public static void setLastError (final Object object) {
        Error.lastError = (ConvertException) object;
    }

    @Override
    public void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.error);
        final ConvertException e = Error.getLastError ();
        ((TextView) this.findViewById (R.id.exception)).setText (e.toString ());
        if (e.getCause () != null) {
            ((TextView) this.findViewById (R.id.exception2)).setText (e.getCause ().toString ());
        } else {
            ((TextView) this.findViewById (R.id.exception2)).setText ("That's all we know.");
        }

        ((Button) this.findViewById (R.id.backtoconvform)).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                Error.this.finish ();
            }

        });
    }
}
