package org.toilelibre.libe.athg2sms.business.export;

import org.toilelibre.libe.athg2sms.business.pattern.Format.FormatRegexRepresentation;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;
import org.toilelibre.libe.athg2sms.business.sms.QuotedPrintable;
import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static org.toilelibre.libe.athg2sms.business.sms.Folder.INBOX;

public class MessageMapper {

    public MessageMapper(){}

    private long toMicrosoftTimestamp(long value) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(value);
        calendar.add(Calendar.YEAR, 369);
        //there are 369 years difference between unix timestamp and microsoft timestamp
        return calendar.getTimeInMillis() * 10000;
    }
    public String convert (Sms sms, String pattern) {
        final FormatRegexRepresentation theFormatRegexRepresentation = FormatSettings.getInstance().getFormats().get(pattern).getRegex();
        String result = INBOX == sms.getFolder() ?
                theFormatRegexRepresentation.getExportFormatForInbox() :
                theFormatRegexRepresentation.getExportFormatForSent();
        result = result.replace ("$(folder)", 
                INBOX == sms.getFolder() ?
                        theFormatRegexRepresentation.getInboxKeyword() : theFormatRegexRepresentation.getSentKeyword() );
        result = result.replace ("$(" + sms.getFolder().getFolderName() + ":address)", sms.getAddress());
        result = result.replace ("$(address)", sms.getAddress());
        result = result.replace ("$(inbox:address)", "");
        result = result.replace ("$(sent:address)", "");
        result = replaceBody (result, sms, theFormatRegexRepresentation);
        int startOfDateVar = Math.max(result.indexOf ("$(date"), result.indexOf ("$(localtimestamp"));
        int startOfDatePattern = Math.max(result.indexOf ("$(date") + 6, result.indexOf ("$(localtimestamp") + 16);
        int endOfDate = result.indexOf (')', startOfDatePattern);
        boolean localtimestamp = result.contains("$(localtimestamp)");
        String datePattern = result.substring (startOfDatePattern, endOfDate);
        
        if ("".equals(datePattern)) {
            result = result.substring (0, startOfDateVar) +
                    (localtimestamp ? toMicrosoftTimestamp(sms.getDate()) : sms.getDate()) +
                    result.substring (endOfDate + 1);
        }else{
            DateFormat df = new SimpleDateFormat (datePattern, Locale.getDefault());
            result = result.substring (0, startOfDateVar) +
                    df.format (new Date (sms.getDate())) +
                    result.substring (endOfDate + 1);
        }

        return result;
    }

    private String replaceBody(String result, Sms sms, FormatRegexRepresentation theFormatRegexRepresentation) {
        char endToken = theFormatRegexRepresentation.getExportFormat().charAt(
                theFormatRegexRepresentation.getExportFormat().indexOf("$(body)") + 7);
        String body = sms.getBody();
        if (endToken == '"') {
            body = body.replaceAll("\"", "\\\"");
        }
        if (endToken == '\'') {
            body = body.replaceAll("\'", "\\\'");
        }
        if (theFormatRegexRepresentation.getCommonRegex().contains("$(encoding)")) {
            body = new String(new QuotedPrintable ().encodeQuotedPrintable(body.getBytes()));
        }
        return result.replace ("$(body)", body);
    }
}
