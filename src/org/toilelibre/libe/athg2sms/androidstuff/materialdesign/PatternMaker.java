package org.toilelibre.libe.athg2sms.androidstuff.materialdesign;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.api.id.ViewIdGenerator;

import java.util.ArrayList;
import java.util.List;

public class PatternMaker extends Fragment {
    private List<View> allViews = new ArrayList<View>();
    private String [] allPossibleCompletions = new String [] { "__choose below",
      "anything until", "the char(s)", "a comma", "a semicolon", "a simple quote", "a double quote", "the address", "the folder", "the body", "the date", "end of match"
    };
    private ViewGroup rootView;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup result = (ViewGroup)inflater.inflate(R.layout.patternmaker, container, false);
        this.rootView = result;
        addSpinner ();
        return result;
    }

    private void addSpinner() {
        Spinner newSpinner = spawnSpinner (this.getContext());
        allViews.add(newSpinner);
        ((ViewGroup)this.rootView.findViewById(R.id.builder)).addView(newSpinner);
    }

    private void addTextView() {
        EditText newEditText = new EditText(this.getContext());
        newEditText.setId(ViewIdGenerator.generateViewId());
        newEditText.setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        allViews.add(newEditText);
        ((ViewGroup)this.rootView.findViewById(R.id.builder)).addView(newEditText);
    }

    private Spinner spawnSpinner(Context context) {
        Spinner spinner = new Spinner(context);
        spinner.setId(ViewIdGenerator.generateViewId());
        spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,
                allPossibleCompletions));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1 || i == 2) {
                    addTextView();
                }
                if (i != 0 && i != allPossibleCompletions.length - 1) {
                    addSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return spinner;
    }

}
