package org.toilelibre.libe.athg2sms.kitkatwrapper;

import java.lang.reflect.InvocationTargetException;

import android.content.Context;

public class Sms {

	public static class Intents {
		public static String ACTION_CHANGE_DEFAULT = "android.provider.Telephony.ACTION_CHANGE_DEFAULT";
	    public static final String EXTRA_PACKAGE_NAME = "package";
		
	}

	public static String getDefaultSmsPackage (
            Context context) {
		String result = null;
		try {
	        Class<?> c = Class.forName ("android.provider.Telephony$Sms");
	        result = (String) c.getMethod ("getDefaultSmsPackage", Context.class).invoke (null, context);
        } catch (ClassNotFoundException e) {
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
	    return result;
    }

}
