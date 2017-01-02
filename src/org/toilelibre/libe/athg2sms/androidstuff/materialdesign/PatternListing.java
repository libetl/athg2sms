package org.toilelibre.libe.athg2sms.androidstuff.materialdesign;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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

        try {
            ((FloatingActionButton)result.findViewById (R.id.addone)).setImageResource(R.drawable.ic_add_black_24dp);
        } catch (Resources.NotFoundException drawableNotSupportedException) {
            ((FloatingActionButton)result.findViewById (R.id.addone)).setImageResource(android.R.drawable.ic_menu_edit);
        }


        new PatternListingUI().onCreate(result, this.getActivity(), new Actions().getAllFormats());

        return result;
    }

    @Override
    public void onResume () {
        super.onResume ();
        new PatternListingUI().onResume(result, this.getActivity(), new Actions().getAllFormats());
    }

}
