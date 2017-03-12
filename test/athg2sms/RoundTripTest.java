package athg2sms;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.athg2sms.business.pattern.BuiltInFormat;

import java.net.URISyntaxException;

import static athg2sms.Athg2SmsJUnitTester.exportNow;
import static athg2sms.Athg2SmsJUnitTester.importString;

public class RoundTripTest {


    @Test
    public void xmlMsgFile () throws URISyntaxException {
        String inboxAndSentMessage =
                "<Message><Recepients><string>+33612345678</string></Recepients><Body>Say Hello Joe</Body><IsIncoming>false</IsIncoming><IsRead>true</IsRead><Attachments /><LocalTimestamp>131230002909464794</LocalTimestamp><Sender /></Message>" +
                "<Message><Recepients /><Body>Hello Joe</Body><IsIncoming>true</IsIncoming><IsRead>true</IsRead><Attachments /><LocalTimestamp>131230002746920903</LocalTimestamp><Sender>+33623456789</Sender></Message>";
        Athg2SmsJUnitTester.JunitConvertListener importListener = importString(inboxAndSentMessage, BuiltInFormat.XmlMessage);

        String result = exportNow(importListener.getMessages(), BuiltInFormat.XmlMessage);

        Assert.assertEquals (result, inboxAndSentMessage);
    }
}
