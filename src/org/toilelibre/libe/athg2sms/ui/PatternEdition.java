package org.toilelibre.libe.athg2sms.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.business.pattern.Format;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;

public class PatternEdition extends Activity {

    private String pattern;
    private Format format;

    private void notAllowedChar () {
        final AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setMessage ("No '#' in Pattern name please.").setIcon (android.R.drawable.ic_dialog_alert).setCancelable (false).setPositiveButton ("Ok", new DialogInterface.OnClickListener () {
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
            this.format = FormatSettings.getInstance().getFormats().get(this.pattern);
            this.findViewById (R.id.editcsname).setEnabled (false);
            ((TextView) this.findViewById (R.id.cspattern)).setText (this.format.getRegex().getCommonRegex());
            ((TextView) this.findViewById (R.id.csinbox)).setText (this.format.getRegex().getInboxKeyword());
            ((TextView) this.findViewById (R.id.cssent)).setText (this.format.getRegex().getSentKeyword());
        } else {
            this.format = null;
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
                        FormatSettings.getInstance().getFormats().remove (PatternEdition.this.pattern);
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
                    thiz.notAllowedChar ();
                    return;
                }
                thiz.pattern = newPattern;
                thiz.format = new Format(
                        thiz.pattern,
                        ((TextView) thiz.findViewById (R.id.cspattern)).getText ().toString (),
                        ((TextView) thiz.findViewById (R.id.cspattern)).getText ().toString (),
                        ((TextView) thiz.findViewById (R.id.csinbox)).getText ().toString (),
                        ((TextView) thiz.findViewById (R.id.cssent)).getText ().toString ());

                FormatSettings.getInstance().addOrChangeFormats(format);
                thiz.finish ();
            }

        });

    }
}
