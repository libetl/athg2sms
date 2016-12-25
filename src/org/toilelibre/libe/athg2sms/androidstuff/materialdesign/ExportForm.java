package org.toilelibre.libe.athg2sms.androidstuff.materialdesign;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.ExportFormUI;

public class ExportForm extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View result = inflater.inflate(R.layout.exportform, container, false);

        new ExportFormUI().onCreate(result, this.getActivity());

        return result;
    }
}
