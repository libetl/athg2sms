package athg2sms;

import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.athg2sms.business.convert.ConvertException;
import org.toilelibre.libe.athg2sms.business.pattern.BuiltInFormatName;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import athg2sms.Athg2SmsJUnitTester.JunitConvertListener;

import static athg2sms.Athg2SmsJUnitTester.importFile;
import static athg2sms.Athg2SmsJUnitTester.importString;

public class ImportTest {


    @Test
    public void csvSms () throws URISyntaxException {
        // "\"Created\",\"Number\",\"Sender Name\",\"Text\",\"Folder\"\n"
        JunitConvertListener convertListener = importString("\"2016-04-19 01:04:34\",\"VM-FCHRGE\",\"\",\"Dear customer, You have made a Debit\",\"INBOX\"\n" + "\"2016-04-19 17:24:11\",\"ID-IDEA\",\"\",\"UR BSNL a/c Topup with Rs. 10 by 2222\",\"INBOX\"\n", BuiltInFormatName.DateAndAddressAndBodyAndINBOX, false);
        Assert.assertEquals (2, convertListener.getMessages().size());
    }

    @Test(expected=ConvertException.class)
    public void empty () throws URISyntaxException {
        importString("", BuiltInFormatName.NokiaCsv, true);
    }

    @Test
    public void indianGuy () throws URISyntaxException {
        importFile("athg2sms/DE.csv", BuiltInFormatName.DateAndFromAndAddressAndbody);
    }

    @Test
    public void aleTxt () throws URISyntaxException {
        importFile("athg2sms/ale.txt", BuiltInFormatName.NokiaCsvWithCommas);
    }

    @Test
    public void otherOtherNokia () throws URISyntaxException {
        importFile("athg2sms/nokia.csv", BuiltInFormatName.NokiaSuite);
    }

    @Test
    public void lumia () throws URISyntaxException {
        importFile("athg2sms/sms.vmsg", BuiltInFormatName.LumiaVmg);
    }

    @Test
    public void lionel () throws URISyntaxException {
        importFile("athg2sms/Msgs5200.csv", BuiltInFormatName.NokiaCsv);
    }

    @Test
    public void yetAnotherTest () throws URISyntaxException {
        importFile("athg2sms/oldFile.csv", BuiltInFormatName.NokiaCsv);
    }

    @Test
    public void checkingTheDateFormat () throws URISyntaxException {
        JunitConvertListener convertListener = importString("sms;submit;\"0000000\";\"\";\"\";\"2010.01.11 16:05\";\"\";\"Bonjour Ca va ?\"\n", BuiltInFormatName.NokiaCsv, false);
        Date d = new Date (convertListener.getMessages().get (0).getDate());
        Calendar c = new GregorianCalendar ();
        c.setTime (d);
        int hourOfDay = c.get (Calendar.HOUR_OF_DAY);
        int minutes = c.get (Calendar.MINUTE);
        Assert.assertTrue (hourOfDay == 16);
        Assert.assertTrue (minutes == /*0*/5);
    }

    @Test
    public void checkingTheDateFormat2 () throws URISyntaxException {
        JunitConvertListener convertListener = importString("sms;submit;\"0000000\";\"\";\"\";\"2010.01.11 12:04\";\"\";\"C'est l'heure de manger\"\n", BuiltInFormatName.NokiaCsv, false);
        Date d = new Date (convertListener.getMessages().get (0).getDate());
        Calendar c = new GregorianCalendar ();
        c.setTime (d);
        int hourOfDay = c.get (Calendar.HOUR_OF_DAY);
        int minutes = c.get (Calendar.MINUTE);
        Assert.assertTrue (hourOfDay == 12);
        Assert.assertTrue (minutes == /*0*/4);
    }

    @Test
    public void unknownSmsFormat () throws URISyntaxException {
        JunitConvertListener convertListener = importString("\"+33682864563\",\"2015-07-10 21:53\",\"SMS\",\"0\",\"Bienvenue\"\n", BuiltInFormatName.UnknownSmsFormat1, true);
        Assert.assertEquals (1, convertListener.getMessages().size());
    }

    @Test
    public void nokiaCsv () throws URISyntaxException {
        JunitConvertListener convertListener = importString("sms;deliver;\"+33612345678\";\"\";\"\";\"2016.03.22 15:46\";\"\";\"First message\"\n" + "sms;submit;\"\";\"+33612345678\";\"\";\"2016.03.22 15:48\";\"\";\"Answer to the first message\"", BuiltInFormatName.NokiaCsv, true);
        Assert.assertEquals (2, convertListener.getMessages().size());
    }

    @Test
    public void nokiaCsvWithCR () throws URISyntaxException {
        JunitConvertListener convertListener = importString(
                "\"sms\";\"submit\";\"+498537215678\";\"\";\"\";\"2016.04.14 11:58\";\"\";\"How are you doing?\"\n"
                        + "\"sms\";\"submit\";\"00434566400787\";\"\";\"\";\"2016.04.10 10:43\";\"\";\"Neue Info OS129: Die aktuelle Abflugzeit ist jetzt voraussichtlich 10Apr 11:10. Wir bitten um Entschuldigung.\"",
                BuiltInFormatName.NokiaCsvWithQuotes);
        Assert.assertEquals (2, convertListener.getMessages().size());
    }

    @Test
    public void nokiaSuite () throws URISyntaxException {

        //"sms","$(folder)",(?:"",)?"$(address)",(?:"",)?"","$(dateyyyy.MM.dd hh:mm)","","$(body)"
        JunitConvertListener convertListener = importString(
                "\"sms\",\"READ,RECEIVED\",\"+33654321009\",\"\",\"\",\"2015.04.19 12:23\",\"\",\"Here is a received message\"\n" +
                        "\"sms\",\"SENT\",\"\",\"+33634567811\",\"\",\"2015.04.20 18:49\",\"\",\"Here is a sent message\"\n",
                BuiltInFormatName.NokiaSuite);
        Assert.assertEquals (2, convertListener.getMessages().size());
    }

    @Test
    public void loremIpsum () throws URISyntaxException {
        JunitConvertListener convertListener = importString("-545061504,Fri Feb 19 03:18:04 EST 2010,Thu Feb 18 16:18:10 EST 2010,false,+61422798642,\"Lorem ipsumRecu\"\n" + "-491825428,Fri Feb 19 07:05:26 EST 2010,Fri Feb 19 07:05:26 EST 2010,true,+61432988391,\"Lorem ipsumSent\"", BuiltInFormatName.BlackberryCsv, false);
        Assert.assertEquals (2, convertListener.getMessages().size());
    }

    @Test
    public void mxtSmsGlobal () throws URISyntaxException {
        JunitConvertListener convertListener = importString("Date,Origin,Destination,Message,Status\n" +
                "\"2016-12-22 23:34:54\",61424525904,61405190016,\"Thomas if you see or hear\n" +
                "the cat can you bring him in, I couldn't find him...\",Delivered\n" +
                "Date,Origin,Destination,Message,Status", BuiltInFormatName.MxtSmsglobal);
        Assert.assertEquals (1, convertListener.getMessages().size());
    }

    @Test
    public void xmlMsgFile () throws URISyntaxException {
        JunitConvertListener convertListener = importFile("athg2sms/xmlfile.msg", BuiltInFormatName.XmlMessage);
        Assert.assertEquals (12328, convertListener.getMessages().size());
    }

    @Test
    public void philippe () throws URISyntaxException {
        JunitConvertListener convertListener = importString("sms,\"\",+32478679517,,,1435597166455,,\"Texte du message\"\n", BuiltInFormatName.NokiaCsvWithoutQuotes, false);
        Assert.assertEquals (1, convertListener.getMessages().size());
    }

    @Test
    public void vmg () throws URISyntaxException {
        JunitConvertListener convertListener = importFile("athg2sms/test.vmg", BuiltInFormatName.NokiaVmgInbox);
        Assert.assertEquals (1, convertListener.getMessages().size());
    }

    @Test
    public void received () throws URISyntaxException {
        JunitConvertListener convertListener = importString("\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAA AAA AAAA AAAAA AAA AAA AA AA AAAA AAAA..... AAA AAAA AA AAAAAAAA.... AAA AAA AA A, A AAAAA AA AA AAAAAA AAAA AAA AA AAAA.... \"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAA AAAAAA AA AAAAAAA AA AAAAAAAA AAAA AAAAA AAA AAAAAAAA......... AAAAA AA AA AAA AAAAAA AA AA AAAA AAAA AAAAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAAAAA AA AA AAAAA AAA AAA .. AA AA AAA AA AAAA AAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AA AAAA AAAAA..... AAA AAA AAAAAA AAA..... AAAAA AAA AAAA AAA AAAAA AA AAAA AAAA\"\n" +
                "\"sms\",\"RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AAAAA AAAA AA AAA AA AAAAAAAA AA AAAAAA A AAA AA \"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAAA AA AA AAA AAAAAA AAAA AAAAA AA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAAA AAAAA AAAA AAAA AA AA AAA AAAAAAAA , AA AA AAAAAA AAAAAA...... AAA AAA AAAAA AA AAAAAA AA AAA AAAA AAAAA AAAAAAA AAA........ AA AA AAAAAA AAA ? AA AAA AAAAA AA AAAAA AA AAA\"\n" +
                "\"sms\",\"RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AA AAAAA AAAA AA AAA AA AA AAAAAA AA AAA AAAA AAAAA AAAAA\"\n" +
                "\"sms\",\"RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AA AA AAA AAA AAAAA AAAA AAA AAAA AA AAAAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAA. AAAA AAAAAA AA AAA AAA ?\"\n" +
                "\"sms\",\"RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AA AA AA AAA AAA AA AAA AAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAAAA AA ... AAA ,,,,,,,,,,,,, AAA AAAA\"\n" +
                "\"sms\",\"RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AAA AAA AA AA AAAA AAAA AAAAA AA ;-(AA AA AA AAA AAAAA AAA AA AA AAAA AA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAAAA AA ... AA AA AA AAAAAAA AAAA AAAA AAAA AA AAA.   AAA AAAA AAAAA AAA AAA AA AAAA AAA.... AA AAAA AAAA AAAA AAAA ............. AAA AAA AAA AAAA AAAA...... AAA AAAA AAAA.... AAAA AAAA.\"\n" +
                "\"sms\",\"RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAAA ..... AAAAAA........ AA AAAA AAAA AA AAAAA AA AAAA.. AAAAA AAA AAAAA AAA AA. \"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AAA AAAAA AAAAA AAAA\"\n" +
                "\"sms\",\"RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AAAA AAA AAAAA AAAAA?\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAA AA AAAAAA AAA.... AAA AAAA AAAA....... AAAAAA AA AAAA AAAA .... AAA AAA AAAAAA AA AAAAAAAAAA AAAA AAAA AAA AAA AA AAAA AA AAAA AAAAAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAA AAA AA AAAAAA AA AAAAA AAAA AAAAA AAAAAAA. AA AAAAA AA AAAA AAA. AAA AAAA AAAAAAA AAAAA AAAA. \"\n" +
                "\"sms\",\"RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AAAA :-) AAAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAA. AAA AAA'A AAAA AAAAA AAA AAAAAAA AAAA, AA AAA AA AAAAAAA\"\n" +
                "\"sms\",\"RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AAAAA AA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAA AA AAAA AA AA AAAAAAA . AA AAAAAA.. \"\n" +
                "\"sms\",\"RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AA AAAAA AAAA AAA AA AAAAA AAA AAAA AAAA AA AAAAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAA AAA A AAAAA AAAA... AA AAAA AA AAAAAAA AAAA. AAAA AAAA AAAA\"\n" +
                "\"sms\",\"RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AA AA AAA AA AAAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAAAAA AAA ....... AAAAA AA AAAA .AAAAAA AAAA AA AAAAAAAAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAA AAA AAAAAAAA AAAAAAA AAAA\"\n" +
                "\"sms\",\"RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"A AAA AA AAAA AAAAA AA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AA AAA AAAA AAAA AAA. AAA AAAA AAAA AAA AAAA AAAA AA AA AAAA AAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AA. AAAA AAA . AAA AAAA AAAA AAAA AAAAA AA\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AAAAA AAA AAA AAAAAA AA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAAAAAA AAAA AA AA AAA AA AAA AAAA AA AAAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAA A AAAA AAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAAAAA !!! AAA AAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAA AAAAA AA AAAA AA AAAA AAA AAAA.   AAA AAA AAAA AA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AA ..... AAAAAA AAAAAAA AAAAA.\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AAA AAAAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAA AAAAAA .... AAAA AAAAA AA AAAA AA.... \"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AAA AAA \"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAAAAA AAAA AAAAA AAA , AAAAA AA AA AAAAAA\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAAAA AAAAA AAA AAAA AA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAAA AAAAAA... AA AAAAA AAAAA AAA\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AAA?\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAAAAAA\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+3312345678\",\"\",\"\",\"2017.01.01 15:23\",\"\",\"AAAA?\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AA AAA AAAA AAAAA AAAA\"\n" +
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAAA ?\"\n", BuiltInFormatName.NokiaSuite);
        Assert.assertEquals (50, convertListener.getMessages().size());
    }

    @Test
    public void johnPierre() throws URISyntaxException {
        JunitConvertListener convertListener = importString("BEGIN:VMSG\n" +
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
                "END:VMSG\n", BuiltInFormatName.LumiaVmg);
        Assert.assertEquals (2, convertListener.getMessages().size());
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
        JunitConvertListener convertListener = importString(anotherAttempt, BuiltInFormatName.NokiaSuite);
        Assert.assertEquals (8, convertListener.getMessages().size());

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
