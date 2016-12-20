package org.toilelibre.libe.athg2sms.androidstuff.ui;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.androidstuff.api.storage.FileRetriever;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.HandlerHolder;
import org.toilelibre.libe.athg2sms.androidstuff.sms.SmsDeleter;
import org.toilelibre.libe.athg2sms.androidstuff.sms.SmsInserter;
import org.toilelibre.libe.athg2sms.business.convert.ConvertException;
import org.toilelibre.libe.athg2sms.business.convert.ConvertListener;
import org.toilelibre.libe.athg2sms.business.convert.Converter;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;

import java.io.FileNotFoundException;
import java.text.ParseException;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class ConvertService extends IntentService {

    public ConvertService() {
        super(ConvertService.class.getSimpleName());
    }

    private void error(Exception e) {
        final Intent intent = new Intent(getBaseContext(), Error.class);
        intent.putExtra("errorMessage", e.toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY | FLAG_ACTIVITY_CLEAR_TOP);
        if (e.getCause() != null) {
            intent.putExtra("secondErrorMessage", e.getCause().toString());
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent ("stopConvert"));
        this.startActivity(intent);
    }

    private void done() {
        final Intent intent = new Intent(getBaseContext(), Done.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY | FLAG_ACTIVITY_CLEAR_TOP);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent ("stopConvert"));
        this.startActivity(intent);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        final Converter thisConverter = new Converter();
        ProcessRealTimeFeedback convertListener = ProcessRealTimeFeedback.getInstance();
        final String content = this.getContentFromFileName (convertListener, intent.getStringExtra("filename"));
        if (content == null) {
            return;
        }
        boolean atLeastOneConverted;
        try {
            atLeastOneConverted = thisConverter.convertNow(FormatSettings.getInstance().getFormats().get(
                    intent.getStringExtra("pattern")), content,
                    convertListener, new HandlerHolder<>(convertListener.getHandler()),
                    new ContextHolder<>(this), new SmsInserter(), new SmsDeleter());
        } catch (ConvertException ce) {
            error(ce);
            return;
        } finally {
            ProcessRealTimeFeedback.unbind();
        }
        if (!atLeastOneConverted)
            error(new ParseException("No SMS Imported !\nThe selected conversion set does not match the input", 0));
        else
            done();
    }

    private String getContentFromFileName (ConvertListener convertListener, String filename) {
        try {
            return FileRetriever.getFile (this, filename);
        } catch (FileNotFoundException e) {
            ProcessRealTimeFeedback.unbind();
            return null;
        }
    }
}

