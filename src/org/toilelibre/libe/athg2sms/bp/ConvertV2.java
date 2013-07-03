package org.toilelibre.libe.athg2sms.bp;

import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.toilelibre.libe.athg2sms.settings.Settings;
import org.toilelibre.libe.athg2sms.util.LookForStringReader;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Handler;

public class ConvertV2 extends Thread implements ConvertThread {
    private static String   folder = "content://sms/";

    private File            f;
    private ConvertListener convertListener;
    private Exception       exception;
    private Handler         handler;
    private int             inserted;

    public ConvertV2 () {
        super ();
        this.inserted = 0;
        this.exception = null;
    }

    private ContentValues buildMessageFromString (String key, String line) {
        ContentValues values = new ContentValues ();
        String format = Settings.getFormat (key);
        String pattern = Settings.getPattern (key);
        String valPattern = Settings.getValPattern (key);
        Pattern pVar = Pattern.compile (pattern);
        Matcher mVar = pVar.matcher (format);
        Pattern pVal = Pattern.compile (valPattern);
        Matcher mVal = pVal.matcher (line);
        DateFormat df = null;
        mVar.find ();
        mVal.find ();
        int groupCount = mVar.groupCount ();
        for (int i = 1; i <= groupCount; i++ ) {
            String var = mVar.group (i);
            String val = mVal.group (i);
            if ( !var.startsWith ("date")) {
                values.put (var, val);
            } else {
                df = new SimpleDateFormat (var.substring ("date".length ()),
                        Locale.US);
                try {
                    if (values.get ("date") == null){
                        values.put ("date", df.parse (val).getTime ());
                    }else {
                        long l = values.getAsLong ("date");
                        l += df.parse (val).getTime ();
                        values.put ("date", l);
                    }
                } catch (ParseException e) {
                    e.printStackTrace ();
                }
            }
        }
        return values;
    }

    private String determineSubFolder (String line) {
        boolean found = false;
        Iterator<String> it = Settings.getValPatternsIterator ();
        String patternKey = null;

        while (it.hasNext () && !found) {
            patternKey = it.next ();
            String patternString = Settings.getValPattern (patternKey);
            found |= Pattern.matches (patternString, line);
        }
        return found ? patternKey : null;
    }

    public Exception getException () {
        return this.exception;
    }

    public int getInserted () {
        return this.inserted;
    }

    @Override
    public void run () {
        try {
            Settings.makePatterns ();
            FileReader fr = new FileReader (this.f);
            LookForStringReader lfsr = new LookForStringReader (fr, Settings
                    .getDelimiter ());
            final List<String> texts = new LinkedList<String> ();
            String text = "dummy";
            while (text != null) {
                text = lfsr.readUntilNextSequence ();
                if (text != null) {
                    texts.add (text);
                }
                this.handler.post (new Runnable () {

                    public void run () {
                        ConvertV2.this.convertListener
                                .sayIPrepareTheList (texts.size ());
                    }

                });
            }
            final int nb = texts.size ();

            this.handler.post (new Runnable () {

                public void run () {
                    ConvertV2.this.convertListener.setMax (nb);
                }
            });

            for (int i = 0; i < texts.size (); i++ ) {
                final int ins = this.inserted;
                text = texts.get (i);
                final int i2 = i;
                this.handler.post (new Runnable () {

                    public void run () {
                        ConvertV2.this.convertListener.updateProgress (i2, nb);
                        ConvertV2.this.convertListener.displayInserted (ins);
                    }

                });

                String suffix = this.determineSubFolder (text);
                if (suffix != null) {
                    this.inserted++ ;
                    String folderMsg = ConvertV2.folder + suffix;
                    try {
                      ContentValues values = this.buildMessageFromString (suffix,
                              text);

                      this.convertListener.getContentResolver ().insert (
                              Uri.parse (folderMsg), values);
                    }catch(IllegalStateException ise){
                        this.exception = ise;
                    }
                }
            }
        } catch (Exception e) {
            this.exception = e;
        }
        this.handler.post (new Runnable () {

            public void run () {
                ConvertV2.this.convertListener.end ();
            }

        });
    }

    public void setConvertListener (ConvertListener cl) {
        this.convertListener = cl;
    }

    public void setFile (File f) {
        this.f = f;
    }

    public void setHandler (Handler handler) {
        this.handler = handler;
    }

}
