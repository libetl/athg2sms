package org.toilelibre.libe.athg2sms.androidstuff.materialdesign;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mifmif.common.regex.Generex;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.api.id.ViewIdGenerator;

import java.util.ArrayList;
import java.util.List;

public class PatternMaker extends Fragment {
    private List<View> allViews = new ArrayList<View>();
    private String [] allPossibleCompletions = new String [] { "__choose below",
      "0+ char until", "1+ char until", "a number", "a version", "a comma", "a semicolon", "a simple quote", "a double quote", "the address", "the folder", "the body", "the date", "end of match"
    };
    private String [] regexCompletions = new String [] { "",
            ".{0,20}", ".{1,20}", "[0-9]{1,20}", "\\s*(?:[0-9]{1,2}(?:\\.[0-9]{1,2})?)?\\s*", ",", ";", "\\'", "\\\"", "ADDRESS", "INBOX", "Lorem Ipsum", "2000-01-01", "\\s+"
    };
    private ViewGroup rootView;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup result = (ViewGroup)inflater.inflate(R.layout.patternmaker, container, false);
        this.rootView = result;
        addSpinnerIfOneMissing();
        return result;
    }

    private void addSpinnerIfOneMissing() {
        View lastChild = ((ViewGroup)this.rootView.findViewById(R.id.builder)).getChildAt(
                ((ViewGroup)this.rootView.findViewById(R.id.builder)).getChildCount() - 1);
        if (lastChild instanceof Spinner && ((Spinner) lastChild).getSelectedItemPosition() == 0) return;
        Spinner newSpinner = spawnSpinner (this.getContext());
        allViews.add(newSpinner);
        ((ViewGroup)this.rootView.findViewById(R.id.builder)).addView(newSpinner);
    }

    private void addTextViewAfter(View view) {
        EditText newEditText = new EditText(this.getContext());
        newEditText.setMaxLines(1);
        newEditText.setId(ViewIdGenerator.generateViewId());
        newEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 15);
        newEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                updateExamples();
                return true;
            }
        });
        int addThere = ((ViewGroup)this.rootView.findViewById(R.id.builder)).indexOfChild(view) + 1;
        if (addThere == 0 || addThere == allViews.size()) {
            allViews.add(newEditText);
            ((ViewGroup) this.rootView.findViewById(R.id.builder)).addView(newEditText, addThere);
        }else if (allViews.size() > addThere && !(allViews.get(addThere) instanceof EditText)){
            allViews.add(addThere, newEditText);
            ((ViewGroup) this.rootView.findViewById(R.id.builder)).addView(newEditText, addThere);
        }
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
                    addTextViewAfter(adapterView);
                }

                if (i != 0 && i != allPossibleCompletions.length - 1) {
                    addSpinnerIfOneMissing();
                }
                updateExamples();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return spinner;
    }

    private void updateExamples() {
        StringBuilder regex = new StringBuilder();
        for (int i = 0 ; i < allViews.size() ; i++) {
            if (allViews.get(i) instanceof Spinner) {
                if (((Spinner) allViews.get(i)).getSelectedItemPosition() == 1 ||
                        ((Spinner) allViews.get(i)).getSelectedItemPosition() == 2) {
                    if (((EditText)allViews.get(i+1)).getText().length() != 0) {
                        regex.append(regexCompletions [((Spinner) allViews.get(i)).getSelectedItemPosition()]);
                        i++;
                        regex.append(((EditText) allViews.get(i)).getText());
                    }
                }else {
                    regex.append(regexCompletions [((Spinner) allViews.get(i)).getSelectedItemPosition()]);
                }
            }else if (allViews.get(i) instanceof EditText) {
                regex.append(((EditText)allViews.get(i)).getText());
            }
        }
        ((ViewGroup)this.rootView.findViewById(R.id.example)).removeAllViews();
        TextView textView = new TextView(this.getContext());
        String example = new Generex(regex.toString()).random(30, 160);
        textView.setText(example);
        ((ViewGroup)this.rootView.findViewById(R.id.example)).addView(textView);
    }

}
