package athg2sms;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.athg2sms.business.export.MessageMapper;
import org.toilelibre.libe.athg2sms.business.pattern.BuiltInFormatName;
import org.toilelibre.libe.athg2sms.business.sms.Folder;
import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.util.HashMap;
import java.util.Map;

public class PatternToExportPatternTest {

    @Test
    public void withNokiaSuite ()  {

        long timestamp = 1487003796000L;
        Map<Sms.Part, Object> sampleMessageMap = new HashMap<Sms.Part, Object> ();
        sampleMessageMap.put (Sms.Part.FOLDER, Folder.INBOX);
        sampleMessageMap.put (Sms.Part.DATE, timestamp);
        sampleMessageMap.put (Sms.Part.ADDRESS, "+33238792342");
        sampleMessageMap.put (Sms.Part.BODY, "Da da dee dow dow...");
        Assert.assertEquals("\"sms\",\"READ,RECEIVED\",\"+33238792342\",\"\",\"\",\"2017.02.13 17:36\",\"\",\"Da da dee dow dow...\"\n", new MessageMapper().convert (new Sms(sampleMessageMap),
                BuiltInFormatName.NokiaSuite.getValue ()));
    }

    @Test
    public void withVMG ()  {
        long timestamp = 1487003796000L;
        Map<Sms.Part, Object> sampleMessageMap = new HashMap<Sms.Part, Object> ();
        sampleMessageMap.put (Sms.Part.FOLDER, Folder.INBOX);
        sampleMessageMap.put (Sms.Part.DATE, timestamp);
        sampleMessageMap.put (Sms.Part.ADDRESS, "+33238792342");
        sampleMessageMap.put (Sms.Part.BODY, "Da da dee dow dow...");
        Assert.assertEquals(
                "BEGIN:VMSG\r\n" +
                        "VERSION: 1.1\r\n" +
                        "BEGIN:VCARD\r\n" +
                        "TEL:+33238792342\r\n" +
                        "END:VCARD\r\n" +
                        "BEGIN:VBODY\r\n" +
                        "X-BOX:INBOX\r\n" +
                        "X-READ:READ\r\n" +
                        "X-SIMID:0\r\n" +
                        "X-LOCKED:UNLOCKED\r\n" +
                        "X-TYPE:SMS\r\n" +
                        "Date:2017/02/13 17:36:36\r\n" +
                        "Subject;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8:Da da dee dow dow...\r\n" +
                        "END:VBODY\r\n" +
                        "END:VMSG\r\n", new MessageMapper().convert (new Sms(sampleMessageMap),
                BuiltInFormatName.LumiaVmg.getValue ()));

    }
}
