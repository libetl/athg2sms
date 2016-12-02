package org.toilelibre.libe.athg2sms.androidstuff;

import android.content.SharedPreferences;

import java.util.Collections;
import java.util.Map;

/**
 * Created by lionel on 01/12/16.
 */
public class SharedPreferencesHolder<T> {

    private T sharedPreferences;

    public SharedPreferencesHolder(T sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public Map<String, ?> getAll() {
        if (sharedPreferences instanceof SharedPreferences) {
            return ((SharedPreferences) sharedPreferences).getAll();
        }
        return Collections.emptyMap();
    }
}
