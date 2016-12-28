package org.toilelibre.libe.athg2sms.androidstuff.interactions;

import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;

public class ProceedHandler extends Handler implements Serializable {

    private ProgressBar progressBar;
    private TextView current;
    private TextView inserted;

	public ProceedHandler(ProgressBar progressBar, TextView current, TextView inserted) {
		super();
		this.progressBar = progressBar;
		this.current = current;
		this.inserted = inserted;
	}
	public ProgressBar getProgressBar() {
		return progressBar;
	}
	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}
	public TextView getCurrent() {
		return current;
	}
	public void setCurrent(TextView current) {
		this.current = current;
	}
	public TextView getInserted() {
		return inserted;
	}
	public void setInserted(TextView inserted) {
		this.inserted = inserted;
	}
    
    
}
