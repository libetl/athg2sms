package org.toilelibre.libe.athg2sms.androidstuff.api.storage;


import android.database.Cursor;

public class CursorHolder<T> {

    private final T cursor;

    public CursorHolder(T cursor) {
        this.cursor = cursor;
    }

    public void moveToFirst() {
        if (cursor instanceof Cursor) {
            ((Cursor)this.cursor).moveToFirst();
        }
    }

    public int getCount() {
        if (cursor instanceof Cursor) {
            ((Cursor)this.cursor).getCount();
        }
        return 0;
    }

    public String[] getColumnNames() {
        if (cursor instanceof Cursor) {
            ((Cursor)this.cursor).getColumnNames();
        }
        return new String[0];
    }

    public void moveToNext() {
        if (cursor instanceof Cursor) {
            ((Cursor)this.cursor).moveToNext();
        }
    }

    public int getColumnIndex(String columnName) {
        if (cursor instanceof Cursor) {
            return  ((Cursor)this.cursor).getColumnIndex(columnName);
        }
        return 0;
    }

    public String getString(int columnIndex) {
        if (cursor instanceof Cursor) {
            ((Cursor)this.cursor).getString(columnIndex);
        }
        return "";
    }

    public void close() {
        if (cursor instanceof Cursor) {
            ((Cursor)this.cursor).close();
        }

    }
}
