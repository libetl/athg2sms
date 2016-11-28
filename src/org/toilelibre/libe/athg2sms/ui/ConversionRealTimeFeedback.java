package org.toilelibre.libe.athg2sms.ui;

import android.annotation.SuppressLint;

import org.toilelibre.libe.athg2sms.business.convert.ConvertListener;
import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.net.URI;

class ConversionRealTimeFeedback implements ConvertListener {

    private ProceedHandler proceedHandler;
    private static ConversionRealTimeFeedback instance = null;

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
        instance = null;
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
    }


    public void updateProgress (final int i, final int nb) {
        this.proceedHandler.getCurrent().setText ("Writing sms # : " + i + " / " + nb);
        this.proceedHandler.getProgressBar().setProgress (i);
    }

    public ConversionRealTimeFeedback bind () {
        if (instance == null) {
            instance = this;
            return this;
        }
        return instance;
    }

    ProceedHandler getHandler() {
        return proceedHandler;
    }

    void updateHandler (ProceedHandler handler) {
        handler.getProgressBar().setMax(this.proceedHandler.getProgressBar().getMax());
        this.proceedHandler = handler;
    }

    static ConversionRealTimeFeedback getInstance() {
        return instance;
    }
}
