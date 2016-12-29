package org.toilelibre.libe.athg2sms.androidstuff.interactions;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
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
import java.util.Arrays;

public class ConversionFormUI {
    private Handler handler = new Handler ();

    public void onCreate (final View target, final Activity activity, final Fragment fragment) {
        if (android.os.Build.VERSION.SDK_INT < 19){
            target.findViewById(R.id.toggledefaultapp).setVisibility(View.INVISIBLE);
        }
        target.findViewById (R.id.selectfile).setOnClickListener (new OnClickListener () {

            @SuppressLint ("InlinedApi")
            public void onClick (final View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT < 19){
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

                fromColorToColor(activity, target, R.id.start, R.color.colorAccent, R.color.redAccent);

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
                                    activity.getText(R.string.nofileselected), Snackbar.LENGTH_SHORT).show();
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
                triggerGuessFormat(activity, target);
            }
        });

        target.findViewById (R.id.toggledefaultapp).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                new SmsApplicationToggle().toggleDefault(activity);
            }
        });
    }

    private void fromColorToColor(final Context context, final View rootView, final int button, final int start, final int end) {
        final int nbSteps = 100;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), 0, nbSteps);
            colorAnimation.setDuration(3000);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        final int value = (Integer)animator.getAnimatedValue();

                        final int colorAccent = ContextCompat.getColor(context, start);
                        final int redAccent = ContextCompat.getColor(context, end);
                        final int decreasing = nbSteps - value;
                        final int newColor = Color.argb(255, Color.red(colorAccent) * decreasing / nbSteps + Color.red(redAccent) * value / nbSteps,
                                Color.green(colorAccent) * decreasing / nbSteps + Color.green(redAccent) * value / nbSteps,
                                Color.blue(colorAccent) * decreasing / nbSteps + Color.blue(redAccent) * value / nbSteps);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            rootView.findViewById (button).setBackgroundTintList(ColorStateList.valueOf(newColor));
                        }else {
                            rootView.findViewById (button).setBackgroundColor(newColor);
                        }
                    }
                }

            });
            colorAnimation.start();
        }
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (Arrays.asList(permissions).contains("android.permission.READ_EXTERNAL_STORAGE")) {
            activity.getIntent().putExtra("filename", ((EditText) activity.findViewById (R.id.filename)).getText ().toString ());
            activity.getIntent().putExtra("pattern", ((Spinner) activity.findViewById (R.id.conversionSet)).getSelectedItem ().toString ());
            new ConvertUI().retryConvertOperation (activity);
        }
    }

    public void triggerGuessFormat(final Activity activity, View rootView) {
        final Spinner spinner = (Spinner) rootView.findViewById (R.id.conversionSet);
        final String file = ((EditText) rootView.findViewById (R.id.filename)).getText ().toString ();
        final ProgressBar progressBar =  ((ProgressBar)rootView.findViewById (R.id.guessing));
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
                                    activity.getText(R.string.nofileselected), Snackbar.LENGTH_SHORT).show();
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
                                    activity.getText(R.string.nocompatiblepattern), Snackbar.LENGTH_SHORT).show();
                        }else{
                            spinner.setSelection (index);
                        }
                        progressBar.setVisibility (View.INVISIBLE);

                    }

                });
            }
        }.start();
    }

}
