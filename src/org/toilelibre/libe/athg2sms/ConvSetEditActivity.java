package org.toilelibre.libe.athg2sms;

import java.util.HashMap;
import java.util.Map;

import org.toilelibre.libe.athg2sms.settings.Settings;
import org.toilelibre.libe.athg2sms.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ConvSetEditActivity extends Activity {

    private String              cs;
    private Map<String, String> map;

    private void notAllowedChar () {
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setMessage ("No '#' in Conv Set name please.").setIcon (
                android.R.drawable.ic_dialog_alert).setCancelable (false)
                .setPositiveButton ("Ok",
                        new DialogInterface.OnClickListener () {
                            public void onClick (DialogInterface dialog, int id) {
                                dialog.dismiss ();
                            }
                        }).show ();
        builder.create ();

    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.csedit);
        Bundle bundle = this.getIntent ().getExtras ();
        if (bundle != null && bundle.getCharSequence ("cs") != null) {
            this.cs = bundle.getCharSequence ("cs").toString ();
            ((TextView) this.findViewById (R.id.editcsname)).setText (this.cs);
            this.map = Settings.getSet (this.cs);
            ((EditText) this.findViewById (R.id.editcsname)).setEnabled (false);
            ((TextView) this.findViewById (R.id.csinbox)).setText (this.map
                    .get ("inbox").replace ("\n", "[\\n]").replace ("\t", "[\\t]"));
            ((TextView) this.findViewById (R.id.cssent)).setText (this.map
                    .get ("sent").replace ("\n", "[\\n]").replace ("\t", "[\\t]"));
        } else {
            this.map = new HashMap<String, String> ();
            this.cs = "?";
            ((Button) this.findViewById (R.id.delete)).setEnabled (false);
        }
        this.findViewById (R.id.cancel).setOnClickListener (
                new OnClickListener () {

                    public void onClick (View v) {
                        ConvSetEditActivity.this.finish ();
                    }

                });
        this.findViewById (R.id.delete).setOnClickListener (
                new OnClickListener () {

                    public void onClick (View v) {
                        new AlertDialog.Builder (ConvSetEditActivity.this)
                                .setIcon (android.R.drawable.ic_dialog_alert)
                                .setTitle (R.string.delete).setMessage (
                                        R.string.really_delete)
                                .setPositiveButton (R.string.delete_yes,
                                        new DialogInterface.OnClickListener () {

                                            public void onClick (
                                                    DialogInterface dialog,
                                                    int which) {
                                                Settings
                                                        .getSets ()
                                                        .remove (
                                                                ConvSetEditActivity.this.cs);
                                                ConvSetEditActivity.this
                                                        .finish ();
                                            }

                                        }).setNegativeButton (R.string.cancel,
                                        null).show ();

                    }

                });
        this.findViewById (R.id.modify).setOnClickListener (
                new OnClickListener () {

                    public void onClick (View v) {
                        ConvSetEditActivity thiz = ConvSetEditActivity.this;
                        String newCs = ((TextView) thiz
                                .findViewById (R.id.editcsname)).getText ()
                                .toString ();
                        if (newCs.indexOf ('#') != -1) {
                            thiz.notAllowedChar ();
                        } else {
                            thiz.cs = newCs;
                            thiz.map.put ("inbox", ((TextView) thiz
                                    .findViewById (R.id.csinbox)).getText ()
                                    .toString ().replace ("[\\n]", "\n")
                                    .replace ("[\\t]", "\t"));
                            thiz.map.put ("sent", ((TextView) thiz
                                    .findViewById (R.id.cssent)).getText ()
                                    .toString ().replace ("[\\n]", "\n")
                                    .replace ("[\\t]", "\t"));
                            Settings.putSet (thiz.cs, thiz.map);
                            thiz.finish ();
                        }
                    }

                });

    }
}
