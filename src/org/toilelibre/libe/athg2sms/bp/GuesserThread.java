package org.toilelibre.libe.athg2sms.bp;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;

import org.toilelibre.libe.athg2sms.FileRetriever;
import org.toilelibre.libe.athg2sms.settings.Settings;
import org.toilelibre.libe.athg2sms.util.MatchesScanner;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

public class GuesserThread extends Thread {
    
    private String      file;
    private Handler     handler;
    private Spinner     spinner;
    private ProgressBar progressBar;
    private Activity    context;
    
    @Override
    public void run () {
        try {
            this.guessNow ();
        } finally {
            this.end ();
        }
    }
    
    private void guessNow () {
    	String content;
		try {
			content = FileRetriever.getFile (context, file);
		} catch (final FileNotFoundException e) {
			((android.os.Handler) this.handler).post (new Runnable () {
				public void run () {
                    progressBar.setVisibility(View.INVISIBLE);
		            Toast.makeText (context, "No file selected", Toast.LENGTH_SHORT).show ();
				}
			});
            return;
		}
        Matcher matcher = null;
        String key = null;
        for (String pattern : Settings.getSets ().keySet ()) {
            key = pattern;
            matcher = new MatchesScanner (Settings.preparePattern (pattern), content).matcher ();
            if (matcher != null) {
                break;
            }
            
        }
        
        final String foundKey = matcher == null ? null : key;
        ((android.os.Handler) this.handler).post (new Runnable () {
            
            public void run () {
                int index = -1;
                
                for (int i = 0 ; i < spinner.getCount () ; i++) {
                    if (spinner.getItemAtPosition (i).toString ().equalsIgnoreCase (foundKey)) {
                        index = i;
                        break;
                    }
                }
                if (index == -1) {
                    Toast.makeText (context, "No compatible pattern", Toast.LENGTH_SHORT).show ();
                }else{
                    spinner.setSelection (index);
                }
                progressBar.setVisibility (View.INVISIBLE);
                
            }
            
        });
    }
    
    private void end () {
        
    }
    
    public void setFileToBeRead (final String f) {
        this.file = f;
    }
    
    public void setHandler (Handler handler1) {
        this.handler = handler1;
    }
    
    public void setSpinner (Spinner spinner1) {
        this.spinner = spinner1;
    }
    
    public void setProgressBar (ProgressBar progressBar1) {
        this.progressBar = progressBar1;
    }

    public void setContext (Activity context) {
        this.context = context;
    }
    
}
