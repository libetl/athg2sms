package org.toilelibre.libe.athg2sms.androidstuff.interactions;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.toilelibre.libe.athg2sms.R;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.HandlerHolder;
import org.toilelibre.libe.athg2sms.androidstuff.sms.SmsFinder;
import org.toilelibre.libe.athg2sms.business.convert.ConvertListener;
import org.toilelibre.libe.athg2sms.business.sms.Sms;
import org.toilelibre.libe.athg2sms.business.sms.SmsList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MySmsUI {

    public void onCreateView(final LayoutInflater inflater, final ViewGroup container, final Context context, final View view) {
        final Lock lock = new ReentrantLock();
        final Condition stopMonitor = lock.newCondition();
        final ProceedHandler handler = new ProceedHandler((ProgressBar)view.findViewById(R.id.progress),
                (TextView)view.findViewById(R.id.current), (TextView)view.findViewById(R.id.inserted));
        final Handler cardHandler = new Handler();
        new Thread() {
            @SuppressWarnings("unchecked")
            public void run() {
                if (!SmsList.getInstance().isBuilt()) {
                    final List<Sms> list = new SmsFinder().pickThemAll(new ContextHolder<Object>(context), new HandlerHolder<Object>(handler),
                            (ConvertListener)new ProcessRealTimeFeedback(ProcessRealTimeFeedback.Type.NOTHING, handler), stopMonitor);

                    SmsList.getInstance().buildFrom(list);
                }
                final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.sms_list);

                for (Map.Entry<CharSequence, List<Sms>> smsIterationEntry : SmsList.getInstance().getSmsList().entrySet()) {
                    final CharSequence key = smsIterationEntry.getKey();
                    final CardView cardView = new CardView(context);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                    layoutParams.setMargins(16, 16, 16, 0);
                    cardView.setLayoutParams(layoutParams);
                    cardView.addView(inflater.inflate(R.layout.peerview, container, false));
                    ((TextView)cardView.findViewById(R.id.name_of_peer)).setText(key);
                    cardHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            linearLayout.addView(cardView);
                        }
                    });
                }
                cardHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((ViewGroup)view).removeView(view.findViewById(R.id.loading));
                    }});
            }
        }.start();
    }
}
