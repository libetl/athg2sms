package org.toilelibre.libe.athg2sms.bp;

import java.io.InputStream;

public interface ConvertThread {

	public Object getException ();

	public int getInserted ();

	public void interrupt ();

	public void run ();

	public void setConvertListener (ConvertListener cl);

	public void setInputStream (InputStream is);

	public void setHandler (Object handler);

	public void start ();
}
