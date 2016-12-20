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
        Map<String, Object> sampleMessageMap = new HashMap<String, Object> ();
        sampleMessageMap.put ("folder", "inbox");
        sampleMessageMap.put ("date", timestamp);
        sampleMessageMap.put ("address", "+33238792342");
        sampleMessageMap.put ("body", "Da da dee dow dow...");
        System.out.println(new MessageMapper().convert (new Sms(sampleMessageMap),
                BuiltInFormatName.NokiaSuite.getValue ()));
    }

    @Test
    public void withVMG ()  {
        long timestamp = System.currentTimeMillis ();
        Map<String, Object> sampleMessageMap = new HashMap<String, Object> ();
        sampleMessageMap.put ("folder", "inbox");
        sampleMessageMap.put ("date", timestamp);
        sampleMessageMap.put ("address", "+33238792342");
        sampleMessageMap.put ("body", "Da da dee dow dow...");
        System.out.println(new MessageMapper().convert (new Sms(sampleMessageMap),
                BuiltInFormatName.LumiaVmg.getValue ()));

    }
}
