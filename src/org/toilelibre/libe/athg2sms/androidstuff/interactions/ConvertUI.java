package org.toilelibre.libe.athg2sms.androidstuff.interactions;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.toilelibre.libe.athg2sms.EntryPoint;
import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.service.ConvertService;
import org.toilelibre.libe.athg2sms.androidstuff.sms.SmsApplicationToggle;

import java.util.ArrayList;

class ConvertUI {

    private static final ArrayList<BroadcastReceiver> toBeUnregistered = new ArrayList<BroadcastReceiver>();

    void retryConvertOperation(final Activity activity) {
        String filename = activity.getIntent().getStringExtra("filename");
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 ||
                (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)){
            this.startConvertProcess(activity, filename);
            return;
        }

        ActivityCompat.requestPermissions(activity, new String []{"android.permission.READ_EXTERNAL_STORAGE",
                Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS}, 0);

    }

    private void startConvertProcess(final Activity activity, final String filename) {

        final ProceedHandler handler = new ProceedHandler ((ProgressBar) activity.findViewById (R.id.progress),
                (TextView) activity.findViewById (R.id.current), (TextView) activity.findViewById (R.id.inserted));
        final ProcessRealTimeFeedback convertListener = new ProcessRealTimeFeedback(ProcessRealTimeFeedback.Type.IMPORT, handler);

        final Intent intent = new Intent (activity, ConvertService.class);

        intent.putExtra("pattern", activity.getIntent().getStringExtra("pattern"));
        intent.putExtra("filename", filename);

        final BroadcastReceiver stopConvert = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new ConversionFormUI().resetStartButton(activity);
                buildAlertDialog(activity, R.string.done_title, R.layout.done, R.string.great);
                unregisterAllReceivers(activity);
            }
        };
        final BroadcastReceiver errorDuringConvert = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new ConversionFormUI().resetStartButton(activity);
                Dialog dialog = buildAlertDialog(activity, R.string.error_title, R.layout.error, R.string.again);
                final String errorMessage1 = intent.getExtras().getString("errorMessage");
                final String errorMessage2 = intent.getExtras().getString("secondErrorMessage");
                ((TextView) dialog.findViewById (R.id.exception)).setText (errorMessage1);
                if (errorMessage2 != null) {
                    ((TextView) dialog.findViewById (R.id.exception2)).setText (errorMessage2);
                } else {
                    ((TextView) dialog.findViewById (R.id.exception2)).setText (context.getString(R.string.thatsallweknow));
                }
                unregisterAllReceivers(activity);
            }
        };
        final BroadcastReceiver abortConvert = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new ConversionFormUI().resetStartButton(activity);
                Snackbar.make(activity.findViewById(android.R.id.content),
                        activity.getString(R.string.aborted),
                        Snackbar.LENGTH_LONG).show();
                unregisterAllReceivers(activity);
            }
        };
        toBeUnregistered.add(stopConvert);
        toBeUnregistered.add(errorDuringConvert);
        toBeUnregistered.add(abortConvert);
        LocalBroadcastManager.getInstance(activity).registerReceiver(stopConvert, new IntentFilter("stopConvert"));
        LocalBroadcastManager.getInstance(activity).registerReceiver(errorDuringConvert, new IntentFilter("errorDuringConvert"));
        LocalBroadcastManager.getInstance(activity).registerReceiver(abortConvert, new IntentFilter("abortConvert"));

        if (convertListener.bind() == convertListener) {
            displayIfDryRun(activity);
            activity.startService (intent);
        } else {
            activity.findViewById (R.id.progress).setVisibility(View.VISIBLE);
            ProcessRealTimeFeedback.getInstance().updateHandler(handler);
        }
    }

    void stopConvertOperation(Activity activity) {
        final Intent intent = new Intent (activity, ConvertService.class);
        intent.putExtra("stopNow", "stopNow");
        activity.startService(intent);
    }

    private synchronized void unregisterAllReceivers(Activity activity) {
        for (BroadcastReceiver receiver : toBeUnregistered) {
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiver);
        }
        toBeUnregistered.clear();
    }

    private Dialog buildAlertDialog(Context context, int titleResId, int layout, int buttonText) {
        Dialog dialog = new AlertDialog.Builder(context).setCancelable(false)
                .setTitle(titleResId)
                .setView(LayoutInflater.from(context).inflate(layout, null))
                .setPositiveButton(context.getResources().getText(buttonText),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
        return dialog;
    }

    private void displayIfDryRun(final Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT &&
                !EntryPoint.class.getPackage().getName().equals(
                        new SmsApplicationToggle().getDefaultSmsPackage(activity))) {

            Snackbar.make(activity.findViewById(android.R.id.content),
                    activity.getText(R.string.dryRun), Snackbar.LENGTH_LONG).show();

        }
    }
}
