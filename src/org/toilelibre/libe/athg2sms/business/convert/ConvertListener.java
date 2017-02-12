package org.toilelibre.libe.athg2sms.business.convert;

import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.io.Serializable;
import java.net.URI;

public interface ConvertListener<T> extends Serializable {

    ConvertListener bind ();

    int delete (URI uriDelete, String where, String [] strings);

    void displayInserted(final ContextHolder<T> contextHolder, final Converter.ConversionResult result);

    void end ();

    void insert (URI uri, Sms sms);

    void sayIPrepareTheList (ContextHolder<T> contextHolder, int size);

    void setMax (int nb2);

    void updateProgress (String text, int i2, int nb2);

}
