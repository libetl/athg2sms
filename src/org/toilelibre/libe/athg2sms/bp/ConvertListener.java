package org.toilelibre.libe.athg2sms.bp;

import android.content.ContentResolver;

public interface ConvertListener {

	void displayInserted (int inserted);

	void end ();

	ContentResolver getContentResolver ();

	void sayIPrepareTheList (int size);

	void setMax (int nb2);

	void updateProgress (int i2, int nb2);

}
