package athg2sms;

import org.junit.Test;
import org.toilelibre.libe.athg2sms.business.export.MessageMapper;
import org.toilelibre.libe.athg2sms.business.pattern.BuiltInFormatName;
import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.util.HashMap;
import java.util.Map;

public class PatternToExportPatternTest {

    @Test
    public void withNokiaSuite ()  {

        long timestamp = System.currentTimeMillis ();
        Map<Sms.Part, Object> sampleMessageMap = new HashMap<Sms.Part, Object> ();
        sampleMessageMap.put (Sms.Part.FOLDER, "inbox");
        sampleMessageMap.put (Sms.Part.DATE, timestamp);
        sampleMessageMap.put (Sms.Part.ADDRESS, "+33238792342");
        sampleMessageMap.put (Sms.Part.BODY, "Da da dee dow dow...");
        System.out.println(new MessageMapper().convert (new Sms(sampleMessageMap),
                BuiltInFormatName.NokiaSuite.getValue ()));
    }

    @Test
    public void withVMG ()  {
        long timestamp = System.currentTimeMillis ();
        Map<Sms.Part, Object> sampleMessageMap = new HashMap<Sms.Part, Object> ();
        sampleMessageMap.put (Sms.Part.FOLDER, "inbox");
        sampleMessageMap.put (Sms.Part.DATE, timestamp);
        sampleMessageMap.put (Sms.Part.ADDRESS, "+33238792342");
        sampleMessageMap.put (Sms.Part.BODY, "Da da dee dow dow...");
        System.out.println(new MessageMapper().convert (new Sms(sampleMessageMap),
                BuiltInFormatName.LumiaVmg.getValue ()));

    }
}
