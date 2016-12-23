package org.toilelibre.libe.athg2sms.androidstuff.material;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.actions.Actions;
import org.toilelibre.libe.athg2sms.androidstuff.ui.Export;

public class ExportForm extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View result = inflater.inflate(R.layout.exportform, container, false);
        container.findViewById (R.id.backtomainmenu).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
            }

        });

        result.findViewById (R.id.exportfile).setOnClickListener (new OnClickListener () {

            @SuppressLint("InlinedApi")
            public void onClick (final View v) {
                final String patternName = ((Spinner) result.findViewById (R.id.exportPatterns)).getSelectedItem ().toString ();
                final Intent goToExportActivity = new Intent(ExportForm.this.getActivity(), Export.class).putExtra("pattern", patternName);
                ExportForm.this.startActivity(goToExportActivity);
            }
        });

        ((Spinner) result.findViewById (R.id.exportPatterns)).setAdapter (new ArrayAdapter<> (this.getActivity(), android.R.layout.simple_spinner_item,
                new Actions().getAllFormats()));

        ((ViewGroup)result).removeView(result.findViewById(R.id.ImageView01));
        result.findViewById(R.id.backtomainmenu).setVisibility(View.INVISIBLE);

        return result;
    }
}
