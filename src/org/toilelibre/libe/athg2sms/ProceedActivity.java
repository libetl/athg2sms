package org.toilelibre.libe.athg2sms;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.text.ParseException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.toilelibre.libe.athg2sms.bp.ConvertException;
import org.toilelibre.libe.athg2sms.bp.ConvertListener;
import org.toilelibre.libe.athg2sms.bp.ConvertThread;
import org.toilelibre.libe.athg2sms.settings.SettingsFactory;
import org.toilelibre.libe.athg2sms.status.Done;
import org.toilelibre.libe.athg2sms.status.Error;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.provider.DocumentFile;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProceedActivity extends Activity implements ConvertListener {

    private static String filename;

    public static String getFilename () {
        return ProceedActivity.filename;
    }

    public static void setFilename (final String filename) {
        ProceedActivity.filename = filename;
    }

    private ConvertThread convert;
    private Handler       handler;

    private ProgressBar progress;

    private TextView current;
    private TextView inserted;

    public int delete (final URI uriDelete, final String where, final String [] strings) {
        return this.getContentResolver ().delete (Uri.parse (uriDelete.toString ()), where, strings);
    }

    public void displayInserted (final int inserted, final int dupl) {
        this.inserted.setText ("Inserted SMS : " + inserted + " (" + dupl + " duplicate(s))");
    }

    public void end () {
        Intent resultIntent = new Intent (this, Done.class);
        if (this.convert.getException () != null) {
            Error.setLastError (this.convert.getException ());
            resultIntent = new Intent (this, Error.class);
        } else if (this.convert.getInserted () == 0) {
            Error.setLastError (new ParseException ("No SMS Imported !\nThe selected conversion set does not match the input", 0));
            resultIntent = new Intent (this, Error.class);
        }
        this.startActivity (resultIntent);
        this.finish ();
    }

    public void insert (final URI uri, final Map<String, Object> values) {
        final ContentValues values2 = new ContentValues ();
        for (final Entry<String, Object> entry : values.entrySet ()) {
            this.putEntry (values2, entry);
        }
        this.getContentResolver ().insert (Uri.parse (uri.toString ()), values2);
    }

    @Override
    protected void onCreate (final Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.setContentView (R.layout.proceed);
        this.progress = (ProgressBar) this.findViewById (R.id.progress);
        this.current = (TextView) this.findViewById (R.id.current);
        this.inserted = (TextView) this.findViewById (R.id.inserted);
        ((TextView) this.findViewById (R.id.filename)).setText ("Filename : " + ProceedActivity.filename);
        this.handler = new Handler ();
        
        final File f = getFileFromFileName ();
        if (f == null) {
            throw new RuntimeException ("Failed to find this file, sorry. Send that to libe4@free.fr");
        }
        this.convert = SettingsFactory.asV4 ().getConvertThreadInstance ();
        try {
            final Scanner scan = new Scanner (f);
            scan.useDelimiter ("\\A");
            final String content = scan.next ();
            this.convert.setContentToBeParsed (content);
            this.convert.setConvertListener (this);
            this.convert.setHandler (this.handler);
            this.convert.start ();
        } catch (final FileNotFoundException e) {
            throw new RuntimeException (e);
        }
    }

    private File getFileFromFileName () {
        String realPath = null;
        if (ProceedActivity.filename == null){
            return null;
        }else if (ProceedActivity.filename.indexOf (':') == -1) {
            return new File (ProceedActivity.filename);
        }
        String id = ProceedActivity.filename.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };     

        String where = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, where, new String [] {id}, null);
        if (cursor == null) {
            return null;
        }
        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(column[0]);
            realPath = cursor.getString(columnIndex);
        }
        cursor.close();
        return new File (realPath);
    }
        

    private void putEntry (final ContentValues values, final Entry<String, Object> entry) {
        if (entry.getValue () instanceof Boolean) {

        } else if (entry.getValue () instanceof Boolean) {
            values.put (entry.getKey (), (Boolean) entry.getValue ());

        } else if (entry.getValue () instanceof Byte) {
            values.put (entry.getKey (), (Byte) entry.getValue ());

        } else if (entry.getValue () instanceof byte []) {
            values.put (entry.getKey (), (byte []) entry.getValue ());

        } else if (entry.getValue () instanceof Double) {
            values.put (entry.getKey (), (Double) entry.getValue ());

        } else if (entry.getValue () instanceof Float) {
            values.put (entry.getKey (), (Float) entry.getValue ());

        } else if (entry.getValue () instanceof Integer) {
            values.put (entry.getKey (), (Integer) entry.getValue ());

        } else if (entry.getValue () instanceof Long) {
            values.put (entry.getKey (), (Long) entry.getValue ());

        } else if (entry.getValue () instanceof Short) {
            values.put (entry.getKey (), (Short) entry.getValue ());

        } else if (entry.getValue () instanceof String) {
            values.put (entry.getKey (), (String) entry.getValue ());

        }
    }

    public void sayIPrepareTheList (final int size) {
        this.progress.setIndeterminate (true);
        this.current.setText ("Preparing write for sms # : " + size);

    }

    public void setMax (final int nb) {
        this.progress.setIndeterminate (false);
        this.progress.setMax (nb);
    }

    public void updateProgress (final int i, final int nb) {
        this.current.setText ("Writing sms # : " + i + " / " + nb);
        this.progress.setProgress (i);
    }
}
