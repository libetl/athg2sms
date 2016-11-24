package athg2sms;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.toilelibre.libe.athg2sms.settings.DefaultSettings;
import org.toilelibre.libe.athg2sms.util.MapToMessage;

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
                DefaultSettings.BuiltInConversionSets.NokiaSuite.getValue ()));

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
                DefaultSettings.BuiltInConversionSets.LumiaVmg.getValue ()));

    }
}
