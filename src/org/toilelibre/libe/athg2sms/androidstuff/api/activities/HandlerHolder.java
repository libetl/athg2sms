package org.toilelibre.libe.athg2sms.androidstuff.api.activities;

import android.os.Handler;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by lionel on 01/12/16.
 */

public class HandlerHolder<T> {

    private final T handler;

    public HandlerHolder(T handler) {
        this.handler = handler;
    }

    public void postForHandler(Runnable runnable) {
        if (this.handler instanceof Handler){
            ((Handler) this.handler).post(runnable);
            return;
        }
        try {
            this.handler.getClass().getMethod("post", Runnable.class).invoke(this.handler, runnable);
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (NoSuchMethodException e) {
        }
    }
}
