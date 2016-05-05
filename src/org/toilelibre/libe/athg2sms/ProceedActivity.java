package org.toilelibre.libe.athg2sms;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;

import org.toilelibre.libe.athg2sms.bp.ConvertListener;
import org.toilelibre.libe.athg2sms.bp.ConvertThread;
import org.toilelibre.libe.athg2sms.bp.DefaultConvertListener;
import org.toilelibre.libe.athg2sms.settings.SettingsFactory;
import org.toilelibre.libe.athg2sms.status.Done;
import org.toilelibre.libe.athg2sms.status.Error;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProceedActivity extends Activity {

    private static String filename;

    public static String getFilename () {
        return ProceedActivity.filename;
    }

    public static void setFilename (final String filename) {
        ProceedActivity.filename = filename;
    }

    private Handler       handler;



    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.proceed);
        ((TextView) this.findViewById (R.id.filename)).setText ("Filename : " + ProceedActivity.filename);
        this.handler = new Handler ();

        final File f = this.getFileFromFileName ();
        ConvertThread convert = SettingsFactory.asV4 ().getConvertThreadInstance ();
        ConvertListener convertListener = new DefaultConvertListener (this, Done.class, Error.class, convert,
                (ProgressBar) this.findViewById (R.id.progress), (TextView) this.findViewById (R.id.current), (TextView) this.findViewById (R.id.inserted));
        if (f == null) {
            convert.setException (new URISyntaxException (ProceedActivity.filename, "Failed to find this file, sorry. Send that to libe4@free.fr"));
            convertListener.end ();
            return;
        }
        try {
            final Scanner scan = new Scanner (f);
            scan.useDelimiter ("\\A");
            final String content = scan.next ();
            scan.close ();
            convert.setContentToBeParsed (content);
            convert.setConvertListener (convertListener);
            convert.setHandler (this.handler);
            convert.start ();
        } catch (final FileNotFoundException e) {
            convert.setException (e);
            convertListener.end ();
        }
    }

    private File getFileFromFileName () {
        String realPath = null;
        if (ProceedActivity.filename == null) {
            return null;
        } else if (ProceedActivity.filename.indexOf (':') == -1) {
            return new File (ProceedActivity.filename);
        }
        final String id = ProceedActivity.filename.split (":") [1];

        final String [] column = { MediaColumns.DATA };

        final String where = BaseColumns._ID + "=?";
        final Cursor cursor = this.getContentResolver ().query (MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, where, new String [] { id }, null);
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst ()) {
            final int columnIndex = cursor.getColumnIndex (column [0]);
            realPath = cursor.getString (columnIndex);
        }
        cursor.close ();
        return new File (realPath);
    }
}
