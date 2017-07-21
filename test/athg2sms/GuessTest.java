package athg2sms;

import junit.framework.Assert;

import org.junit.Test;
import org.toilelibre.libe.athg2sms.actions.Actions;

import java.io.FileNotFoundException;

public class GuessTest {

    @Test
    public void victorInputShouldBeGuessed () throws FileNotFoundException {
        //given
        String text = "number,address,content,date,send|receive,person,,\n" +
                "14,2.34807E+12,8132581738,16/10/2014 12:01,other,2.34807E+12,,This is a draft message\n" +
                "17,8091924903,08037598576 - Dayo,23/04/2014 16:43,other,8091924903,,This is a draft message\n" +
                "29,2.34807E+12,How far...abeg hola wen done. Cos right nw ur boi done almst dey stranded. Tnx,18/05/2014 17:56,receive,413,,This is a received message\n" +
                "30,2.34807E+12,Aight.,18/09/2014 14:42,receive,413,,This is a sent message\n" +
                "31,8065089071,Call you later,18/09/2014 10:45,send,me,,\n";

        //when
        String patternName = new Actions().guessNow(text);

        //then
        Assert.assertNotNull(patternName);
    }
}
