package org.toilelibre.libe.athg2sms.androidstuff.interactions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.actions.Actions;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.androidstuff.api.storage.FileRetriever;
import org.toilelibre.libe.athg2sms.androidstuff.sms.SmsApplicationToggle;

import java.io.FileNotFoundException;

public class ConversionFormUI {
    private Handler handler = new Handler ();

    public void onCreate (final View target, final Activity activity, final Fragment fragment) {
        target.findViewById (R.id.selectfile).setOnClickListener (new OnClickListener () {

            @SuppressLint ("InlinedApi")
            public void onClick (final View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT <19){
                    intent = new Intent (Intent.ACTION_GET_CONTENT);
                    intent.setType ("*/*");
                    intent.putExtra ("org.openintents.extra.BUTTON_TEXT", activity.getString (R.string.selectfile));
                    fragment.startActivityForResult (intent, 1);
                } else {
                    intent = new Intent();
                    intent.setType("text/*");
                    intent.addCategory (Intent.CATEGORY_OPENABLE);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    fragment.startActivityForResult (intent, 0);
                }
            }
        });
        target.findViewById (R.id.start).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                if (((Spinner) target.findViewById (R.id.conversionSet)).getSelectedItem () == null) {
                    return;
                }
                String filename = ((EditText) target.findViewById (R.id.filename)).getText ().toString ();
                try {
                    FileRetriever.getFile (new ContextHolder<Object>(activity), filename);
                } catch (final FileNotFoundException e) {
                    handler.post (new Runnable () {
                        public void run () {
                            Snackbar.make(activity.findViewById(android.R.id.content),
                                    "No file selected", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                final Intent proceedIntent = activity.getIntent();
                proceedIntent.putExtra("filename", ((EditText) target.findViewById (R.id.filename)).getText ().toString ());
                proceedIntent.putExtra("pattern", ((Spinner) target.findViewById (R.id.conversionSet)).getSelectedItem ().toString ());
                new ConvertUI().retryConvertOperation (activity);
            }
        });
        target.findViewById (R.id.guessconvset).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                final Spinner spinner = (Spinner) target.findViewById (R.id.conversionSet);
                final String file = ((EditText) target.findViewById (R.id.filename)).getText ().toString ();
                final ProgressBar progressBar =  ((ProgressBar)target.findViewById (R.id.guessing));
                progressBar.setVisibility(View.VISIBLE);
                new Thread() {
                    @Override
                    public void run() {
                        final String formatKey;
                        final String content;
                        try {
                            content = FileRetriever.getFile (new ContextHolder<Object>(activity), file);
                            formatKey = new Actions().guessNow(content);
                        } catch (final FileNotFoundException e) {
                            handler.post (new Runnable () {
                                public void run () {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Snackbar.make(activity.findViewById(android.R.id.content),
                                            "No file selected", Snackbar.LENGTH_SHORT).show();
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
                                    Snackbar.make(activity.findViewById(android.R.id.content),
                                            "No compatible pattern", Snackbar.LENGTH_SHORT).show();
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

        target.findViewById (R.id.toggledefaultapp).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                new SmsApplicationToggle().toggleDefault(activity);
            }
        });
    }

}
