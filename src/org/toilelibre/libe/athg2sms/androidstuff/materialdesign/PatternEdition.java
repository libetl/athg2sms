package org.toilelibre.libe.athg2sms.androidstuff.materialdesign;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.actions.Actions;

public class PatternEdition extends Activity {

    private String pattern;

    private void warning (String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setMessage (message).setIcon (android.R.drawable.ic_dialog_alert).setCancelable (false).setPositiveButton ("Ok", new DialogInterface.OnClickListener () {
            public void onClick (final DialogInterface dialog, final int id) {
                dialog.dismiss ();
            }
        }).show ();
        builder.create ();
    }

    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.csedit);
        final Bundle bundle = this.getIntent ().getExtras ();
        if (bundle != null && bundle.getCharSequence ("cs") != null) {
            this.pattern = (String) bundle.getCharSequence ("cs");
            ((TextView) this.findViewById (R.id.editcsname)).setText (this.pattern);
            this.findViewById (R.id.editcsname).setEnabled (false);

            ((TextView) this.findViewById (R.id.cspattern)).setText (new Actions().getFormatRegex(this.pattern).getCommonRegex());
            ((TextView) this.findViewById (R.id.export_format)).setText (new Actions().getFormatRegex(this.pattern).getExportFormat());
            ((TextView) this.findViewById (R.id.csinbox)).setText (new Actions().getFormatRegex(this.pattern).getInboxKeyword());
            ((TextView) this.findViewById (R.id.cssent)).setText (new Actions().getFormatRegex(this.pattern).getSentKeyword());
        } else {
            this.pattern = "?";
            this.findViewById (R.id.delete).setEnabled (false);
        }
        this.findViewById (R.id.cancel).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                PatternEdition.this.finish ();
            }

        });
        this.findViewById (R.id.delete).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                new AlertDialog.Builder (PatternEdition.this).setIcon (android.R.drawable.ic_dialog_alert).setTitle (R.string.delete).setMessage (R.string.really_delete).setPositiveButton (R.string.delete_yes, new DialogInterface.OnClickListener () {

                    public void onClick (final DialogInterface dialog, final int which) {
                        new Actions().removeFormat(PatternEdition.this.pattern);
                        PatternEdition.this.finish ();
                    }

                }).setNegativeButton (R.string.cancel, null).show ();

            }

        });
        this.findViewById (R.id.modify).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                final PatternEdition thiz = PatternEdition.this;
                final String newPattern = ((TextView) thiz.findViewById (R.id.editcsname)).getText ().toString ();
                if (newPattern.indexOf ('#') != -1) {
                    thiz.warning ("No '#' in Format name please.");
                    return;
                }
                try {
                    thiz.pattern = newPattern;
                    new Actions().addOrChangeFormat(thiz.pattern,
                            ((TextView) thiz.findViewById(R.id.cspattern)).getText().toString(),
                            ((TextView) thiz.findViewById(R.id.export_format)).getText().toString().isEmpty() ?
                                    ((TextView) thiz.findViewById(R.id.cspattern)).getText().toString() :
                                    ((TextView) thiz.findViewById(R.id.export_format)).getText().toString(),
                            ((TextView) thiz.findViewById(R.id.csinbox)).getText().toString(),
                            ((TextView) thiz.findViewById(R.id.cssent)).getText().toString());
                    thiz.finish ();
                } catch (IllegalArgumentException ife) {
                    thiz.warning (ife.getMessage());
                }
            }

        });

    }
}
