package org.toilelibre.libe.athg2sms;

import java.util.Set;

import org.toilelibre.libe.athg2sms.settings.Settings;
import org.toilelibre.libe.athg2sms.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ConvSetsListActivity extends ListActivity {

    private String [] setsArray;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.cslist);
        this.reloadList ();
        ((ListView) this.findViewById (android.R.id.list)).setClickable (true);
        this.findViewById (R.id.backtomainmenu).setOnClickListener (
                new OnClickListener () {

                    public void onClick (View v) {
                        ConvSetsListActivity.this.finish ();
                    }

                });
        this.findViewById (R.id.addone).setOnClickListener (
                new OnClickListener () {

                    public void onClick (View v) {
                        Intent intent = new Intent (ConvSetsListActivity.this,
                                ConvSetEditActivity.class);
                        ConvSetsListActivity.this.startActivity (intent);
                    }

                });
    }

    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {
        super.onListItemClick (l, v, position, id);
        Intent intent = new Intent (this, ConvSetEditActivity.class);
        intent.putExtra ("cs", this.setsArray [position]);
        this.startActivity (intent);

    }

    @Override
    protected void onResume () {
        super.onResume ();
        this.reloadList ();
    }

    private void reloadList () {
        Set<String> setsSet = Settings.getSetsKeySet ();
        this.setsArray = new String [setsSet.size ()];
        setsSet.toArray (this.setsArray);
        ((ListView) this.findViewById (android.R.id.list))
                .setAdapter (new ArrayAdapter<String> (this,
                        android.R.layout.simple_list_item_1, this.setsArray));
    }

}
