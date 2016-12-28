package org.toilelibre.libe.athg2sms.business.convert;

import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

public interface ConvertListener<T> extends Serializable {

    ConvertListener bind ();

    int delete (URI uriDelete, String where, String [] strings);

    void displayInserted (final ContextHolder<T> contextHolder, final int inserted, final int dupl);

    void end ();

    void insert (URI uri, Map<String, Object> smsValues);

    void sayIPrepareTheList (ContextHolder<T> contextHolder, int size);

    void setMax (int nb2);

    void updateProgress (String text, int i2, int nb2);

}
