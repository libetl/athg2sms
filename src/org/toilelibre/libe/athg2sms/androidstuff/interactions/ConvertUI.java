package org.toilelibre.libe.athg2sms.androidstuff.interactions;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.toilelibre.libe.athg2sms.EntryPoint;
import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.actions.ProcessRealTimeFeedback;
import org.toilelibre.libe.athg2sms.androidstuff.service.ConvertService;
import org.toilelibre.libe.athg2sms.androidstuff.sms.SmsApplicationToggle;

public class ConvertUI {

    public void retryConvertOperation(final Activity activity) {
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
        final ProcessRealTimeFeedback convertListener = new ProcessRealTimeFeedback(handler);

        final Intent intent = new Intent (activity, ConvertService.class);

        intent.putExtra("pattern", activity.getIntent().getStringExtra("pattern"));
        intent.putExtra("filename", filename);

        LocalBroadcastManager.getInstance(activity).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.finish();
            }
        }, new IntentFilter("stopConvert"));

        if (convertListener.bind() == convertListener) {
            displayIfDryRun(activity);
            activity.startService (intent);
        } else {
            ProcessRealTimeFeedback.getInstance().updateHandler(handler);
        }
    }

    private void displayIfDryRun(final Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT &&
                !EntryPoint.class.getPackage().getName().equals(
                        new SmsApplicationToggle().getDefaultSmsPackage(activity))) {

            Snackbar.make(activity.findViewById(android.R.id.content),
                    "BTW... this app is not toggled to insert SMS messages right now." +
                            "This will be a dry run.", Snackbar.LENGTH_LONG).show();

        }
    }
}
