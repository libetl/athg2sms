package org.toilelibre.libe.athg2sms.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.view.View.OnClickListener;

import org.toilelibre.libe.athg2sms.R;

public class ExportForm extends Activity {

    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.export);

        this.findViewById (R.id.backtomainmenu).setOnClickListener (new OnClickListener () {

            public void onClick (final View v) {
                ExportForm.this.finish ();
            }

        });

        this.findViewById (R.id.exportfile).setOnClickListener (new OnClickListener () {

            @SuppressLint("InlinedApi")
            public void onClick (final View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT <19){
                    intent = new Intent (Intent.ACTION_CREATE_DOCUMENT);
                    intent.setType ("*/*");
                    intent.putExtra ("org.openintents.extra.BUTTON_TEXT", ExportForm.this.getString (R.string.selectfile));
                    ExportForm.this.startActivityForResult (intent, 1);
                } else {
                    intent = new Intent().setAction(Intent.ACTION_CREATE_DOCUMENT)
                            .setType("text/*")
                            .addCategory(Intent.CATEGORY_OPENABLE)
                            .setType(DocumentsContract.Document.COLUMN_MIME_TYPE);
                    ExportForm.this.startActivityForResult (intent, 0);
                }
            }
        });

    }

}
