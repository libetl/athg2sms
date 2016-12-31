package org.toilelibre.libe.athg2sms.androidstuff.interactions;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.service.ExportService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

class ExportUI {
    private static final ArrayList<BroadcastReceiver> toBeUnregistered = new ArrayList<BroadcastReceiver>();

    void retryExportOperation(final Activity activity) {
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 ||
                (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED)){
            this.startExportService(activity);
            return;
        }

        ActivityCompat.requestPermissions(activity, new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_SMS}, 0);

    }

    private void startExportService(final Activity activity) {

        final ProceedHandler handler = new ProceedHandler ((ProgressBar) activity.findViewById (R.id.exportprogress),
                (TextView) activity.findViewById (R.id.exportcurrent), (TextView) activity.findViewById (R.id.exportinserted));
        final ProcessRealTimeFeedback convertListener = new ProcessRealTimeFeedback(ProcessRealTimeFeedback.Type.EXPORT, handler);
        final Intent intent = new Intent (activity, ExportService.class);

        intent.putExtra("pattern", activity.getIntent().getStringExtra("pattern"));

        sendOrSaveFile(activity);

        if (convertListener.bind() == convertListener) {
            activity.startService (intent);
        } else {
            ProcessRealTimeFeedback.getInstance().updateHandler(handler);
        }
    }

    private void sendOrSaveFile(final Activity activity) {
        if (Build.VERSION.SDK_INT < 16) {
            copyTempFile(activity);
            return;
        }

        sendTextData(activity);
    }

    private void sendTextData(final Activity activity) {
        BroadcastReceiver newReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra("result") != null) {
                    final Intent sendToIntent = new Intent(Intent.ACTION_SEND)
                            .putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(intent.getStringExtra("result"))))
                            .setType("text/plain");
                    activity.startActivity(Intent.createChooser(sendToIntent, "Save as..."));
                }
                ProcessRealTimeFeedback.unbind();
                new ExportFormUI().resetExportButton(activity);
                unregisterAllReceivers(activity);
            }
        };
        LocalBroadcastManager.getInstance(activity).registerReceiver(newReceiver, new IntentFilter("stopExport"));
        toBeUnregistered.add(newReceiver);
    }

    private void copyTempFile(final Activity activity) {
        BroadcastReceiver newReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra("result") == null) {
                    ProcessRealTimeFeedback.unbind();
                    new ExportFormUI().resetExportButton(activity);
                    unregisterAllReceivers(activity);
                }
                File parentDir;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
                    parentDir = activity.getExternalFilesDir(null);
                }else {
                    parentDir = new File("/sdcard/");
                }
                if (parentDir == null) {
                    return;
                }
                parentDir.mkdir();
                String destFilename = parentDir.getAbsolutePath() + File.separator + "export" +
                        System.currentTimeMillis() + ".txt";
                try {
                    FileInputStream inStream = new FileInputStream(new File(intent.getStringExtra("result")));
                    FileOutputStream outStream = new FileOutputStream(new File(destFilename));
                    FileChannel inChannel = inStream.getChannel();
                    FileChannel outChannel = outStream.getChannel();
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                    inStream.close();
                    outStream.close();

                    Snackbar.make(activity.findViewById(android.R.id.content),
                            activity.getResources().getString(R.string.export_under_android_4, destFilename), Snackbar.LENGTH_LONG).show();

                } catch (IOException e) {
                    Snackbar.make(activity.findViewById(android.R.id.content),
                            activity.getResources().getString(R.string.problem_while_exporting, e.getMessage()), Snackbar.LENGTH_LONG).show();
                } finally {
                    ProcessRealTimeFeedback.unbind();
                    new ExportFormUI().resetExportButton(activity);
                    unregisterAllReceivers(activity);
                }
            }
        };
        LocalBroadcastManager.getInstance(activity).registerReceiver(newReceiver, new IntentFilter("stopExport"));
        toBeUnregistered.add(newReceiver);
    }

    void stopConvertOperation(Activity activity) {
        final Intent intent = new Intent (activity, ExportService.class);
        intent.putExtra("stopNow", "stopNow");
        activity.startService (intent);
    }

    private synchronized void unregisterAllReceivers(Activity activity) {
        for (BroadcastReceiver receiver : toBeUnregistered) {
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiver);
        }
        toBeUnregistered.clear();
    }
}
