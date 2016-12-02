package org.toilelibre.libe.athg2sms.androidstuff.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;

public class ExportForm extends Activity {

    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.exportform);

        this.findViewById (R.id.backtomainmenu).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                ExportForm.this.finish ();
            }

        });

        this.findViewById (R.id.exportfile).setOnClickListener (new OnClickListener () {

            @SuppressLint("InlinedApi")
            public void onClick (final View v) {
                final String patternName = ((Spinner) ExportForm.this.findViewById (R.id.exportPatterns)).getSelectedItem ().toString ();
                final Intent goToExportActivity = new Intent(ExportForm.this, Export.class).putExtra("pattern", patternName);
                ExportForm.this.startActivity(goToExportActivity);
            }
        });

        ((Spinner) this.findViewById (R.id.exportPatterns)).setAdapter (new ArrayAdapter<> (this, android.R.layout.simple_spinner_item,
                FormatSettings.getInstance().getFormats().keySet().toArray(new String [FormatSettings.getInstance().getFormats().size ()])));

    }
}
