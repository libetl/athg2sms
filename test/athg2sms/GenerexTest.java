package athg2sms;

import com.mifmif.common.regex.Generex;

import org.junit.Test;

public class GenerexTest {

    @Test
    public void tryGenerex () {
        Generex generex = new Generex(".{1,20}ghhjhju,");
        String s = generex.random(12, 160);
    }
}
