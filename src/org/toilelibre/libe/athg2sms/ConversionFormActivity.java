package org.toilelibre.libe.athg2sms;

import java.util.Set;

import org.toilelibre.libe.athg2sms.settings.SettingsFactory;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class ConversionFormActivity extends Activity {

    @Override
    protected void onActivityResult (final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (data != null && data.getData () != null && data.getData ().getPath () != null) {
            ((EditText) this.findViewById (R.id.filename)).setText (Build.VERSION.SDK_INT <19 ? data.getData ().getPath () : data.getDataString ());
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.form);
        final Set<String> setsSet = SettingsFactory.common ().getSetsKeySet ();
        final String [] setsArray = new String [setsSet.size ()];
        setsSet.toArray (setsArray);

        ((Spinner) this.findViewById (R.id.conversionSet)).setAdapter (new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, setsArray));
        this.findViewById (R.id.selectfile).setOnClickListener (new OnClickListener () {

            @SuppressLint ("InlinedApi")
            public void onClick (final View v) {
                Intent intent = null;
                if (Build.VERSION.SDK_INT <19){
                    intent = new Intent (Intent.ACTION_GET_CONTENT);
                    intent.setType ("*/*");
                    intent.putExtra ("org.openintents.extra.BUTTON_TEXT", ConversionFormActivity.this.getString (R.string.selectfile));
                    ConversionFormActivity.this.startActivityForResult (intent, 1);
                } else {
                    intent = new Intent(); 
                    intent.setType("text/plain");
                    intent.addCategory (Intent.CATEGORY_OPENABLE);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    ConversionFormActivity.this.startActivityForResult (intent, 0);
                }
            }
        });
        this.findViewById (R.id.start).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                final ConversionFormActivity thiz = ConversionFormActivity.this;
                SettingsFactory.common ().chooseSet (((Spinner) thiz.findViewById (R.id.conversionSet)).getSelectedItem ().toString ());
                final Intent proceedIntent = new Intent (thiz, ProceedActivity.class);
                ProceedActivity.setFilename (((EditText) thiz.findViewById (R.id.filename)).getText ().toString ());
                thiz.startActivity (proceedIntent);
            }
        });
        this.findViewById (R.id.backtomainmenu).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                ConversionFormActivity.this.finish ();
            }

        });
    }

}
