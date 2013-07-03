package org.toilelibre.libe.athg2sms;

import java.util.Set;

import org.toilelibre.libe.athg2sms.settings.Settings;
import org.toilelibre.libe.athg2sms.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class ConversionFormActivity extends Activity {

    @Override
    protected void onActivityResult (int requestCode, int resultCode,
            Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (data != null && data.getDataString () != null) {
            ((EditText) this.findViewById (R.id.filename)).setText (data
                    .getDataString ().substring (7));
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.form);
        Set<String> setsSet = Settings.getSetsKeySet ();
        String [] setsArray = new String [setsSet.size ()];
        setsSet.toArray (setsArray);

        ((Spinner) this.findViewById (R.id.conversionSet))
                .setAdapter (new ArrayAdapter<String> (this,
                        android.R.layout.simple_spinner_item, setsArray));
        this.findViewById (R.id.selectfile).setOnClickListener (
                new OnClickListener () {

                    public void onClick (View v) {
                        Intent intent = new Intent (
                                "org.openintents.action.PICK_FILE");
                        intent.putExtra ("org.openintents.extra.TITLE",
                                ConversionFormActivity.this
                                        .getString (R.string.cfoifm_title));
                        intent.putExtra ("org.openintents.extra.BUTTON_TEXT",
                                ConversionFormActivity.this
                                        .getString (R.string.selectfile));
                        ConversionFormActivity.this.startActivityForResult (
                                intent, 1);

                    }
                });
        this.findViewById (R.id.start).setOnClickListener (
                new OnClickListener () {

                    public void onClick (View v) {
                        ConversionFormActivity thiz = ConversionFormActivity.this;
                        Settings.chooseSet ( ((Spinner) thiz
                                .findViewById (R.id.conversionSet))
                                .getSelectedItem ().toString ());
                        Intent proceedIntent = new Intent (thiz,
                                ProceedActivity.class);
                        ProceedActivity.setFilename ( ((EditText) thiz
                                .findViewById (R.id.filename)).getText ()
                                .toString ());
                        thiz.startActivity (proceedIntent);
                    }
                });
        this.findViewById (R.id.backtomainmenu).setOnClickListener (
                new OnClickListener () {

                    public void onClick (View v) {
                        ConversionFormActivity.this.finish ();
                    }

                });
    }

}
