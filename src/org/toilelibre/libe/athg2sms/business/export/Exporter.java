package org.toilelibre.libe.athg2sms.business.export;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.HandlerHolder;
import org.toilelibre.libe.athg2sms.androidstuff.interactions.ProcessRealTimeFeedback;
import org.toilelibre.libe.athg2sms.androidstuff.sms.SmsFinder;
import org.toilelibre.libe.athg2sms.business.convert.ConvertListener;
import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;

import static org.toilelibre.libe.athg2sms.business.concurrent.ConditionWatcher.weAreAskedToStopNowBecauseOfThe;

public class Exporter {

    public String export(final ContextHolder<?> context, final HandlerHolder<?> handler, final String patternName, final ConvertListener convertListener, final Condition stopMonitor) {
        handler.postForHandler(new Runnable() {
            @Override
            @SuppressWarnings("unchecked")
            public void run() {
            convertListener.sayIPrepareTheList(context, 0);
            }
        });
        final StringBuilder result = new StringBuilder();
        final MessageMapper messageMapper = new MessageMapper();
        final List<Map<String, Object>> list = new SmsFinder().pickThemAll(context, handler, (ProcessRealTimeFeedback) convertListener, stopMonitor);

        if (list == null) return null;

        handler.postForHandler(new Runnable() {
            @Override
            public void run() {
                convertListener.setMax(list.size());
            }
        });

        for (int i = 0; i < list.size() ; i++) {

            if (weAreAskedToStopNowBecauseOfThe(stopMonitor)) return null;

            final int thisIndex = i;
            handler.postForHandler(new Runnable() {
                @Override
                public void run() {
                   convertListener.updateProgress(context.getString(R.string.savingthesms), thisIndex, list.size());
                }
            });
            Sms sms = new Sms(list.get(i));
            result.append(messageMapper.convert(sms, patternName));
        }
        handler.postForHandler(new Runnable() {
            @Override
            public void run() {
                convertListener.end();
            }
        });
        return result.toString();
    }
}
