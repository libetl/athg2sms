package org.toilelibre.libe.athg2sms;

import java.io.FileNotFoundException;
import java.util.Set;

import org.toilelibre.libe.athg2sms.bp.GuesserThread;
import org.toilelibre.libe.athg2sms.settings.Settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class ConversionFormActivity extends Activity {
    private Handler handler = new Handler ();

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
        final Set<String> setsSet = Settings.getSetsKeySet ();
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
                    intent.setType("text/*");
                    intent.addCategory (Intent.CATEGORY_OPENABLE);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    ConversionFormActivity.this.startActivityForResult (intent, 0);
                }
            }
        });
        this.findViewById (R.id.start).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                final ConversionFormActivity thiz = ConversionFormActivity.this;
                final Intent proceedIntent = new Intent (thiz, ProceedActivity.class);
                ProceedActivity.setFilename (((EditText) thiz.findViewById (R.id.filename)).getText ().toString ());
                ProceedActivity.setPattern (((Spinner) thiz.findViewById (R.id.conversionSet)).getSelectedItem ().toString ());
                thiz.startActivity (proceedIntent);
            }
        });
        this.findViewById (R.id.guessconvset).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                final ConversionFormActivity thiz = ConversionFormActivity.this;
                GuesserThread guesserThread = Settings.getGuesserThread ();
                try {
                    guesserThread.setContentToBeParsed (
                            FileRetriever.getFile (thiz, 
                                    ((EditText) thiz.findViewById (R.id.filename)).getText ().toString ()));
                    guesserThread.setHandler(thiz.handler);
                    guesserThread.setSpinner(((Spinner) thiz.findViewById (R.id.conversionSet)));
                    guesserThread.start ();
                } catch (FileNotFoundException e) {
                }
                
            }
        });
        this.findViewById (R.id.backtomainmenu).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                ConversionFormActivity.this.finish ();
            }

        });
    }

}
