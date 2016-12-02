package org.toilelibre.libe.athg2sms.androidstuff;

import android.content.Context;
import android.content.Intent;

import org.toilelibre.libe.athg2sms.preferences.AppPreferences;

import java.lang.reflect.InvocationTargetException;

public class SmsApplicationToggle {
    private static String       ACTION_CHANGE_DEFAULT = "android.provider.Telephony.ACTION_CHANGE_DEFAULT";
    private static final String EXTRA_PACKAGE_NAME    = "package";

    public void toggleDefault(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            final String myPackageName = context.getPackageName ();
            if (!this.getDefaultSmsPackage (context).equals (myPackageName)) {
                new AppPreferences(context.getSharedPreferences ("athg2sms", 0))
                        .saveDefaultSmsApp (this.getDefaultSmsPackage (context));
                final Intent intentSetDefault = new Intent (SmsApplicationToggle.ACTION_CHANGE_DEFAULT);
                intentSetDefault.putExtra (SmsApplicationToggle.EXTRA_PACKAGE_NAME, myPackageName);
                context.startActivity (intentSetDefault);
            } else {
                final String packageName = new AppPreferences(context.getSharedPreferences ("athg2sms", 0))
                        .getDefaultSmsApp ();
                final Intent intentSetDefault = new Intent (SmsApplicationToggle.ACTION_CHANGE_DEFAULT);
                intentSetDefault.putExtra (SmsApplicationToggle.EXTRA_PACKAGE_NAME, packageName);
                context.startActivity (intentSetDefault);

            }
        }

    }

    public String getDefaultSmsPackage (final Context context) {
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
