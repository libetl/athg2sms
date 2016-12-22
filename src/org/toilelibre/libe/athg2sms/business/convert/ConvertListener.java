package org.toilelibre.libe.athg2sms.business.convert;

import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

public interface ConvertListener extends Serializable {

    ConvertListener bind ();

    int delete (URI uriDelete, String where, String [] strings);

    void displayInserted (int inserted, int dupl);

    void end ();

    void insert (URI uri, Map<String, Object> smsValues);

    void sayIPrepareTheList (int size);

    void setMax (int nb2);

    void updateProgress (String text, int i2, int nb2);

}
