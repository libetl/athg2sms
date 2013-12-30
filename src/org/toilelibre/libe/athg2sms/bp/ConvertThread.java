package org.toilelibre.libe.athg2sms.bp;

import java.io.File;

import android.os.Handler;

public interface ConvertThread {

	public Object getException ();

	public int getInserted ();

	public void interrupt ();

	public void run ();

	public void setConvertListener (ConvertListener cl);

	public void setFile (File f);

	public void setHandler (Handler handler);

	public void start ();
}
