package org.toilelibre.libe.athg2sms.bp;

import java.net.URI;
import java.util.Map;

public interface ConvertListener {

	void displayInserted (int inserted, int dupl);

	void end ();

	void sayIPrepareTheList (int size);

	void setMax (int nb2);

	void updateProgress (int i2, int nb2);

    int delete (URI uriDelete, String where, String [] strings);

    void insert (URI uri, Map<String, Object> values);

}
