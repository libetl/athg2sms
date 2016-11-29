package org.toilelibre.libe.athg2sms.business.export;

import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;
import org.toilelibre.libe.athg2sms.business.pattern.Format.FormatRegexRepresentation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static org.toilelibre.libe.athg2sms.business.sms.RawMatcherResult.INBOX;

public class MapToMessage {

    public static String convert (Map<String, Object> message, String pattern) {
        final FormatRegexRepresentation theFormatRegexRepresentation = FormatSettings.getInstance().getFormats().get(pattern).getRegex();
        String result = theFormatRegexRepresentation.getExportFormat();
        result = result.replace ("$(folder)", 
                INBOX.equals (message.get ("folder")) ?
                        theFormatRegexRepresentation.getInboxKeyword() : theFormatRegexRepresentation.getSentKeyword() );
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
        
        DateFormat df = new SimpleDateFormat (datePattern, Locale.getDefault());
        result = result.substring (0, startOfDate - 7) +
                df.format (new Date ((Long)message.get ("date"))) + 
                result.substring (endOfDate + 1);
        
        return result;
    }
}
