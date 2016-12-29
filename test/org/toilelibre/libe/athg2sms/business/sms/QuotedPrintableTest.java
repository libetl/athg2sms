package org.toilelibre.libe.athg2sms.business.sms;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by lionel on 29/12/16.
 */

public class QuotedPrintableTest {
    @Test
    public void edgeCase () {
        Assert.assertNotNull(new QuotedPrintable().decodeQuotedPrintable("Ah ok mince c'est dans le rayon ou il y a le vin blanc en g=C3=A9n=C3=A9ral=".getBytes()));
    }
}
