package org.toilelibre.libe.athg2sms.androidstuff.materialdesign;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.MySmsUI;

public class MySms extends Fragment {

    private View view;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View result = inflater.inflate(R.layout.mysms, container, false);
        this.view = result;

        new MySmsUI().onCreateView(inflater, container, this.getContext(), result);

        return result;
    }

    @Override
    public void onResume() {
        super.onResume ();
    }
}
