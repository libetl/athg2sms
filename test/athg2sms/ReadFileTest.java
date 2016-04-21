package athg2sms;

import java.io.ByteArrayInputStream;
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
import org.toilelibre.libe.athg2sms.settings.SettingsFactory;

public class ReadFileTest {

    private ConvertListener convertListener = new ConvertListener () {

        public void displayInserted (int inserted, int dupl) {
            
        }

        public void end () {
            
        }

        public void sayIPrepareTheList (int size) {
            
        }

        public void setMax (int nb2) {
            
        }

        public void updateProgress (int i2, int nb2) {
            
        }

        public int delete (URI uriDelete, String where, String [] strings) {
            return 0;
        }

        public void insert (URI uri, Map<String, Object> values) {
            System.out.println (values);
        }
        
    };

    
    public void testFile (String classpathFile, String setName) throws URISyntaxException {
        //Given
        ConvertV3 convertV3 = new ConvertV3 ();
        URL url = ReadFileTest.class.getClassLoader ().getResource (classpathFile);
        try {
            Scanner scan = new Scanner(new File (url.toURI ()));
            scan.useDelimiter("\\Z");  
            String content = scan.next(); 
            convertV3.setInputStream (new ByteArrayInputStream (content.getBytes ()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException (e);
        }  
        convertV3.setConvertListener (convertListener );
        SettingsFactory.asV3 ().chooseSet (setName);
        
        //When
        convertV3.run ();
    }

    
    public void testString (String content, String setName) throws URISyntaxException {
        //Given
        ConvertV3 convertV3 = new ConvertV3 ();
        convertV3.setInputStream (new ByteArrayInputStream (content.getBytes ()));
        convertV3.setConvertListener (convertListener );
        SettingsFactory.asV3 ().chooseSet (setName);
        
        //When
        convertV3.run ();
    }

    @Test
    public void indianGuy () throws URISyntaxException {
        this.testFile ("athg2sms/DE.csv", "Date+'from'+address+body");
    }

    @Test
    public void csvSms () throws URISyntaxException {
        //"\"Created\",\"Number\",\"Sender Name\",\"Text\",\"Folder\"\n"
        this.testString (
                "\"2016-04-19 01:04:34\",\"VM-FCHRGE\",\"\",\"Dear customer, You have made a Debit\",\"INBOX\"\n"+
                "\"2016-04-19 17:24:11\",\"ID-IDEA\",\"\",\"UR BSNL a/c Topup with Rs. 10 by 2222\",\"INBOX\"\n",
                "Date+address+body+INBOX");
    }
}
