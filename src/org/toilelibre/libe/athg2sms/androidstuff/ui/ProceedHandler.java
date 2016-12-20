package org.toilelibre.libe.athg2sms.androidstuff.ui;

import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;

class ProceedHandler extends Handler implements Serializable {

    private ProgressBar progressBar;
    private TextView current;
    private TextView inserted;

	ProceedHandler(ProgressBar progressBar, TextView current, TextView inserted) {
		super();
		this.progressBar = progressBar;
		this.current = current;
		this.inserted = inserted;
	}
	ProgressBar getProgressBar() {
		return progressBar;
	}
	void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}
	TextView getCurrent() {
		return current;
	}
	void setCurrent(TextView current) {
		this.current = current;
	}
	TextView getInserted() {
		return inserted;
	}
	void setInserted(TextView inserted) {
		this.inserted = inserted;
	}
    
    
}
