package org.toilelibre.libe.athg2sms.androidstuff.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;

import java.util.Set;

public class PatternListing extends ListActivity {

    private String [] setsArray;

    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.cslist);
        this.reloadList ();
        this.findViewById (android.R.id.list).setClickable (true);
        this.findViewById (R.id.backtomainmenu).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                PatternListing.this.finish ();
            }

        });
        this.findViewById (R.id.addone).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                final Intent intent = new Intent (PatternListing.this, PatternEdition.class);
                PatternListing.this.startActivity (intent);
            }

        });
    }

    @Override
    protected void onListItemClick (final ListView l, final View v, final int position, final long id) {
        super.onListItemClick (l, v, position, id);
        final Intent intent = new Intent (this, PatternEdition.class);
        intent.putExtra ("cs", this.setsArray [position]);
        this.startActivity (intent);
    }

    @Override
    protected void onResume () {
        super.onResume ();
        this.reloadList ();
    }

    private void reloadList () {
        final Set<String> setsSet = FormatSettings.getInstance().getFormats().keySet();
        this.setsArray = new String [setsSet.size ()];
        setsSet.toArray (this.setsArray);
        ((ListView) this.findViewById (android.R.id.list)).setAdapter (new ArrayAdapter<> (this, android.R.layout.simple_list_item_1, this.setsArray));
    }

}
