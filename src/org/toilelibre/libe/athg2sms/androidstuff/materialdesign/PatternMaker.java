package org.toilelibre.libe.athg2sms.androidstuff.materialdesign;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
      "0+ char until", "1+ char until", "a number", "a version", "a comma", "a semicolon", "a simple quote", "a double quote", "the address", "the folder", "the body", "the date", "whitespaces", "remove that"
    };
    private String [] regexCompletions = new String [] { "",
            ".{0,20}", ".{1,20}", "[0-9]{1,20}", "\\s?[0-9](\\.[0-9])?", ",", ";", "\\'", "\\\"", "ADDRESS", "INBOX", "Lorem Ipsum", "2000-01-01", "\\s+"
    };
    private String [] realRegexCompletions = new String [] { "",
            ".*?(?=%s)%s", ".+?(?=%s)%s", "[0-9]+", "\\s?[0-9]+(\\.[0-9]+)?", ",", ";", "\\'", "\\\"", "$(adress)", "$(folder)", "$(body)", "$(dateyyyy-MM-dd)", "\\s+"
    };
    private ViewGroup rootView;

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup result = (ViewGroup)inflater.inflate(R.layout.patternmaker, container, false);
        this.rootView = result;
        addSpinnerIfOneMissing();
        result.findViewById(R.id.build).setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Intent intent = new Intent (PatternMaker.this.getActivity(), PatternEdition.class);
                intent.putExtra ("format", buildRegex(realRegexCompletions));
                PatternMaker.this.getActivity().startActivity (intent);
            }
        });
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

    private void removeTextViewAfter(View view) {
        int removeThere = ((ViewGroup)this.rootView.findViewById(R.id.builder)).indexOfChild(view) + 1;
        if (removeThere == 0 || removeThere >= allViews.size() || !(allViews.get(removeThere) instanceof EditText)) return;

        View viewToRemove = allViews.get(removeThere);
        ((ViewGroup) this.rootView.findViewById(R.id.builder)).removeView(viewToRemove);
        allViews.remove(removeThere);
    }

    private void removeThis(View view) {
        allViews.remove(view);
        ((ViewGroup) this.rootView.findViewById(R.id.builder)).removeView(view);
    }

    private Spinner spawnSpinner(Context context) {
        final Spinner spinner = new Spinner(context);
        spinner.setId(ViewIdGenerator.generateViewId());
        spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,
                allPossibleCompletions));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == allPossibleCompletions.length - 1) {
                    removeThis(adapterView);
                    removeTextViewAfter(adapterView);
                }else if (i == 1 || i == 2) {
                    addTextViewAfter(adapterView);
                    addSpinnerIfOneMissing();
                }else if (i != 0) {
                    addSpinnerIfOneMissing();
                    removeTextViewAfter(adapterView);
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
        String regex = buildRegex (regexCompletions);
        ((ViewGroup)this.rootView.findViewById(R.id.example)).removeAllViews();
        TextView textView = new TextView(this.getContext());
        String example = replaceWeirdChars (new Generex(regex).random(0, 160));
        textView.setText(example);
        ((ViewGroup)this.rootView.findViewById(R.id.example)).addView(textView);
    }

    private String buildRegex(String [] array) {
        StringBuilder regex = new StringBuilder();
        for (int i = 0 ; i < allViews.size() ; i++) {
            if (allViews.get(i) instanceof Spinner) {
                if (((Spinner) allViews.get(i)).getSelectedItemPosition() == 1 ||
                        ((Spinner) allViews.get(i)).getSelectedItemPosition() == 2) {
                    if (((EditText)allViews.get(i+1)).getText().length() != 0) {
                        regex.append(array [((Spinner) allViews.get(i)).getSelectedItemPosition()]);
                        i++;
                        regex.append(((EditText) allViews.get(i)).getText());
                    }
                }else {
                    regex.append(array [((Spinner) allViews.get(i)).getSelectedItemPosition()]);
                }
            }else if (allViews.get(i) instanceof EditText) {
                regex.append(((EditText)allViews.get(i)).getText());
            }
        }
        return regex.toString();
    }

    private String replaceWeirdChars(String text) {
        StringBuilder replacement = new StringBuilder();
        for (int i = 0 ; i < text.length() ; i++) {
            if (text.charAt(i) > 255) {
                int offset = text.charAt(i) % 62;
                if (offset < 26) {
                    replacement.append((char)('A' + offset));
                }else if (offset < 52) {
                    replacement.append((char)('a' + (offset - 26)));
                }else
                    replacement.append((char)('9' + (offset - 52)));
            }else
                replacement.append(text.charAt(i));
        }
        return replacement.toString();
    }

}
