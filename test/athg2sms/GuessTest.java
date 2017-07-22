package athg2sms;

import junit.framework.Assert;

import org.junit.Test;
import org.toilelibre.libe.athg2sms.actions.Actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.fail;

public class GuessTest {

    @Test
    public void nokiaCsvGuess () throws IOException, URISyntaxException {
        //given
        String classpathFile = "athg2sms/nokia.csv";
        final URL url = ImportTest.class.getClassLoader ().getResource (classpathFile);
        String content;
        try {
            final File file = url == null ? new File (classpathFile) : new File (url.toURI ());
            byte[] buffer = new byte[4096];
            InputStream inputStream = new FileInputStream(file);
            if (inputStream.read(buffer) == 0) {
                fail();
            }
            content = new String(buffer);
        } catch (final IOException e) {
            throw new RuntimeException (e);
        }

        //when
        String patternName = new Actions().guessNow(content);

        //then
        Assert.assertNotNull(patternName);
    }

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
