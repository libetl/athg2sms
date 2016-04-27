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
import org.toilelibre.libe.athg2sms.bp.ConvertV3;
import org.toilelibre.libe.athg2sms.bp.ConvertV4;
import org.toilelibre.libe.athg2sms.settings.SettingsFactory;

public class ReadFileTest {

    private final ConvertListener convertListener = new ConvertListener () {

        public int delete (final URI uriDelete, final String where, final String [] strings) {
            return 0;
        }

        public void displayInserted (final int inserted, final int dupl) {

        }

        public void end () {

        }

        public void insert (final URI uri, final Map<String, Object> values) {
            System.out.println (values);
        }

        public void sayIPrepareTheList (final int size) {

        }

        public void setMax (final int nb2) {

        }

        public void updateProgress (final int i2, final int nb2) {

        }

    };

    @Test
    public void csvSms () throws URISyntaxException {
        // "\"Created\",\"Number\",\"Sender Name\",\"Text\",\"Folder\"\n"
        this.testString ("\"2016-04-19 01:04:34\",\"VM-FCHRGE\",\"\",\"Dear customer, You have made a Debit\",\"INBOX\"\n"
                + "\"2016-04-19 17:24:11\",\"ID-IDEA\",\"\",\"UR BSNL a/c Topup with Rs. 10 by 2222\",\"INBOX\"\n", "Date+address+body+INBOX");
    }

    @Test
    public void indianGuy () throws URISyntaxException {
        this.testFile ("athg2sms/DE.csv", "Date+'from'+address+body");
    }

    public void testFile (final String classpathFile, final String setName) throws URISyntaxException {
        // Given
        final ConvertV4 convertV4 = new ConvertV4 ();
        final URL url = ReadFileTest.class.getClassLoader ().getResource (classpathFile);
        try {
            final Scanner scan = new Scanner (new File (url.toURI ()));
            scan.useDelimiter ("\\Z");
            final String content = scan.next ();
            convertV4.setContentToBeParsed (content);
        } catch (final FileNotFoundException e) {
            throw new RuntimeException (e);
        }
        convertV4.setConvertListener (this.convertListener);
        SettingsFactory.asV3 ().chooseSet (setName);

        // When
        convertV4.run ();
    }

    public void testString (final String content, final String setName) throws URISyntaxException {
        // Given
        final ConvertV3 convertV3 = new ConvertV3 ();
        convertV3.setContentToBeParsed (content);
        convertV3.setConvertListener (this.convertListener);
        SettingsFactory.asV4 ().chooseSet (setName);

        // When
        convertV3.run ();
    }
}
