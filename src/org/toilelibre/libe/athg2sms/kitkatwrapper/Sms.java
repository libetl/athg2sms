package org.toilelibre.libe.athg2sms.kitkatwrapper;

import java.lang.reflect.InvocationTargetException;

import android.content.Context;

public class Sms {

    public static class Intents {
        public static String       ACTION_CHANGE_DEFAULT = "android.provider.Telephony.ACTION_CHANGE_DEFAULT";
        public static final String EXTRA_PACKAGE_NAME    = "package";

    }

    public static String getDefaultSmsPackage (final Context context) {
        String result = null;
        try {
            final Class<?> c = Class.forName ("android.provider.Telephony$Sms");
            result = (String) c.getMethod ("getDefaultSmsPackage", Context.class).invoke (null, context);
        } catch (final ClassNotFoundException e) {
        } catch (final SecurityException e) {
        } catch (final NoSuchMethodException e) {
        } catch (final IllegalArgumentException e) {
        } catch (final IllegalAccessException e) {
        } catch (final InvocationTargetException e) {
        }
        return result;
    }

}
