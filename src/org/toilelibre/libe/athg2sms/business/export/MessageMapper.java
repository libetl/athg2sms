package org.toilelibre.libe.athg2sms.business.export;

import org.toilelibre.libe.athg2sms.business.pattern.Format.FormatRegexRepresentation;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;
import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.toilelibre.libe.athg2sms.business.sms.RawMatcherResult.INBOX;

public class MessageMapper {

    public MessageMapper(){}

    public String convert (Sms sms, String pattern) {
        final FormatRegexRepresentation theFormatRegexRepresentation = FormatSettings.getInstance().getFormats().get(pattern).getRegex();
        String result = theFormatRegexRepresentation.getExportFormat();
        result = result.replace ("$(folder)", 
                INBOX.equals (sms.getFolder()) ?
                        theFormatRegexRepresentation.getInboxKeyword() : theFormatRegexRepresentation.getSentKeyword() );
        result = result.replace ("$(" + sms.getFolder() + ":address)", sms.getAddress());
        result = result.replace ("$(address)", sms.getAddress());
        result = result.replace ("$(inbox:address)", "");
        result = result.replace ("$(sent:address)", "");
        result = result.replace ("$(body)",sms.getBody());
        int startOfDate = result.indexOf ("$(date") + 7;
        int endOfDate = result.indexOf (')', startOfDate);
        String datePattern = result.substring (startOfDate, endOfDate);
        
        DateFormat df = new SimpleDateFormat (datePattern, Locale.getDefault());
        result = result.substring (0, startOfDate - 7) +
                df.format (new Date (sms.getDate())) +
                result.substring (endOfDate + 1);
        
        return result;
    }
}
