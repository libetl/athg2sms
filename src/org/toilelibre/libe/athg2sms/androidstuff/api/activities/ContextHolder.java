package org.toilelibre.libe.athg2sms.androidstuff.api.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.test.mock.MockContentResolver;

public class ContextHolder<T> {

    private final T context;

    public ContextHolder(T context) {
        this.context = context;
    }

    public T get() {
        return this.context;
    }

    @SuppressWarnings("unchecked")
    public <U> U get(Class<U> contentResolver) {
        if (contentResolver == ContentResolver.class && this.context instanceof Context) {
            return (U)((Context) this.context).getContentResolver();
        }
        if (contentResolver == ContentResolver.class) {
            return (U)new MockContentResolver();
        }
        return null;
    }
}
