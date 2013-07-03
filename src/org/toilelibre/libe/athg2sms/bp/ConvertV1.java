package org.toilelibre.libe.athg2sms.bp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Handler;

public class ConvertV1 extends Thread implements ConvertThread {

    private File            f;
    private ConvertListener activity;
    private Exception       exception;
    private Handler         handler;

    public ConvertV1 () {
        super ();
        this.exception = null;
    }

    public Exception getException () {
        return this.exception;
    }

    public int getInserted () {
        return -1;
    }

    @Override
    public void run () {
        try {

            FileReader fr = new FileReader (this.f);
            BufferedReader br = new BufferedReader (fr);
            int i = 0;
            int nb = 0;
            while (br.readLine () != null) {
                nb++ ;
            }

            final int nb2 = nb;
            this.handler.post (new Runnable () {

                public void run () {
                    ConvertV1.this.activity.setMax (nb2);
                }

            });
            br = new BufferedReader (fr);
            SimpleDateFormat df = new SimpleDateFormat ("yyyy.MM.dd hh:mm");
            fr = new FileReader (this.f);
            br = new BufferedReader (fr);
            String line = br.readLine ();
            while (line != null) {
                final int i2 = i;
                this.handler.post (new Runnable () {

                    public void run () {
                        ConvertV1.this.activity.updateProgress (i2, nb2);
                    }

                });
                String folder = "content://sms/";

                if (line.startsWith ("sms;deliver")) {
                    folder += "inbox";
                    line = line.replace ("sms;deliver;\"", "");
                } else if (line.startsWith ("sms;submit")) {
                    folder += "sent";
                    line = line.replace ("sms;submit;\"\";\"", "");
                    line = line.replace ("sms;submit;\"", "");
                }
                String [] message = line.split ("\";\"");

                if (message.length > 0
                        && (message [0].startsWith ("0") || message [0]
                                .startsWith ("+"))) {
                    ContentValues values = new ContentValues ();
                    values.put ("address", message [0]);
                    values.put ("date", df.parse (message [3]).getTime ());
                    values.put ("body", message [5].substring (0, message [5]
                            .length () - 1));
                    this.activity.getContentResolver ().insert (
                            Uri.parse (folder), values);
                }

                line = br.readLine ();
                i++ ;

            }
        } catch (Exception e) {
            this.exception = e;
        }
        this.handler.post (new Runnable () {

            public void run () {
                ConvertV1.this.activity.end ();
            }

        });
    }

    public void setConvertListener (ConvertListener activity) {
        this.activity = activity;
    }

    public void setFile (File f) {
        this.f = f;
    }

    public void setHandler (Handler handler) {
        this.handler = handler;
    }
}
