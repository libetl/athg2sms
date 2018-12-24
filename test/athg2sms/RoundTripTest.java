package athg2sms;

import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.toilelibre.libe.athg2sms.business.pattern.BuiltInFormat;

import java.net.URISyntaxException;

import static athg2sms.Athg2SmsJUnitTester.exportNow;
import static athg2sms.Athg2SmsJUnitTester.importString;

public class RoundTripTest {


    @Test
    public void xmlMsgFile () throws URISyntaxException {
        String inboxAndSentMessages1 =
                "<Message><Recepients><string>+33612345678</string></Recepients><Body>Say Hello Joe</Body><IsIncoming>false</IsIncoming><IsRead>true</IsRead><Attachments /><LocalTimestamp>131230002909460000</LocalTimestamp><Sender /></Message>" +
                "<Message><Recepients /><Body>Hello Joe</Body><IsIncoming>true</IsIncoming><IsRead>true</IsRead><Attachments /><LocalTimestamp>131230002746920000</LocalTimestamp><Sender>+33623456789</Sender></Message>";

        String inboxAndSentMessages2 =
                "<Message><Recepients /><Body>Hello Joe</Body><IsIncoming>true</IsIncoming><IsRead>true</IsRead><Attachments /><LocalTimestamp>131230002746920000</LocalTimestamp><Sender>+33623456789</Sender></Message>" +
                "<Message><Recepients><string>+33612345678</string></Recepients><Body>Say Hello Joe</Body><IsIncoming>false</IsIncoming><IsRead>true</IsRead><Attachments /><LocalTimestamp>131230002909460000</LocalTimestamp><Sender /></Message>";

        Athg2SmsJUnitTester.JunitConvertListener importListener = importString(inboxAndSentMessages1, BuiltInFormat.XmlMessage);

        String result = exportNow(importListener.getMessages(), BuiltInFormat.XmlMessage);

        try {
            Assert.assertEquals(inboxAndSentMessages1, result);
        }catch(ComparisonFailure comparisonFailure){
            Assert.assertEquals(inboxAndSentMessages2, result);
        }
    }
}
