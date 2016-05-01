package athg2sms;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

import org.junit.Test;
import org.toilelibre.libe.athg2sms.bp.ConvertListener;
import org.toilelibre.libe.athg2sms.bp.ConvertV4;
import org.toilelibre.libe.athg2sms.settings.DefaultSettings.BuiltInConversionSets;
import org.toilelibre.libe.athg2sms.settings.SettingsFactory;

import junit.framework.Assert;

public class ReadFileTest {
    private int                   messagesInserted = 0;

    private final ConvertListener convertListener  = new ConvertListener () {

                                                       public int delete (final URI uriDelete, final String where, final String [] strings) {
                                                           return 0;
                                                       }

                                                       public void displayInserted (final int inserted, final int dupl) {

                                                       }

                                                       public void end () {

                                                       }

                                                       public void insert (final URI uri, final Map<String, Object> values) {
                                                           System.out.println (values);
                                                           ReadFileTest.this.messagesInserted++;
                                                       }

                                                       public void sayIPrepareTheList (final int size) {

                                                       }

                                                       public void setMax (final int nb2) {
                                                           ReadFileTest.this.messagesInserted = 0;
                                                       }

                                                       public void updateProgress (final int i2, final int nb2) {
                                                       }

                                                   };

    @Test
    public void csvSms () throws URISyntaxException {
        // "\"Created\",\"Number\",\"Sender Name\",\"Text\",\"Folder\"\n"
        this.testString ("\"2016-04-19 01:04:34\",\"VM-FCHRGE\",\"\",\"Dear customer, You have made a Debit\",\"INBOX\"\n" + "\"2016-04-19 17:24:11\",\"ID-IDEA\",\"\",\"UR BSNL a/c Topup with Rs. 10 by 2222\",\"INBOX\"\n", BuiltInConversionSets.DateAndAddressAndBodyAndINBOX, false);
        Assert.assertEquals (2, this.messagesInserted);
    }

    @Test
    public void empty () throws URISyntaxException {
        this.testString ("", BuiltInConversionSets.NokiaCsv, true);
    }

    @Test
    public void indianGuy () throws URISyntaxException {
        this.testFile ("athg2sms/DE.csv", BuiltInConversionSets.DateAndFromAndAddressAndbody, false);
    }

    @Test
    public void lionel () throws URISyntaxException {
        this.testFile ("/mnt/data/lionel/Documents/misc/NouvelOrdi/Msgs5200.csv", BuiltInConversionSets.NokiaCsv, false);
    }

    @Test
    public void yetAnotherTest () throws URISyntaxException {
        this.testFile ("/mnt/data/lionel/Documents/misc/NouvelOrdi/test.csv", BuiltInConversionSets.NokiaCsv, false);
    }

    @Test
    public void nokiaCsv () throws URISyntaxException {
        this.testString ("sms;deliver;\"+33612345678\";\"\";\"\";\"2016.03.22 15:46\";\"\";\"First message\"\n" + "sms;submit;\"\";\"+33612345678\";\"\";\"2016.03.22 15:48\";\"\";\"Answer to the first message\"", BuiltInConversionSets.NokiaCsv, true);
        Assert.assertEquals (2, this.messagesInserted);
    }

    @Test
    public void loremIpsum () throws URISyntaxException {
        this.testString ("-545061504,Fri Feb 19 03:18:04 EST 2010,Thu Feb 18 16:18:10 EST 2010,false,+61422798642,\"Lorem ipsumRecu\"\n" + "-491825428,Fri Feb 19 07:05:26 EST 2010,Fri Feb 19 07:05:26 EST 2010,true,+61432988391,\"Lorem ipsumSent\"", BuiltInConversionSets.BlackberryCsv, false);
        Assert.assertEquals (2, this.messagesInserted);
    }

    public void testFile (final String classpathFile, final BuiltInConversionSets conversionSet, final boolean shouldBeEmpty) throws URISyntaxException {
        // Given
        final ConvertV4 convertV4 = new ConvertV4 ();
        final URL url = ReadFileTest.class.getClassLoader ().getResource (classpathFile);
        try {
            final Scanner scan = new Scanner (url == null ? new File (classpathFile) : new File (url.toURI ()));
            scan.useDelimiter ("\\Z");
            final String content = scan.next ();
            convertV4.setContentToBeParsed (content);
        } catch (final FileNotFoundException e) {
            throw new RuntimeException (e);
        }
        convertV4.setConvertListener (this.convertListener);
        SettingsFactory.asV4 ().chooseSet (conversionSet.getValue ());

        // When
        convertV4.run ();

        // then
        if (!shouldBeEmpty) {
            Assert.assertTrue (this.messagesInserted > 0);
        }
    }

    public void testString (final String content, final BuiltInConversionSets conversionSet, final boolean shouldBeEmpty) throws URISyntaxException {
        // Given
        final ConvertV4 convertV4 = new ConvertV4 ();
        convertV4.setContentToBeParsed (content);
        convertV4.setConvertListener (this.convertListener);
        SettingsFactory.asV4 ().chooseSet (conversionSet.getValue ());

        // When
        convertV4.run ();

        // then
        if (!shouldBeEmpty) {
            Assert.assertTrue (this.messagesInserted > 0);
        }
    }
}
