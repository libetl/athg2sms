package org.toilelibre.libe.athg2sms.actions;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.ProceedHandler;
import org.toilelibre.libe.athg2sms.business.convert.ConvertListener;

import java.net.URI;
import java.util.Map;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class ProcessRealTimeFeedback implements ConvertListener {

    private ProceedHandler proceedHandler;
    private static ProcessRealTimeFeedback instance = null;

    public ProcessRealTimeFeedback(ProceedHandler proceedHandler) {
        this.proceedHandler = proceedHandler;
    }

    public int delete (final URI uriDelete, final String where, final String [] strings) {
        return 0;
    }

    public <T> void displayInserted (final ContextHolder<T> contextHolder, final int inserted, final int dupl) {
        this.proceedHandler.getInserted().setText (contextHolder.getString(R.string.insertedSms, inserted, dupl));
    }

    public void end () {
        instance = null;
        this.proceedHandler.getProgressBar().setVisibility (INVISIBLE);
    }

    public void insert (final URI uri, final Map<String, Object> smsValues) {
    }

    public <T> void sayIPrepareTheList (final ContextHolder<T> contextHolder, final int size) {
        this.proceedHandler.getProgressBar().setVisibility (VISIBLE);
    	this.proceedHandler.getProgressBar().setIndeterminate (true);
    	this.proceedHandler.getCurrent().setText (contextHolder.getString(R.string.preparingWrite, size));

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

    public static void unbind() {
        if (instance != null) {
            instance = null;
        }
    }

    public ProceedHandler getHandler() {
        return proceedHandler;
    }

    public void updateHandler(ProceedHandler handler) {
        handler.getProgressBar().setMax(this.proceedHandler.getProgressBar().getMax());
        this.proceedHandler = handler;
    }

    public static ProcessRealTimeFeedback getInstance() {
        return instance;
    }
}
