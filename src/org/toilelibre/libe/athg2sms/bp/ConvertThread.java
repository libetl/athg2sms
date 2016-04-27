package org.toilelibre.libe.athg2sms.bp;

public interface ConvertThread {

    public ConvertException getException ();

    public int getInserted ();

    public void interrupt ();

    public void run ();

    public void setContentToBeParsed (String content);

    public void setConvertListener (ConvertListener cl);

    public void setHandler (Object handler);

    public void start ();
}
