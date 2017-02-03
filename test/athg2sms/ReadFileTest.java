package athg2sms;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.athg2sms.androidstuff.api.activities.ContextHolder;
import org.toilelibre.libe.athg2sms.business.convert.ConvertException;
import org.toilelibre.libe.athg2sms.business.convert.ConvertListener;
import org.toilelibre.libe.athg2sms.business.convert.Converter;
import org.toilelibre.libe.athg2sms.business.pattern.BuiltInFormatName;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;
import org.toilelibre.libe.athg2sms.business.sms.Sms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class ReadFileTest {
    private int                       messagesInserted = 0;
    private List<Sms> messages = new ArrayList<Sms>();

    private final ConvertListener<Object> convertListener  = new ConvertListener<Object> () {

        @Override
        public ConvertListener bind() {
            return null;
        }

        public int delete (final URI uriDelete, final String where, final String [] strings) {
            return 0;
        }

        public void displayInserted(final ContextHolder<Object> contextHolder, final Converter.ConversionResult result) {

        }

        public void end () {

        }

        public void insert (final URI uri, final Map<String, Object> smsValues) {
            System.out.println (smsValues);
            messages.add (new Sms(smsValues));
            ReadFileTest.this.messagesInserted++;
        }

        public void sayIPrepareTheList (final ContextHolder<Object> contextHolder, final int size) {
        }

        public void setMax (final int nb2) {
            messages = new ArrayList<Sms>(nb2);
            ReadFileTest.this.messagesInserted = 0;
        }

        public void updateProgress (final String text, final int i2, final int nb2) {
        }

    };

    @Test
    public void csvSms () throws URISyntaxException {
        // "\"Created\",\"Number\",\"Sender Name\",\"Text\",\"Folder\"\n"
        this.testString ("\"2016-04-19 01:04:34\",\"VM-FCHRGE\",\"\",\"Dear customer, You have made a Debit\",\"INBOX\"\n" + "\"2016-04-19 17:24:11\",\"ID-IDEA\",\"\",\"UR BSNL a/c Topup with Rs. 10 by 2222\",\"INBOX\"\n", BuiltInFormatName.DateAndAddressAndBodyAndINBOX, false);
        Assert.assertEquals (2, this.messagesInserted);
    }

    @Test(expected=ConvertException.class)
    public void empty () throws URISyntaxException {
        this.testString ("", BuiltInFormatName.NokiaCsv, true);
    }

    @Test
    public void indianGuy () throws URISyntaxException {
        this.testFile ("athg2sms/DE.csv", BuiltInFormatName.DateAndFromAndAddressAndbody, false);
    }

    @Test
    public void aleTxt () throws URISyntaxException {
        this.testFile ("athg2sms/ale.txt", BuiltInFormatName.NokiaCsvWithCommas, false);
    }

    @Test
    public void otherOtherNokia () throws URISyntaxException {
        this.testFile ("athg2sms/nokia.csv", BuiltInFormatName.NokiaSuite, false);
    }

    @Test
    public void lumia () throws URISyntaxException {
        this.testFile ("athg2sms/sms.vmsg", BuiltInFormatName.LumiaVmg, false);
    }

    @Test
    public void lionel () throws URISyntaxException {
        this.testFile ("athg2sms/Msgs5200.csv", BuiltInFormatName.NokiaCsv, false);
    }

    @Test
    public void yetAnotherTest () throws URISyntaxException {
        this.testFile ("athg2sms/oldFile.csv", BuiltInFormatName.NokiaCsv, false);
    }

    @Test
    public void checkingTheDateFormat () throws URISyntaxException {
        this.testString ("sms;submit;\"0000000\";\"\";\"\";\"2010.01.11 16:05\";\"\";\"Bonjour Ca va ?\"\n", BuiltInFormatName.NokiaCsv, false);
        Date d = new Date (messages.get (0).getDate());
        Calendar c = new GregorianCalendar ();
        c.setTime (d);
        int hourOfDay = c.get (Calendar.HOUR_OF_DAY);
        int minutes = c.get (Calendar.MINUTE);
        Assert.assertTrue (hourOfDay == 16);
        Assert.assertTrue (minutes == /*0*/5);
    }

    @Test
    public void checkingTheDateFormat2 () throws URISyntaxException {
        this.testString ("sms;submit;\"0000000\";\"\";\"\";\"2010.01.11 12:04\";\"\";\"C'est l'heure de manger\"\n", BuiltInFormatName.NokiaCsv, false);
        Date d = new Date (messages.get (0).getDate());
        Calendar c = new GregorianCalendar ();
        c.setTime (d);
        int hourOfDay = c.get (Calendar.HOUR_OF_DAY);
        int minutes = c.get (Calendar.MINUTE);
        Assert.assertTrue (hourOfDay == 12);
        Assert.assertTrue (minutes == /*0*/4);
    }

    @Test
    public void unknownSmsFormat () throws URISyntaxException {
        this.testString ("\"+33682864563\",\"2015-07-10 21:53\",\"SMS\",\"0\",\"Bienvenue\"\n", BuiltInFormatName.UnknownSmsFormat1, true);
        Assert.assertEquals (1, this.messagesInserted);
    }

    @Test
    public void nokiaCsv () throws URISyntaxException {
        this.testString ("sms;deliver;\"+33612345678\";\"\";\"\";\"2016.03.22 15:46\";\"\";\"First message\"\n" + "sms;submit;\"\";\"+33612345678\";\"\";\"2016.03.22 15:48\";\"\";\"Answer to the first message\"", BuiltInFormatName.NokiaCsv, true);
        Assert.assertEquals (2, this.messagesInserted);
    }

    @Test
    public void nokiaCsvWithCR () throws URISyntaxException {
        this.testString (
                "\"sms\";\"submit\";\"+498537215678\";\"\";\"\";\"2016.04.14 11:58\";\"\";\"How are you doing?\"\n"
                        + "\"sms\";\"submit\";\"00434566400787\";\"\";\"\";\"2016.04.10 10:43\";\"\";\"Neue Info OS129: Die aktuelle Abflugzeit ist jetzt voraussichtlich 10Apr 11:10. Wir bitten um Entschuldigung.\"",
                BuiltInFormatName.NokiaCsvWithQuotes, false);
        Assert.assertEquals (2, this.messagesInserted);
    }

    @Test
    public void nokiaSuite () throws URISyntaxException {

        //"sms","$(folder)",(?:"",)?"$(address)",(?:"",)?"","$(dateyyyy.MM.dd hh:mm)","","$(body)"
        this.testString (
                "\"sms\",\"READ,RECEIVED\",\"+33654321009\",\"\",\"\",\"2015.04.19 12:23\",\"\",\"Here is a received message\"\n" +
                        "\"sms\",\"SENT\",\"\",\"+33634567811\",\"\",\"2015.04.20 18:49\",\"\",\"Here is a sent message\"\n",
                BuiltInFormatName.NokiaSuite, false);
        Assert.assertEquals (2, this.messagesInserted);
    }

    @Test
    public void loremIpsum () throws URISyntaxException {
        this.testString ("-545061504,Fri Feb 19 03:18:04 EST 2010,Thu Feb 18 16:18:10 EST 2010,false,+61422798642,\"Lorem ipsumRecu\"\n" + "-491825428,Fri Feb 19 07:05:26 EST 2010,Fri Feb 19 07:05:26 EST 2010,true,+61432988391,\"Lorem ipsumSent\"", BuiltInFormatName.BlackberryCsv, false);
        Assert.assertEquals (2, this.messagesInserted);
    }

    @Test
    public void mxtSmsGlobal () throws URISyntaxException {
        this.testString ("Date,Origin,Destination,Message,Status\n" +
                "\"2016-12-22 23:34:54\",61424525904,61405190016,\"Thomas if you see or hear\n" +
                "the cat can you bring him in, I couldn't find him...\",Delivered\n" +
                "Date,Origin,Destination,Message,Status", BuiltInFormatName.MxtSmsglobal, false);
        Assert.assertEquals (1, this.messagesInserted);
    }

    @Test
    public void philippe () throws URISyntaxException {
        this.testString ("sms,\"\",+32478679517,,,1435597166455,,\"Texte du message\"\n", BuiltInFormatName.NokiaCsvWithoutQuotes, false);
        Assert.assertEquals (1, this.messagesInserted);
    }

    @Test
    public void vmg () throws URISyntaxException {
        this.testFile ("athg2sms/test.vmg", BuiltInFormatName.NokiaVmgInbox, false);
        Assert.assertEquals (1, this.messagesInserted);
    }

    @Test
    public void johnPierre() throws URISyntaxException {
        this.testString ("BEGIN:VMSG\n" +
                "VERSION: 1.1\n" +
                "BEGIN:VCARD\n" +
                "TEL:+36707100000\n" +
                "END:VCARD\n" +
                "BEGIN:VBODY\n" +
                "X-BOX:INBOX\n" +
                "X-READ:READ\n" +
                "X-SIMID:0\n" +
                "X-LOCKED:UNLOCKED\n" +
                "X-TYPE:SMS\n" +
                "Date:2016/12/27 18:25:44\n" +
                "Subject;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8:Your WhatsApp code is --- but you can simply tap on this link to verify=\n" +
                " your device:---\n" +
                "END:VBODY\n" +
                "END:VMSG\n" +
                "BEGIN:VMSG\n" +
                "VERSION: 1.1\n" +
                "BEGIN:VCARD\n" +
                "TEL:+17632800000\n" +
                "END:VCARD\n" +
                "BEGIN:VBODY\n" +
                "X-BOX:INBOX\n" +
                "X-READ:READ\n" +
                "X-SIMID:0\n" +
                "X-LOCKED:UNLOCKED\n" +
                "X-TYPE:SMS\n" +
                "Date:2016/12/27 18:23:34\n" +
                "Subject;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8:WhatsApp code --- You can also tap on this link to verify your ph=\n" +
                "one: ---\n" +
                "END:VBODY\n" +
                "END:VMSG\n", BuiltInFormatName.LumiaVmg, false);
        Assert.assertEquals (2, this.messagesInserted);
    }

    @Test
    public void nokia382 () throws URISyntaxException {
        String anotherAttempt = "\"sms\",\"SENT\",\"\",\"+287943978430\",\"\",\"2016.12.13 13:44\",\"\",\"Ref:\n" +
                "4900875984\n" +
                "ako e nevalidna izpolzvai slednata ref: 1380647\"\n" +
                "\"sms\",\"SENT\",\"\",\"+238763287642\",\"\",\"2016.12.13 13:17\",\"\",\"Tofdsdfsfdsn adres:\n" +
                "Paulaner Brauerei GmbH & Co\n" +
                "GPS: N48.176454, E11.433920\n" +
                "Malzereisrasse 31\n" +
                "DE/81249 Langwied\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3788979789\",\"\",\"2016.12.13 11:06\",\"\",\"Kolega, da se\n" +
                "sfggf... Na izlizane ot HR trrgfgfg wvcxwvc jhkjkjghkj i\n" +
                "kato vlezesh v SRB prevish prepratnica\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3788979789\",\"\",\"2016.12.12 20:01\",\"\",\"bvncnbcnv\n" +
                "adres: N39.004925, W(-) 3.358996\"\n" +
                "\"sms\",\"SENT\",\"\",\"07657657576\",\"\",\"2016.12.12 19:45\",\"\",\"tryytty e\n" +
                "za 16.12\"\n" +
                "\"sms\",\"SENT\",\"\",\"+359877315758\",\"\",\"2016.12.12 19:45\",\"\",\"Ok\"\n" +
                "\"sms\",\"SENT\",\"\",\"07657657576\",\"\",\"2016.12.12 19:44\",\"\",\"dfdgqsffsd adres:\n" +
                "wvcxwcvx reryeyryt GmbH\n" +
                "GPS:N42.54655; E45.213573\n" +
                "ghjhhjffjjhf 3G\n" +
                "AT/8755 fjjf iytiu kj lkgjglk\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+359877315758\",\"\",\"\",\"2016.12.12\n" +
                "19:40\",\"\",\"Тръгвам\"\n" +
                "\"sms\",\"SENT\",\"\",\"07657657576\",\"\",\"2016.12.12 19:34\",\"\",\"Tovarish na 13.12\n" +
                "ot 08:00 do 17:00\n" +
                "Ref: 45345\"";
        this.testString (anotherAttempt, BuiltInFormatName.NokiaSuite, false);
        Assert.assertEquals (8, this.messagesInserted);

    }

    private void testFile (final String classpathFile, final BuiltInFormatName conversionSet, final boolean shouldBeEmpty) throws URISyntaxException {
        // Given
        final Converter convertV4 = new Converter();
        final URL url = ReadFileTest.class.getClassLoader ().getResource (classpathFile);
        String content;
        try {
            final File file = url == null ? new File (classpathFile) : new File (url.toURI ());
            content = read(file);
        } catch (final IOException e) {
            throw new RuntimeException (e);
        }

        // When
        convertV4.convertNow(FormatSettings.getInstance().getFormats().get(conversionSet.getValue()),
                content, this.convertListener, null, new ContextHolder<Object>(null),
                null, null, null);

        // then
        if (!shouldBeEmpty) {
            Assert.assertTrue (this.messagesInserted > 0);
        }
    }

    private String read(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));

        for (String line = br.readLine(); line != null; line = br.readLine()) {
            stringBuilder.append(line).append('\n');
        }
        return stringBuilder.toString();
    }

    private void testString (final String content, final BuiltInFormatName conversionSet, final boolean shouldBeEmpty) throws URISyntaxException {
        // Given
        final Converter convertV4 = new Converter();

        // When
        convertV4.convertNow(FormatSettings.getInstance().getFormats().get(conversionSet.getValue()),
                content, this.convertListener, null, new ContextHolder<Object>(null),
                null, null, null);

        // then
        if (!shouldBeEmpty) {
            Assert.assertTrue (this.messagesInserted > 0);
        }
    }

    @Test
    public void regexTest () {
        Assert.assertTrue (java.util.regex.Pattern.compile("[\r\n\t]*((?:[^;])*);((?:[^;])*);((?:[^;])*);\"\";\"((?:[^\"]|\"\")*)\"[\r\n\t]+").matcher (
                "12/26/2015 6:22:03 AM;from;+654631231157;\"\";\"na go SONA kno problem hbe na ar oke phn krba go\"\n" +
                        "12/26/2015 6:17:58 AM;from;+654631231157;\"\";\"blchi j tmr sottie kno problem hbe na to go JAAN? ar sei phn gulo deini go ar ki blche go SONAAA?\"\n" +
                        "12/26/2015 6:14:29 AM;from;+654631231157;\"\";\"thik hai\"\n" +
                        "12/26/2015 6:10:48 AM;from;+654631231157;\"\";\"toainaki ha ha ha ha ha\"\n" +
                        "12/26/2015 5:25:57 AM;from;+654631231157;\"\";\"thikache go JAAN tale mongolbarei jabo go\"\n" +
                        "12/26/2015 4:27:18 AM;from;+654631231157;\"\";\"na go SONATA mongolbare hbe na go karn maa ager dn mane mongolbare fanudr bari jabe go tale?\"\n" +
                        "12/26/2015 4:18:23 AM;from;+654631231157;\"\";\"ami blle nebi na krbe na go jabei tale porer sombare jabe go school theke?\"\n" +
                        "12/26/2015 4:11:36 AM;from;+654631231157;\"\";\"kintu mayer operation krar por ki ar jaua hbe go? karn tkhn to amke sb kaj krte hbe abr jdi fanu ase ta o to jaua late uthie dibe go JAAN ki je kri chhai\"\n" +
                        "12/26/2015 4:01:11 AM;from;+654631231157;\"\";\"tau ktodner mddhe eta blen go\"\n" +
                        "12/").find ());
    }
}
