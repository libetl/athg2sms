package org.toilelibre.libe.athg2sms.androidstuff.material;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.actions.Actions;
import org.toilelibre.libe.athg2sms.androidstuff.ui.PatternEdition;


public class PatternListing extends Fragment {

    private String [] setsArray;
    private View result;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.cslist, container, false);
        this.result = result;
        this.reloadList ();
        result.findViewById (android.R.id.list).setClickable (true);
        result.findViewById (R.id.backtomainmenu).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
            }

        });
        ((ListView)result.findViewById(android.R.id.list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                final Intent intent = new Intent (PatternListing.this.getActivity(), PatternEdition.class);
                intent.putExtra ("cs", PatternListing.this.setsArray [position]);
                PatternListing.this.startActivity (intent);
            }
        });
        result.findViewById (R.id.addone).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                final Intent intent = new Intent (PatternListing.this.getActivity(), PatternEdition.class);
                PatternListing.this.startActivity (intent);
            }

        });
        return result;
    }

    @Override
    public void onResume () {
        super.onResume ();
        this.reloadList ();
    }

    private void reloadList () {
        this.setsArray = new Actions().getAllFormats();
        ((ListView) result.findViewById (android.R.id.list)).setAdapter (new ArrayAdapter<> (this.getActivity(), android.R.layout.simple_list_item_1, this.setsArray));
    }

}
