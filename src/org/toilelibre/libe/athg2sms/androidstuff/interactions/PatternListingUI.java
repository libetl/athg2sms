package org.toilelibre.libe.athg2sms.androidstuff.interactions;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.materialdesign.PatternEdition;


public class PatternListingUI {

    public void onCreate(final View target, final Activity activity, final String[] allFormats) {
        onResume(target, activity, allFormats);
        target.findViewById (android.R.id.list).setClickable (true);
        target.findViewById (R.id.addone).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                final Intent intent = new Intent (activity, PatternEdition.class);
                activity.startActivity (intent);
            }

        });

    }

    public void onResume(View target, final Activity activity, final String[] allFormats) {
        ((ListView) target.findViewById (android.R.id.list)).setAdapter (new ArrayAdapter<String> (activity, android.R.layout.simple_list_item_1, allFormats));
        ((ListView) target.findViewById(android.R.id.list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                final Intent intent = new Intent (activity, PatternEdition.class);
                intent.putExtra ("cs", allFormats [position]);
                activity.startActivity (intent);
            }
        });
    }
}
