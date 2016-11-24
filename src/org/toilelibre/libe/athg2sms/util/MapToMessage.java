package org.toilelibre.libe.athg2sms.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.toilelibre.libe.athg2sms.settings.DefaultSettings;
import org.toilelibre.libe.athg2sms.settings.Settings;

public class MapToMessage {

    public static String convert (Map<String, Object> message, String pattern) {
        final Map<String, String> theSet = Settings.getSet (pattern);
        final String exportFormat = theSet.get (DefaultSettings.EXPORT_FORMAT);
        String result = exportFormat;
        result = result.replace ("$(folder)", 
                DefaultSettings.INBOX.equals (message.get ("folder")) ?
                        theSet.get (DefaultSettings.INBOX_KEYWORD) :
                            theSet.get (DefaultSettings.SENT_KEYWORD));
        result = result.replace ("$(" + message.get ("folder").toString () + ":address)",
                message.get ("address").toString ());
        result = result.replace ("$(address)",
                message.get ("address").toString ());
        result = result.replace ("$(inbox:address)","");
        result = result.replace ("$(sent:address)","");
        result = result.replace ("$(body)",message.get ("body").toString ());
        int startOfDate = result.indexOf ("$(date") + 7;
        int endOfDate = result.indexOf (')', startOfDate);
        String datePattern = result.substring (startOfDate, endOfDate);
        
        DateFormat df = new SimpleDateFormat (datePattern);
        result = result.substring (0, startOfDate - 7) +
                df.format (new Date ((Long)message.get ("date"))) + 
                result.substring (endOfDate + 1);
        
        return result;
    }
}
