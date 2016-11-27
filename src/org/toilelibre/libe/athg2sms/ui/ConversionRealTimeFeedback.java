package org.toilelibre.libe.athg2sms.ui;

import java.net.URI;

import android.annotation.SuppressLint;

import org.toilelibre.libe.athg2sms.business.sms.Sms;
import org.toilelibre.libe.athg2sms.business.convert.ConvertListener;

class ConversionRealTimeFeedback implements ConvertListener {

    private ProceedHandler proceedHandler;
    private int max;

    ConversionRealTimeFeedback(ProceedHandler proceedHandler) {
        this.proceedHandler = proceedHandler;
    }

    public int delete (final URI uriDelete, final String where, final String [] strings) {
        return 0;
    }

    public void displayInserted (final int inserted, final int dupl) {
        this.proceedHandler.getInserted().setText ("Inserted SMS : " + inserted + " (" + dupl + " duplicate(s))");
    }

    public void end () {
    }
    
    @SuppressLint ("NewApi")
    public void insert (final URI uri, final Sms sms) {
    }

    public void sayIPrepareTheList (final int size) {
    	this.proceedHandler.getProgressBar().setIndeterminate (true);
    	this.proceedHandler.getCurrent().setText ("Preparing write for sms # : " + size);

    }

    public void setMax (final int nb) {
    	this.proceedHandler.getProgressBar().setIndeterminate (false);
        this.proceedHandler.getProgressBar().setMax (nb);
        this.max = nb;
    }


    public void updateProgress (final int i, final int nb) {
        this.proceedHandler.getCurrent().setText ("Writing sms # : " + i + " / " + nb);
        this.proceedHandler.getProgressBar().setProgress (i);
    }

	public void setProceedHandler(ProceedHandler handler) {
		this.proceedHandler = handler;
	}

	public int getMax() {
		return max;
	}

}
