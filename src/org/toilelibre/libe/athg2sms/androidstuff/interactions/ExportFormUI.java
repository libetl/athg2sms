package org.toilelibre.libe.athg2sms.androidstuff.interactions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.actions.Actions;

import java.util.Arrays;

public class ExportFormUI {

    public void onCreate (final View target, final Activity activity) {

        target.findViewById (R.id.exportfile).setOnClickListener (new OnClickListener () {

            @SuppressLint("InlinedApi")
            public void onClick (final View v) {

                if (ProcessRealTimeFeedback.getInstance() != null &&
                        ProcessRealTimeFeedback.getInstance().getType() == ProcessRealTimeFeedback.Type.EXPORT) {
                    stop(activity);
                }else {
                    start(activity, target);
                }
            }
        });

        ((Spinner) target.findViewById (R.id.exportPatterns)).setAdapter (new ArrayAdapter<String> (activity, android.R.layout.simple_spinner_item,
                new Actions().getAllFormats()));
    }

    private void start(Activity activity, View target) {
        final String patternName = ((Spinner) target.findViewById (R.id.exportPatterns)).getSelectedItem ().toString ();
        activity.getIntent().putExtra("pattern", patternName);

        becomeStopButton(activity);
        new ExportUI().retryExportOperation(activity);
    }

    private void stop(Activity activity) {
        new ExportUI().stopConvertOperation (activity);
    }

    public void becomeStopButton(Activity activity) {
        if (activity.findViewById (R.id.exportfile) == null) return;
        FromColorToColor.animate(activity, activity.findViewById (R.id.exportfile), R.id.exportfile, R.color.colorAccent, R.color.redAccent);
        try {
            ((FloatingActionButton)activity.findViewById (R.id.exportfile)).setImageResource(R.drawable.ic_stop);
        } catch (Resources.NotFoundException drawableNotSupportedException) {
            ((FloatingActionButton)activity.findViewById (R.id.exportfile)).setImageResource(android.R.drawable.alert_light_frame);
        }
    }

    void resetExportButton(Activity activity) {
        if (activity.findViewById (R.id.exportfile) == null) return;
        FromColorToColor.animate(activity, activity.findViewById (R.id.exportfile), R.id.exportfile, R.color.redAccent, R.color.colorAccent);
        try {
            ((FloatingActionButton)activity.findViewById (R.id.exportfile)).setImageResource(R.drawable.ic_cloud_upload_black_24dp);
        } catch (Resources.NotFoundException drawableNotSupportedException) {
            ((FloatingActionButton)activity.findViewById (R.id.exportfile)).setImageResource(android.R.drawable.ic_menu_save);
        }
    }

    public void onRequestPermissionsResult(Activity activity, String[] permissions, Integer... grantResults) {
        if (Arrays.asList(permissions).contains("android.permission.WRITE_EXTERNAL_STORAGE") &&
                Arrays.asList(grantResults).contains(PackageManager.PERMISSION_DENIED)) {
            Snackbar.make(activity.findViewById(android.R.id.content),
                    activity.getString(R.string.exportpermissionsarenecessary),
                    Snackbar.LENGTH_LONG).show();
            resetExportButton(activity);
            return;
        }

        if (Arrays.asList(permissions).contains("android.permission.WRITE_EXTERNAL_STORAGE")) {
            activity.getIntent().putExtra("pattern", ((Spinner) activity.findViewById (R.id.exportfile)).getSelectedItem ().toString ());
            new ExportUI().retryExportOperation(activity);
        } else {
            resetExportButton(activity);
        }
    }
}
