package org.toilelibre.libe.athg2sms.bp;

import java.util.regex.Matcher;

import org.toilelibre.libe.athg2sms.settings.Settings;
import org.toilelibre.libe.athg2sms.util.MatchesScanner;

import android.os.Handler;
import android.widget.Spinner;

public class GuesserThread extends Thread {
    
    private String           content;
    private Handler          handler;
    private Spinner          spinner;
    
    @Override
    public void run () {
        try {
            this.guessNow ();
        } finally {
            this.end ();
        }
    }
    
    private void guessNow () {
        Matcher matcher = null;
        String key = null;
        for (String pattern : Settings.getSets ().keySet ()) {
            key = pattern;
            matcher = new MatchesScanner (Settings.preparePattern (pattern), this.content).matcher ();
            if (matcher != null) {
                break;
            }
            
        }
        
        final String foundKey = matcher == null ? null : key;
        if (this.handler instanceof android.os.Handler) {
            ((android.os.Handler) this.handler).post (new Runnable () {
                
                public void run () {
                    int index = 0;
                    
                    for (int i = 0 ; i < spinner.getCount () ; i++) {
                        if (spinner.getItemAtPosition (i).toString ().equalsIgnoreCase (foundKey)) {
                            index = i;
                            break;
                        }
                    }
                    spinner.setSelection (index);
                }
                
            });
        }
    }
    
    private void end () {
        
    }
    
    public void setContentToBeParsed (final String f) {
        this.content = f;
    }
    
    public void setHandler (Handler handler1) {
        this.handler = handler1;
    }
    
    public void setSpinner (Spinner spinner1) {
        this.spinner = spinner1;
    }
    
}
