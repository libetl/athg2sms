package org.toilelibre.libe.athg2sms.androidstuff.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.api.storage.FileRetriever;
import org.toilelibre.libe.athg2sms.business.convert.ConvertFormatGuesser;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;

import java.io.FileNotFoundException;

public class ConversionForm extends Activity {
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
        this.setContentView (R.layout.conversionform);

        ((Spinner) this.findViewById (R.id.conversionSet)).setAdapter (new ArrayAdapter<> (this, android.R.layout.simple_spinner_item,
                FormatSettings.getInstance().getFormats().keySet().toArray(new String [FormatSettings.getInstance().getFormats().size ()])));
        this.findViewById (R.id.selectfile).setOnClickListener (new OnClickListener () {

            @SuppressLint ("InlinedApi")
            public void onClick (final View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT <19){
                    intent = new Intent (Intent.ACTION_GET_CONTENT);
                    intent.setType ("*/*");
                    intent.putExtra ("org.openintents.extra.BUTTON_TEXT", ConversionForm.this.getString (R.string.selectfile));
                    ConversionForm.this.startActivityForResult (intent, 1);
                } else {
                    intent = new Intent();
                    intent.setType("text/*");
                    intent.addCategory (Intent.CATEGORY_OPENABLE);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    ConversionForm.this.startActivityForResult (intent, 0);
                }
            }
        });
        this.findViewById (R.id.start).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                final ConversionForm thiz = ConversionForm.this;
                if (((Spinner) thiz.findViewById (R.id.conversionSet)).getSelectedItem () == null) {
                    return;
                }
                final Intent proceedIntent = new Intent (thiz, Convert.class);
                proceedIntent.putExtra("filename", ((EditText) thiz.findViewById (R.id.filename)).getText ().toString ());
                proceedIntent.putExtra("pattern", ((Spinner) thiz.findViewById (R.id.conversionSet)).getSelectedItem ().toString ());
                thiz.startActivity (proceedIntent);
            }
        });
        this.findViewById (R.id.guessconvset).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                final Context context = ConversionForm.this;
                final Handler handler = ConversionForm.this.handler;
                final Spinner spinner = (Spinner) ConversionForm.this.findViewById (R.id.conversionSet);
                final String file = ((EditText) ConversionForm.this.findViewById (R.id.filename)).getText ().toString ();
                final ProgressBar progressBar =  ((ProgressBar)ConversionForm.this.findViewById (R.id.guessing));
                progressBar.setVisibility(View.VISIBLE);
                new Thread() {
                    @Override
                    public void run() {
                        final String formatKey;
                        final String content;
                        try {
                            content = FileRetriever.getFile (context, file);
                            formatKey = new ConvertFormatGuesser().guessNow(content);
                        } catch (final FileNotFoundException e) {
                            handler.post (new Runnable () {
                                public void run () {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText (context, "No file selected", Toast.LENGTH_SHORT).show ();
                                }
                            });
                            return;
                        }
                        handler.post (new Runnable () {

                            public void run () {
                                int index = -1;

                                for (int i = 0 ; i < spinner.getCount () ; i++) {
                                    if (spinner.getItemAtPosition (i).toString ().equalsIgnoreCase (formatKey)) {
                                        index = i;
                                        break;
                                    }
                                }
                                if (index == -1) {
                                    Toast.makeText (context, "No compatible pattern", Toast.LENGTH_SHORT).show ();
                                }else{
                                    spinner.setSelection (index);
                                }
                                progressBar.setVisibility (View.INVISIBLE);

                            }

                        });
                    }
                }.start();

            }
        });
        this.findViewById (R.id.backtomainmenu).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                ConversionForm.this.finish ();
            }

        });
    }

}
