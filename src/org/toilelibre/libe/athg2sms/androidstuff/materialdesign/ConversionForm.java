package org.toilelibre.libe.athg2sms.androidstuff.materialdesign;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.toilelibre.libe.athg2sms.EntryPoint;
import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.actions.Actions;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.ConversionFormUI;
import org.toilelibre.libe.athg2sms.androidstuff.sms.SmsApplicationToggle;

public class ConversionForm extends Fragment {

    private View view;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View result = inflater.inflate(R.layout.conversionform, container, false);


        try {
            ((FloatingActionButton)result.findViewById (R.id.start)).setImageResource(R.drawable.ic_move_to_inbox_black_24dp);
        } catch (Resources.NotFoundException drawableNotSupportedException) {
            ((FloatingActionButton)result.findViewById (R.id.start)).setImageResource(android.R.drawable.ic_dialog_email);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            replaceButtonBySwitch(this.getActivity(), (ViewGroup)result);
        }

        ((Spinner) result.findViewById (R.id.conversionSet)).setAdapter (new ArrayAdapter<String> (this.getActivity(), android.R.layout.simple_spinner_item,
                new Actions ().getAllFormats()));

        new ConversionFormUI().onCreate(result, this.getActivity(), this);

        this.view = result;
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        applyCheckedStatus(view.findViewById(R.id.toggledefaultapp));
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void replaceButtonBySwitch(Activity activity, ViewGroup result) {
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView titleView = new TextView(activity);
        titleView.setLayoutParams(lparams);
        titleView.setText(R.string.toggledefaultapp);
        Switch theSwitch = new Switch(activity);
        theSwitch.setId(R.id.toggledefaultapp);
        theSwitch.setLayoutParams(lparams);
        applyCheckedStatus(theSwitch);
        layout.addView(theSwitch);
        layout.addView(titleView);

        ((ViewGroup) result.findViewById(R.id.conversionFormButtons)).removeView(result.findViewById(R.id.toggledefaultapp));
        ((ViewGroup) result.findViewById(R.id.conversionFormButtons)).addView(layout);
    }

    private void applyCheckedStatus(View aSwitch) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            String defaultSmsApp =
                    new SmsApplicationToggle().getDefaultSmsPackage(this.getActivity());
            ((Switch)aSwitch).setChecked(EntryPoint.class.getPackage().getName().equals(defaultSmsApp));
        }
    }

    @Override
    public void onActivityResult (final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (data != null && data.getData () != null && data.getData ().getPath () != null) {
            ((EditText) this.view.findViewById (R.id.filename)).setText (Build.VERSION.SDK_INT <19 ? data.getData ().getPath () : data.getDataString ());
            new ConversionFormUI().triggerGuessFormat(this.getActivity(), this.view);
        }
    }
}
