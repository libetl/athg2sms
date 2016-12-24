package org.toilelibre.libe.athg2sms.androidstuff.materialdesign;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.actions.Actions;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.PatternListingUI;


public class PatternListing extends Fragment {

    private View result;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.cslist, container, false);
        this.result = result;

        new PatternListingUI().onCreate(result, this.getActivity(), new Actions().getAllFormats());

        return result;
    }

    @Override
    public void onResume () {
        super.onResume ();
        new PatternListingUI().onResume(result, this.getActivity(), new Actions().getAllFormats());
    }

}
