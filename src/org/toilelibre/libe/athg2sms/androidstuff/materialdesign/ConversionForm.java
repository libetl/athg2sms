package org.toilelibre.libe.athg2sms.androidstuff.materialdesign;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.actions.Actions;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.ConversionFormUI;

public class ConversionForm extends Fragment {

    private View view;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View result = inflater.inflate(R.layout.conversionform, container, false);
        ((Spinner) result.findViewById (R.id.conversionSet)).setAdapter (new ArrayAdapter<> (this.getActivity(), android.R.layout.simple_spinner_item,
                new Actions ().getAllFormats()));

        new ConversionFormUI().onCreate(result, this.getActivity(), this);

        this.view = result;
        return result;
    }


    public void onActivityResult (final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (data != null && data.getData () != null && data.getData ().getPath () != null) {
            ((EditText) this.view.findViewById (R.id.filename)).setText (Build.VERSION.SDK_INT <19 ? data.getData ().getPath () : data.getDataString ());
        }
    }
}
