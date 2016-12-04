package org.toilelibre.libe.athg2sms.androidstuff.api.storage;

import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SharedPreferencesHolder<T> {

    public static class EditorHolder<U> {
        private final U editor;

        EditorHolder(U editor) {
            this.editor = editor;
        }

        public EditorHolder clear() {
            if (editor instanceof SharedPreferences.Editor) {
                ((SharedPreferences.Editor) editor).clear();
            }
            return this;
        }

        public EditorHolder putString(String key, String value) {
            if (editor instanceof SharedPreferences.Editor) {
                ((SharedPreferences.Editor) editor).putString(key, value);
            }
            return this;
        }

        public EditorHolder putStringSet(String key, Set<String> value) {
            if (editor instanceof SharedPreferences.Editor) {
                ((SharedPreferences.Editor) editor).putString(key, value.toString());
            }
            return this;
        }

        public void commit() {
            if (editor instanceof SharedPreferences.Editor) {
                ((SharedPreferences.Editor) editor).commit();
            }
        }
    }

    private final T sharedPreferences;

    public SharedPreferencesHolder(T sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public Map<String, ?> getAll() {
        if (sharedPreferences instanceof SharedPreferences) {
            return ((SharedPreferences) sharedPreferences).getAll();
        }
        return Collections.emptyMap();
    }

    public String getString(String key, String def) {
        if (sharedPreferences instanceof SharedPreferences) {
            return ((SharedPreferences) sharedPreferences).getString(key, def);
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    public <U> EditorHolder<U> edit() {
        if (sharedPreferences instanceof SharedPreferences) {
            return new EditorHolder<>((U)((SharedPreferences) sharedPreferences).edit());
        }
        return (EditorHolder<U>) new EditorHolder<>(new Object());
    }

    public Set<String> getStringSet(String key, Set<String> strings) {
        if (sharedPreferences instanceof SharedPreferences) {
            String setAsString = ((SharedPreferences) sharedPreferences).getString(key, null);
            if (setAsString == null) {
                return strings;
            }

            return new HashSet<>(Arrays.asList(setAsString.replace('[', ' ').replace(']', ' ').trim().split(",")));
        }
        return Collections.emptySet();
    }

}
