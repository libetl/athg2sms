package org.toilelibre.libe.athg2sms.androidstuff.ui;

import android.annotation.SuppressLint;

import org.toilelibre.libe.athg2sms.business.convert.ConvertListener;
import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.net.URI;

class ProcessRealTimeFeedback implements ConvertListener {

    private ProceedHandler proceedHandler;
    private static ProcessRealTimeFeedback instance = null;

    ProcessRealTimeFeedback(ProceedHandler proceedHandler) {
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


    public void updateProgress (final String text, final int i, final int nb) {
        this.proceedHandler.getCurrent().setText (text + i + " / " + nb);
        this.proceedHandler.getProgressBar().setProgress (i);
    }

    public ProcessRealTimeFeedback bind () {
        if (instance == null) {
            instance = this;
            return this;
        }
        return instance;
    }

    static void unbind () {
        if (instance != null) {
            instance = null;
        }
    }

    ProceedHandler getHandler() {
        return proceedHandler;
    }

    void updateHandler (ProceedHandler handler) {
        handler.getProgressBar().setMax(this.proceedHandler.getProgressBar().getMax());
        this.proceedHandler = handler;
    }

    static ProcessRealTimeFeedback getInstance() {
        return instance;
    }
}
