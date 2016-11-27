package athg2sms;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.toilelibre.libe.athg2sms.business.export.MapToMessage;
import org.toilelibre.libe.athg2sms.business.pattern.BuiltInFormatName;

/**
 * Created by lionel on 24/11/16.
 */

public class PatternToExportPatternTest {

    @Test
    public void withNokiaSuite ()  {

        long timestamp = System.currentTimeMillis ();
        Map<String, Object> sampleMessageMap = new HashMap<String, Object> ();
        sampleMessageMap.put ("folder", "inbox");
        sampleMessageMap.put ("date", timestamp);
        sampleMessageMap.put ("address", "+33238792342");
        sampleMessageMap.put ("body", "Da da dee dow dow...");
        System.out.println(MapToMessage.convert (sampleMessageMap, 
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
        System.out.println(MapToMessage.convert (sampleMessageMap,
                BuiltInFormatName.LumiaVmg.getValue ()));

    }
}
