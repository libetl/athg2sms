package athg2sms;

import athg2sms.Athg2SmsJUnitTester.JunitConvertListener;
import org.junit.Assert;
import org.junit.Test;
import org.toilelibre.libe.athg2sms.actions.Actions;
import org.toilelibre.libe.athg2sms.business.convert.ConvertException;
import org.toilelibre.libe.athg2sms.business.pattern.BuiltInFormat;
import org.toilelibre.libe.athg2sms.business.pattern.Format;
import org.toilelibre.libe.athg2sms.business.pattern.FormatSettings;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static athg2sms.Athg2SmsJUnitTester.importFile;
import static athg2sms.Athg2SmsJUnitTester.importString;

public class ImportTest {


    @Test
    public void csvSms () throws URISyntaxException {
        // "\"Created\",\"Number\",\"Sender Name\",\"Text\",\"Folder\"\n"
        JunitConvertListener convertListener = importString("\"2016-04-19 01:04:34\",\"VM-FCHRGE\",\"\",\"Dear customer, You have made a Debit\",\"INBOX\"\n" + "\"2016-04-19 17:24:11\",\"ID-IDEA\",\"\",\"UR BSNL a/c Topup with Rs. 10 by 2222\",\"INBOX\"\n", BuiltInFormat.DateAndAddressAndBodyAndINBOX, false);
        Assert.assertEquals (2, convertListener.getMessages().size());
    }

    @Test(expected=ConvertException.class)
    public void empty () throws URISyntaxException {
        importString("", BuiltInFormat.NokiaCsv, true);
    }

    @Test
    public void redmi3sPrime () throws URISyntaxException {
        importString("sms,deliver,\"address\",\"\",\"\",\"2017.04.20 19:14\",\"\",\"sms_body\"", BuiltInFormat.NokiaCsvWithCommas, false);
    }

    @Test
    public void indianGuy () throws URISyntaxException {
        importFile("athg2sms/DE.csv", BuiltInFormat.DateAndFromAndAddressAndbody);
    }

    @Test
    public void aleTxt () throws URISyntaxException {
        importFile("athg2sms/ale.txt", BuiltInFormat.NokiaCsvWithCommas);
    }

    @Test
    public void otherOtherNokia () throws URISyntaxException {
        importFile("athg2sms/nokia.csv", BuiltInFormat.NokiaSuite);
    }

    @Test
    public void lumia () throws URISyntaxException {
        importFile("athg2sms/sms.vmsg", BuiltInFormat.LumiaVmg);
    }

    @Test
    public void lionel () throws URISyntaxException {
        importFile("athg2sms/Msgs5200.csv", BuiltInFormat.NokiaCsv);
    }

    @Test
    public void yetAnotherTest () throws URISyntaxException {
        importFile("athg2sms/oldFile.csv", BuiltInFormat.NokiaCsv);
    }

    @Test
    public void checkingTheDateFormat () throws URISyntaxException {
        JunitConvertListener convertListener = importString("sms;submit;\"0000000\";\"\";\"\";\"2010.01.11 16:05\";\"\";\"Bonjour Ca va ?\"\n", BuiltInFormat.NokiaCsv, false);
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
        JunitConvertListener convertListener = importString("sms;submit;\"0000000\";\"\";\"\";\"2010.01.11 12:04\";\"\";\"C'est l'heure de manger\"\n", BuiltInFormat.NokiaCsv, false);
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
        JunitConvertListener convertListener = importString("\"+33682864563\",\"2015-07-10 21:53\",\"SMS\",\"0\",\"Bienvenue\"\n", BuiltInFormat.UnknownSmsFormat1, true);
        Assert.assertEquals (1, convertListener.getMessages().size());
    }

    @Test
    public void nokiaCsv () throws URISyntaxException {
        JunitConvertListener convertListener = importString("sms;deliver;\"+33612345678\";\"\";\"\";\"2016.03.22 15:46\";\"\";\"First message\"\n" + "sms;submit;\"\";\"+33612345678\";\"\";\"2016.03.22 15:48\";\"\";\"Answer to the first message\"", BuiltInFormat.NokiaCsv, true);
        Assert.assertEquals (2, convertListener.getMessages().size());
    }

    @Test
    public void nokiaCsvWithCR () throws URISyntaxException {
        JunitConvertListener convertListener = importString(
                "\"sms\";\"submit\";\"+498537215678\";\"\";\"\";\"2016.04.14 11:58\";\"\";\"How are you doing?\"\n"
                        + "\"sms\";\"submit\";\"00434566400787\";\"\";\"\";\"2016.04.10 10:43\";\"\";\"Neue Info OS129: Die aktuelle Abflugzeit ist jetzt voraussichtlich 10Apr 11:10. Wir bitten um Entschuldigung.\"",
                BuiltInFormat.NokiaCsvWithQuotes);
        Assert.assertEquals (2, convertListener.getMessages().size());
    }

    @Test
    public void nokiaSuite () throws URISyntaxException {

        //"sms","$(folder)",(?:"",)?"$(address)",(?:"",)?"","$(dateyyyy.MM.dd hh:mm)","","$(body)"
        JunitConvertListener convertListener = importString(
                "\"sms\",\"READ,RECEIVED\",\"+33654321009\",\"\",\"\",\"2015.04.19 12:23\",\"\",\"Here is a received message\"\n" +
                        "\"sms\",\"SENT\",\"\",\"+33634567811\",\"\",\"2015.04.20 18:49\",\"\",\"Here is a sent message\"\n",
                BuiltInFormat.NokiaSuite);
        Assert.assertEquals (2, convertListener.getMessages().size());
    }

    @Test
    public void loremIpsum () throws URISyntaxException {
        JunitConvertListener convertListener = importString("-545061504,Fri Feb 19 03:18:04 EST 2010,Thu Feb 18 16:18:10 EST 2010,false,+61422798642,\"Lorem ipsumRecu\"\n" + "-491825428,Fri Feb 19 07:05:26 EST 2010,Fri Feb 19 07:05:26 EST 2010,true,+61432988391,\"Lorem ipsumSent\"", BuiltInFormat.BlackberryCsv, false);
        Assert.assertEquals (2, convertListener.getMessages().size());
    }

    @Test
    public void victorTest () throws URISyntaxException {
        JunitConvertListener convertListener = importString("number,address,content,date,send|receive,person,,\n" +
                "14,2.34807E+12,8132581738,16/10/2014 12:01,other,2.34807E+12,,This is a draft message\n" +
                "17,8091924903,08037598576 - Dayo,23/04/2014 16:43,other,8091924903,,This is a draft message\n" +
                "29,2.34807E+12,How far...abeg hola wen done. Cos right nw ur boi done almst dey stranded. Tnx,18/05/2014 17:56,receive,413,,This is a received message\n" +
                "30,2.34807E+12,Aight.,18/09/2014 14:42,receive,413,,This is a sent message\n" +
                "31,8065089071,Call you later,18/09/2014 10:45,send,me,,\n", BuiltInFormat.WeirdVictorFormat);
        Assert.assertEquals (5, convertListener.getMessages().size());
    }

    @Test
    public void mxtSmsGlobal () throws URISyntaxException {
        JunitConvertListener convertListener = importString("Date,Origin,Destination,Message,Status\n" +
                "\"2016-12-22 23:34:54\",61424525904,61405190016,\"Thomas if you see or hear\n" +
                "the cat can you bring him in, I couldn't find him...\",Delivered\n" +
                "Date,Origin,Destination,Message,Status", BuiltInFormat.MxtSmsglobal);
        Assert.assertEquals (1, convertListener.getMessages().size());
    }

    @Test
    public void xmlMsgFile () throws URISyntaxException {
        JunitConvertListener convertListener = importFile("athg2sms/xmlfile.msg", BuiltInFormat.XmlMessage);
        Assert.assertEquals (12328, convertListener.getMessages().size());
    }

    @Test
    public void philippe () throws URISyntaxException {
        JunitConvertListener convertListener = importString("sms,\"\",+32478679517,,,1435597166455,,\"Texte du message\"\n", BuiltInFormat.NokiaCsvWithoutQuotes, false);
        Assert.assertEquals (1, convertListener.getMessages().size());
    }

    @Test
    public void vmg () throws URISyntaxException {
        JunitConvertListener convertListener = importFile("athg2sms/test.vmg", BuiltInFormat.NokiaVmgInbox);
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
                "\"sms\",\"SENT\",\"\",\"+3312345678\",\"\",\"2017.01.01 15:23\",\"\",\"AAAA ?\"\n", BuiltInFormat.NokiaSuite);
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
                "END:VMSG\n", BuiltInFormat.LumiaVmg);
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
        JunitConvertListener convertListener = importString(anotherAttempt, BuiltInFormat.NokiaSuite);
        Assert.assertEquals (8, convertListener.getMessages().size());

    }

    @Test
    public void regexTest () {
        Assert.assertTrue (java.util.regex.Pattern.compile("[\r\n\t]*((?:[^;])*);((?:[^;])*);((?:[^;])*);\"\";\"((?:[^\"]|\"\")*)\"[\r\n\t]+").matcher (
                "12/26/2015 6:22:03 AM;from;+230983208932;\"\";\"na go SONA kno problem hbe na ar oke phn krba go\"\n" +
                        "12/26/2015 6:17:58 AM;from;+230983208932;\"\";\"blchi j tmr sottie kno problem hbe na to go JAAN? ar sei phn gulo deini go ar ki blche go SONAAA?\"\n" +
                        "12/26/2015 6:14:29 AM;from;+230983208932;\"\";\"thik hai\"\n" +
                        "12/26/2015 6:10:48 AM;from;+230983208932;\"\";\"toainaki ha ha ha ha ha\"\n" +
                        "12/26/2015 5:25:57 AM;from;+230983208932;\"\";\"thikache go JAAN tale mongolbarei jabo go\"\n" +
                        "12/26/2015 4:27:18 AM;from;+230983208932;\"\";\"na go SONATA mongolbare hbe na go karn maa ager dn mane mongolbare fanudr bari jabe go tale?\"\n" +
                        "12/26/2015 4:18:23 AM;from;+230983208932;\"\";\"ami blle nebi na krbe na go jabei tale porer sombare jabe go school theke?\"\n" +
                        "12/26/2015 4:11:36 AM;from;+230983208932;\"\";\"kintu mayer operation krar por ki ar jaua hbe go? karn tkhn to amke sb kaj krte hbe abr jdi fanu ase ta o to jaua late uthie dibe go JAAN ki je kri chhai\"\n" +
                        "12/26/2015 4:01:11 AM;from;+230983208932;\"\";\"tau ktodner mddhe eta blen go\"\n" +
                        "12/").find ());
    }

    @Test
    public void anotherXmlMessage () throws URISyntaxException {
        JunitConvertListener convertListener = importString("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<ArrayOfMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>Hi! Check out our timesaving tips and videos for getting the most from your new phone.\n" +
                "\n" +
                "windowsphone.com/hello\n" +
                "\n" +
                "This is a free message.</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>130352170807645806</LocalTimestamp>\n" +
                "<Sender>Windows Phone</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>Mau berbagi Pulsa ke Sesama Pengguna Telkomsel? Ketik *858*NomorTujuan*NominalTransfer# lalu tunggu SMS konfirmasinya.</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131308493967196623</LocalTimestamp>\n" +
                "<Sender>TELKOMSEL</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>Buy 1 Get 1 Teh Tareek Semenanjung (Hot/Ice).Redeem this SMS today at Kopi Oey Sabang Jl.H.Agus Salim No.16A Sabang,Telp (021)31934438.Limited stock.Promo *606#</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131308214773816168</LocalTimestamp>\n" +
                "<Sender>Kopi Oey</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients>\n" +
                "<string>+6208765216644</string>\n" +
                "</Recepients>\n" +
                "<Body>sdh dibayarkah oleh paung? kpn dibayarnya? bgm sy bs ksh uangnya ke pak santo?</Body>\n" +
                "<IsIncoming>false</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131249601674812662</LocalTimestamp>\n" +
                "<Sender />\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>The message you sent to 6208765216644 was delivered.</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131249601803623809</LocalTimestamp>\n" +
                "<Sender>Mobile operator</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>returan diambil ya besok</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131280670830475069</LocalTimestamp>\n" +
                "<Sender>+6208765216644</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>The message you sent to 6208765216644 was delivered.</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>false</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131265992559883530</LocalTimestamp>\n" +
                "<Sender>Mobile operator</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients>\n" +
                "<string>+6208765216644</string>\n" +
                "</Recepients>\n" +
                "<Body>ketarik sisa 44 buku yg ada, sy lgs bayar tunai...</Body>\n" +
                "<IsIncoming>false</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131265992489069086</LocalTimestamp>\n" +
                "<Sender />\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>kapan ampo bro</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131265991431285223</LocalTimestamp>\n" +
                "<Sender>+6208765216644</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>dihubungi saja bos saya</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131249602209415890</LocalTimestamp>\n" +
                "<Sender>+230983208932</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>The message you sent to 6208765216644 was delivered.</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131249601803623809</LocalTimestamp>\n" +
                "<Sender>Mobile operator</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients>\n" +
                "<string>+230983208932</string>\n" +
                "</Recepients>\n" +
                "<Body>sdh dibayarkah oleh paung? kpn dibayarnya? bgm sy bs ksh uangnya ke pak santo?</Body>\n" +
                "<IsIncoming>false</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131249601674812662</LocalTimestamp>\n" +
                "<Sender />\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>The message you sent to 230983208932 was delivered.</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131237593783283181</LocalTimestamp>\n" +
                "<Sender>Mobile operator</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients>\n" +
                "<string>+6208765216644</string>\n" +
                "</Recepients>\n" +
                "<Body>pak santo, sy mau menarik buku 'direktori perguruan tinggi' &amp; dana hasil penjualan buku minggu ini. kpn sy bs ambil. terima kasih</Body>\n" +
                "<IsIncoming>false</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131237593670674338</LocalTimestamp>\n" +
                "<Sender />\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>The message you sent to 6208765216644 was delivered.</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131183865918594256</LocalTimestamp>\n" +
                "<Sender>Mobile operator</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients>\n" +
                "<string>+230983208932</string>\n" +
                "</Recepients>\n" +
                "<Body>pak santo, sy mau menarik dana hasil penjualan buku saya minggu ini, berapa pun jumlahnya. terima kasih</Body>\n" +
                "<IsIncoming>false</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131183865807130000</LocalTimestamp>\n" +
                "<Sender />\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>habis minggu depan</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131158028228770000</LocalTimestamp>\n" +
                "<Sender>+230983208932</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>The message you sent to 6208765216644 was delivered.</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131158025317700994</LocalTimestamp>\n" +
                "<Sender>Mobile operator</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients>\n" +
                "<string>+230983208932</string>\n" +
                "</Recepients>\n" +
                "<Body>sdh 4 bulan buku saya ada di paung. bgm kabar penjualan buku saya? terima kasih sebelumnya.</Body>\n" +
                "<IsIncoming>false</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131158025211430000</LocalTimestamp>\n" +
                "<Sender />\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>mas belum</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131133875315000000</LocalTimestamp>\n" +
                "<Sender>+6208765216644</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>The message you sent to 6208765216644 was delivered.</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131133865136937766</LocalTimestamp>\n" +
                "<Sender>Mobile operator</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients>\n" +
                "<string>+6208765216644</string>\n" +
                "</Recepients>\n" +
                "<Body>pak santo, ini sando. saya mau tanya ttg laporan penjualan buku saya. terima kasih</Body>\n" +
                "<IsIncoming>false</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131133865062030000</LocalTimestamp>\n" +
                "<Sender />\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>The message you sent to 6208765216644 was delivered.</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131103593146768339</LocalTimestamp>\n" +
                "<Sender>Mobile operator</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients>\n" +
                "<string>+6208765216644</string>\n" +
                "</Recepients>\n" +
                "<Body>kalau bukunya laku semua n cpt dibayarin oleh paung, saya pasti lebihin, apalagi kalau dibayar sblm lebaran. tq sebelumnya</Body>\n" +
                "<IsIncoming>false</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131103593034800000</LocalTimestamp>\n" +
                "<Sender />\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>mas tambahin seratus ribu buat oleh oleh bisakan mas</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131103423205930000</LocalTimestamp>\n" +
                "<Sender>+6208765216644</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>mas 250 ribu ya untuk gunung agung biaya barcod ya</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131102740066570000</LocalTimestamp>\n" +
                "<Sender>+6208765216644</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>The message you sent to 6208765216644 was delivered.</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131066626584797592</LocalTimestamp>\n" +
                "<Sender>Mobile operator</Sender>\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients>\n" +
                "<string>+6208765216644</string>\n" +
                "</Recepients>\n" +
                "<Body>bsk sktr jam 10 ya pak. terima kasih.</Body>\n" +
                "<IsIncoming>false</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131066580763100000</LocalTimestamp>\n" +
                "<Sender />\n" +
                "</Message>\n" +
                "<Message>\n" +
                "<Recepients />\n" +
                "<Body>mas dikteori perguruan tinggi: 100 exp</Body>\n" +
                "<IsIncoming>true</IsIncoming>\n" +
                "<IsRead>true</IsRead>\n" +
                "<Attachments />\n" +
                "<LocalTimestamp>131066523469030000</LocalTimestamp>\n" +
                "<Sender>+6208765216644</Sender>\n" +
                "</Message>", BuiltInFormat.XmlMessage);

        Assert.assertEquals (27, convertListener.getMessages().size());
    }


    @Test
    public void localTimestampIssue () throws URISyntaxException {
        JunitConvertListener convertListener = importString(
                "<ArrayOfMessage>" +
                        " <Message>" +
                        "  <Recepients>" +
                        "   <string>+33685280000</string>" +
                        "  </Recepients>" +
                        "  <Body>Ah cool! Au fait les crevettes vont très bien !</Body>" +
                        "  <IsIncoming>false</IsIncoming>" +
                        "  <IsRead>true</IsRead>" +
                        "  <Attachments/>" +
                        "  <LocalTimestamp>131450419779746184</LocalTimestamp>" +
                        "  <Sender/>" +
                        " </Message>" +
                        "</ArrayOfMessage>", BuiltInFormat.XmlMessage);
        Assert.assertEquals(1, convertListener.getMessages().size());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(convertListener.getMessages().get(0).getDate());
        Assert.assertTrue(calendar.get(GregorianCalendar.YEAR) == 2017);
    }

    @Test
    public void nokiaDecember2017 () throws URISyntaxException {
        JunitConvertListener convertListener = importString(
                "2017-11-11 18:49:00,DM-SBIUPI,DM-SBIUPI,Received,\"Your One time password is 586099 for Setting MPIN to your account, It is valid for next 10 mins, Do not disclose OTP to anyone, State Bank of India.\",1708,null,1.51036E+12,0,0,1,-1,1,0,null,9.19768E+11,0,4,0,com.sonyericsson.conversations,0,-1,null,null,null,null,1.51036E+12,0,1.51036E+12,1\n" +
                        "2017-11-11 18:49:00,BZ-SBIUPI,BZ-SBIUPI,Received,\"Your One time password is 003725 for Setting MPIN to your account, It is valid for next 10 mins, Do not disclose OTP to anyone, State Bank of India.\",1707,null,1.51036E+12,0,0,1,-1,1,0,null,9.19423E+11,0,4,0,com.sonyericsson.conversations,0,-1,null,null,null,null,1.51036E+12,0,1.51036E+12,1\n" +
                        "2017-11-11 18:49:00,9740797407,9740797407,Sent,\"UPI sft5dtp76kmsv6scxibdvlfr9tcyftudl71so35osfm=\",1706,null,1.51036E+12,0,null,1,0,2,null,null,null,0,4,0,net.one97.paytm,1,-1,null,null,null,null,1.51036E+12,0,null,1\n" +
                        "2017-11-11 18:49:00,BH-CDSLTX,BH-CDSLTX,Received,\"CDSL: Debit in a/c *13126071 for 1000-VIJI FINANCE-EQTY on 10NOV\",1705,null,1.51032E+12,0,0,1,-1,1,0,null,9.19418E+11,0,4,0,com.sonyericsson.conversations,0,-1,null,null,null,null,1.51032E+12,0,1.51032E+12,1",
                BuiltInFormat.SonyEricsson);
        Assert.assertEquals (4, convertListener.getMessages().size());
    }

    @Test
    public void vmgBis() throws URISyntaxException, FileNotFoundException {
        String text = "BEGIN:VMSG\n" +
                "VERSION:1.1\n" +
                "X-IRMC-STATUS:\n" +
                "X-IRMC-BOX:INBOX\n" +
                "X-NOK-DT:20091022T064825Z\n" +
                "X-MESSAGE-TYPE:DELIVER\n" +
                "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "N:\n" +
                "TEL:+990123456789\n" +
                "END:VCARD\n" +
                "BEGIN:VENV\n" +
                "BEGIN:VBODY\n" +
                "Date:22.10.2007 06:48:25\n" +
                "<Text Body 0-140+ chars>\n" +
                "END:VBODY\n" +
                "END:VENV\n" +
                "END:VMSG\n" +
                "BEGIN:VMSG\n" +
                "\n" +
                "VERSION:1.1\n" +
                "\n" +
                "X-IRMC-STATUS:\n" +
                "READ\n" +
                "X-IRMC-BOX:INBOX\n" +
                "\n" +
                "X-NOK-DT:20101006T015058Z\n" +
                "\n" +
                "X-MESSAGE-TYPE:DELIVER\n" +
                "BEGIN:VCARD\n" +
                "\n" +
                "VERSION:3.0\n" +
                "\n" +
                "N:\n" +
                "\n" +
                "TEL:+990123456789\n" +
                "\n" +
                "END:VCARD\n" +
                "\n" +
                "BEGIN:VENV\n" +
                "\n" +
                "BEGIN:VBODY\n" +
                "\n" +
                "Date:06.10.2009 01:50:58\n" +
                "\n" +
                "<Text Body 140+ chars>\n" +
                "\n" +
                "END:VBODY\n" +
                "\n" +
                "END:VENV\n" +
                "\n" +
                "END:VMSG";

        String patternName = new Actions().guessNow(text);
        Format format = FormatSettings.getInstance().getFormats().get(patternName);
        JunitConvertListener convertListener = importString(text, BuiltInFormat.Vmg2018);
        Assert.assertEquals (2, convertListener.getMessages().size());
    }

    @Test
    public void guessThenImport () throws FileNotFoundException, URISyntaxException {
        String text = "sms,deliver,\"XX\",\"\";\"\";\"2017.04.21 23:48\",\"\",\"Ierland geloof ik\"\n" +
                "sms,deliver,\"XX\",\"\";\"\";\"2017.04.21 23:48\",\"\",\"Ierland geloof ik\"\n" +
                "sms,deliver,\"XX\",\"\";\"\";\"2017.04.21 23:46\",\"\",\"Krijg m volgende week pas\"\n" +
                "sms,deliver,\"XX\",\"\";\"\";\"2017.04.21 23:46\",\"\",\"Krijg m volgende week pas\"\n" +
                "sms,deliver,\"XX\",\"\";\"\";\"2017.04.20 17:45\",\"\",\"Doet je whatsapp het nog steeds niet?\"\n" +
                "sms,deliver,\"XX\",\"\";\"\";\"2017.04.20 17:45\",\"\",\"Doet je whatsapp het nog steeds niet?\"\n" +
                "sms,deliver,\"XX\",\"\";\"\";\"2017.04.20 14:22\",\"\",\"Zal vanavond zoeken.\"\n" +
                "sms,deliver,\"XX\",\"\";\"\";\"2017.04.20 14:22\",\"\",\"Zal vanavond zoeken.\"\n" +
                "sms;\"submit\";\"\";\"XX\";\"\";\"2017.05.18 12:56\";\"\";\"Jammer\"\n" +
                "sms;\"submit\";\"\";\"XX\";\"\";\"2017.05.18 12:56\";\"\";\"Jammer\"\n";

        JunitConvertListener convertListener = importString(text, BuiltInFormat.MixedNokiaCsv);
        Assert.assertEquals (6, convertListener.getMessages().size());
    }

    @Test
    public void testGreekNokia() throws URISyntaxException, FileNotFoundException {
        String text = "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2017.12.04 12:06\",\"\",\"Τζένη καλημέρα,  η Παυλίνα είμαι.Ελπίζω να είστε όλοι καλά. Όταν μπορέσεις πάρε με.(Δεν ηξερα αν σχόλασες γι'αυτο πήρα).Βρήκα λογοθεραπευτρια.Φιλιά!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.11.29 00:05\",\"\",\"AΓAΠH MOY TI KANEIΣ; EΓΩ TΩPA ΠAΩ ΓIA YΠNO. M' EXEIΣ KANEI NYXTOΠOYΛI ΣAN EΣENA;-) MOY ΛEIΠOYN ΠOΛY TA MHNYMATA ΠOY ΣTEΛNAME O ENAΣ ΣTON AΛΛO ΠAΛIA. ΓI'AYTO OTAN EXEIΣ YΠHPEΣIA, XAMOΓEΛAΩ OTAN XTYΠAEI O HXOΣ OTI EXΩ MHNYMA ΓIATI ΞEPΩ OTI EIΣAI EΣY. Σ'AΓAΠAΩ KAI M'EXEIΣ TPEΛANEI ME TA ΦIΛIA ΣOY AYTEΣ TIΣ MEPEΣ...\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2017.11.28 23:06\",\"\",\"MΩPAKI MOY ΔEN ΞEPΩ ΠΩΣ ΘA ΠEPAΣOYN AΛΛEΣ 4 EBΔOMAΔEΣ XΩPIΣ NA MΠOPΩ NA ΣE AΓΓIΞΩ KAI NA ΣE XAIΔEΨΩ OΠΩΣ AKPIBΩΣ ΘEΛΩ.NOMIZΩ OTI EINAI ΠOΛY MAKPIA AKOMA.KAΘE XPONOΣ ΠOY ΠEPNAEI NIΩΘΩ OTI EXΩ ΛIΓOTEPH YΠOMONH AYTHN THN ΠEPIOΔO.Σ'AΓAΠΩ!ΣE ΘEΛΩ.ΦIΛAKIA!\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2017.11.09 06:43\",\"\",\"230983208932\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.11.03 18:08\",\"\",\"MΩPO ΘA ΠAΣ NA ΠAPEIΣ TON ΔHMHTPH; O ΠAΠA ΘANAΣHΣ ΔEN EINAI ΣHMEPA\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2017.10.31 13:48\",\"\",\"EIXATE 2 KΛHΣEIΣ:\n" +
                "+230983208932\n" +
                "(2) 31/10 14:46\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2017.10.27 20:17\",\"\",\"EIXATE 1 KΛHΣH:\n" +
                "+230983208932\n" +
                "(1) 27/10 20:17\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2017.10.18 00:14\",\"\",\"MΩPO MOY ΘEΛΩ NA ΣOY ΠΩ NA EIΣAI XAΛAPH KAI NA MHN AΓXΩNEΣAI ME OΣA EXEIΣ NA KANEIΣ.BEBAIA ME OΛH AYTH THN ENTAΣH ΠOY EXEIΣ ME KAΠOIO TPOΠO ME KANEI NA ΘEΛΩ NA ΓINOMAΣTE ENA ΣYNEXEIA.HPEMHΣE KAI ΣKEΨOY OTI ΦETOΣ ΔEN TAΛAIΠΩPEIΣAI ΣTOYΣ ΔPOMOYΣ KAI ΠPAΓMATIKA AYTO ME EXEI ANAKOYΦIΣEI.Σ'AΓAΠΩ!ΦIΛAKIA! \"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.10.01 00:24\",\"\",\"AΓAΠOYΛA ΠΩΣ ΠAEI H ΔOYΛEIA; APXIΣEΣ NA NYΣTAZEIΣ MHΠΩΣ; HΘEΛA NA ΣOY ΠΩ OTI ΣE ΘAYMAZΩ ΠOΛY ΓIA TIΣ ΔYNATOTHTEΣ ΣOY, ΓIATI EINAI ΠOΛYΠΛEYPEΣ KAI OTI Σ'EYXAPIΣTΩ ΓIATI ME BOHΘAΣ ΠANTA ΣE O,TI XPEIAΣTΩ. Σ' AΓAΠΩ, Σ'AΓAΠΩ, Σ'AΓAΠΩ KAI TΩPA ΠAΩ ΓIA NANI... :-*\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.09.11 23:03\",\"\",\"AΘHNA ΣOPPY ΓIA THN ENOXΛHΣH, TΩPA BΛEΠΩ TA mail. HPΘE ENA KAI ΛEEI NA ΣTEIΛOYME ΣTH ΔIEYΘYNΣH THN ΠPΩTOTYΠH ANAΦOPA ANAΛHΨHΣ YΠHPEΣIAΣ OΛOI OI NEOI ΠPOIΣTAMENOI. TH ΔIKH MOY THN EIXEΣ ΠAEI THN ΠAPAΣKEYH; \"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.08.27 12:12\",\"\",\"EΛA KOPITΣI ΦATE EΣEIΣ  KAI EMEIΣ ΘA EPΘOYME AΠO EKEI TO AΠOΓEYMATAKI ΓIA MΠANIO. ΦIΛAKIA!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.08.11 17:33\",\"\",\"EΛA KOPITΣI ΓΛYKO TI KANEIΣ; ΔEN ΘEΛΩ NA ΠAPΩ THΛ NA MHN ENOXΛHΣΩ. TA ΠAIΔIA KAΛA; ΦIΛAKIA ΠOΛΛA Σ'OΛOYΣ, AN BOΛEΨEI ΘA XAPOYME NA ΣAΣ ΔOYME AΠO KONTA\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2017.06.29 01:03\",\"\",\"8 XPONIA AΠO THN HMEPA ΠOYΓINAM ENA,8 XPONIA ΠOY ΣE ZΩ KAΘE MEPA KAI EIMAI EYΓNΩMΩN ΓIA AYTO,8 XPONIA ΠOY ANEXEΣAI TO EΓΩ MOY KAI KAΠOIEΣ ΦOPEΣ THN MOYPMOYPA MOY,8 XPONIA TPIA YΠEPOXA ΠΛAΣMATA,  8 XPONIA ΓEMATA ΠAΘOΣ   \"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.06.24 17:39\",\"\",\"EΛA KOPITΣI TI KANEIΣ; OΠOTE ΘEΛEIΣ KAI EXEIΣ XPONO ΠAPE ME THΛ NA ΣYNENNOHΘOYME. ΦIΛAKIA!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"+230983208932\",\"\",\"2017.06.23 22:17\",\"\",\"EΛA EΛENA ΔEN ΘA EPΘEI O XPIΣTOΦOPOΣ; EIMAI ΣTON ΠAΠAΘANAΣH KAI EXEI ΠOΛY KOΣMO AKOMA. EXEIΣ KANENA ΓIAOYPTI NA KATEBAΣEIΣ ΣTON ΘOΔΩPH ΓIATI ΘEΛΩ NA TO ΠAPΩ ME THN ANTIBIΩΣH. ΦIΛAKIA!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.06.23 22:06\",\"\",\"TEO ΦAE TO MEΣHMEPIANO EΣY ΓIATI MAΛΛON ΘA APΓHΣΩ. TA ΦAΣOΛAKIA EINAI TOY MΠAMΠA ΣOY\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.06.22 23:51\",\"\",\"KAI EΣY MAΣ EΛEIΨEΣ KAPΔOYΛA MOY! EΛΠIZΩ NA ΠEPAΣEΣ OMOPΦA, NA ΠPOΣEYXHΘHKEΣ ΓIA THN OIKOΓENEIA MAΣ KAI ΓIA OΛOYΣ, KAI NA ΓEMIΣEΣ TIΣ MΠATAPIEΣ ΣOY! ΦIΛAKIA, KAΛO BPAΔY!\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2017.06.22 23:37\",\"\",\"EΛA MΩPAKI MOY EΛΠIZΩ NA XAΛAPΩNEIΣ KI EΣY TΩPA.MOY EXETE ΛEIΨEI ΠAPA ΠOΛY OΛOI KAI ANYΠOMONΩ NA ΣAΣ ΔΩ AYPIO.Σ'AΓAΠΩ!ΦIΛAKIA!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.06.15 19:06\",\"\",\"KOPITΣI TI KANEIΣ; ΞEPEIΣ ΠOΣEΣ MEPEΣ ΘEΛΩ NA ΣE ΠAPΩ THΛ; EIMAI ΘEΣNIKH, MOΛIΣ ΠAΩ ΣΠITI, ΘA ΣE ΠAPΩ. ΦIΛAKIA!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.06.06 22:29\",\"\",\"EΛA KOPITΣI TI KANEIΣ; EYXOMAI AΠO KAPΔIAΣ KAΛH APXH AYPIO ΓIA TH XPYΣH KAI TON ANHΨIO ΣOY. NA ΠANE OΛA KAΛA KAI EIΘE O ΘEOΣ NA ANTAMEIΨEI OΛOYΣ TOYΣ KOΠOYΣ TOYΣ. ΦIΛAKIA ΠOΛΛA!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.06.01 12:22\",\"\",\"EΛA EXΩ TEΛEIΩΣEI. OΠOTE MΠOPEΣEIΣ ΠEΣ MOY. ΦIΛAKIA!\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2017.06.01 00:09\",\"\",\"MΩPO MOY MOΛIΣ ΞYPIΣTHKA KIOΛAΣ.MOY APEΣE ΠOΛY ΠOY ME ΦIΛHΣEΣ TO ΠPΩI ΠPIN ΦYΓEIΣ KAI EIΠEΣ OTI ΘA ΣOY ΛEIΨΩ.Σ'AΓAΠΩ KAI MOY ΛEIΠEIΣ ΠOΛY.EΛΠIZΩ NA ΓINEIΣ ΓPHΓOPA KAΛA.ΠANE TA ΠAIΔIA ΣTHN TOYAΛETA.ΦIΛAKIA!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.05.17 00:26\",\"\",\"ΣE ΛATPEYΩ ΓI AYTA TA MHNYMATA ΣOY! TΩPA ΠHΓA TA ΠAIΔIA ΣTO MΠANIO. ΠAΩ ΓIA NANI, KAΛHNYXTA MΩPO MOY. Σ'AΓAΠAΩ!\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2017.05.17 00:08\",\"\",\"MΩPO MOY ΣE ΘEΛΩ ΠOΛY OΛEΣ AYTEΣ TIΣ MEPEΣ,ΓIATI AΠO TO ΠAΣXA AKOMH ΔEN ΣE EXΩ XOPTAΣEI.EΛΠIZΩ ΣE ΛIΓO KAIPO NA NIΩΣΩ KAΘE ΣΠIΘAMH ΣOY ΠANΩ MOY.Σ'AΓAΠΩ!ΦIΛAKIA!\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2017.04.28 01:23\",\"\",\"MΩPO MOY ΘEΛΩ NA ΣOY ΠΩ OTI EIMAI ΠOΛY EYTYXIΣMENOΣ ΠOY ΣE EXΩ ΓIATI EIΣAI ΠPAΓMATIKA TO ΣTHPIΓMA MOY KAI TO AΛΛO MOY MIΣO.TAYTOXPONA ME KANEIΣ NA ΘEΛΩ ΣAN TPEΛOΣ NA ΓINOMAΣTE ENA KAI NA ΣE EXΩ ΣΦIXTA ΣTHN AΓKAΛIA MOY.ΔEN YΠAPXEI KATI ΠOY NA MOY ΛEIΠEI ΣTHN ΣXEΣH MAΣ.Σ'AΓAΠΩ!ΦIΛAKIA!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.04.14 22:22\",\"\",\"TEO OTAN ΞANAΠEPAΣEI AΠO MΠPOΣTA O EΠITAΦIOΣ ΠEΣ NA BΓOYME\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.03.31 21:17\",\"\",\"ΦEPE ΛIΓH ΦETA AΠO TO ΣOYΠEP MAPKET ΓIA TA ΠAIΔIA\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2017.03.24 00:21\",\"\",\"MΩPO MOY OΣO ΠEPNAEI O KAIPOΣ MOY ΛEIΠEIΣ ΠIO ΠOΛY.EINAI ΠIO ΔYΣKΛOΛO ΓIA MENA NA KPATIEMAI.ΩΣTOΣO EIMAI ΠOΛY EYTYXIΣMENOΣ ΠOY EIΣAI ΓYNAIKA MOY KAI EXOYME MIA YΠEPOXH OIKOΓENEIA.EIΣAI TO AΛΛO MOY MIΣO KAI ΔE ΘA ΣTAMATHΣΩ NA TO ΛEΩ ΠOTE AYTO.Σ'AΓAΠΩ!ΦIΛAKIA!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"+230983208932\",\"\",\"2017.03.10 07:28\",\"\",\"EΛA EΛENA EIΠA ΣTH MIPEΛΛA NA MOY ΠAPEI ΛIΓO ΨΩMI KAI KOYΛOYPIA ΓIA TA ΠAIΔIA KAΘΩΣ ΘA PXETAI. ΔΩΣE THΣ 2 EYPΩ, ΠOΣO KANOYN KAI EΓΩ ΘA ΣTA ΔΩΣΩ META. ΦIΛAKIA!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.03.08 07:07\",\"\",\"MΩPO MOY KAΛHMEPA! ΦTAΣATE KAΛA ΓIATI EPIΞE ΠOΛY BPOXH\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.03.06 21:28\",\"\",\"KYPIE ΔHMHTPH AYTH H ΓAΣTENTEPITIΔA M'EXEI ΘEPIΣEI. OYTE O ΠYPETOΣ, OYTE TO ENTEPO MOY ΠEPAΣAN. KAI EΠEIΔH EINAI IOΓENHΣ ΦOBAMAI MHN KOΛΛHΣOYN KAI TA ΠAIΔIA. EYXOMAI NA MOY ΠEPAΣEI KAI NA EΠIΣTPEΨΩ THN TETAPTH. ΣYΓΓNΩMH, KAΛO BPAΔY!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"+230983208932\",\"\",\"2017.03.06 19:30\",\"\",\"NA ΣAΣ ZHΣEI! NA EINAI ΓEPOΣ KAI EYΛOΓHMENOΣ! EYXEΣ AΠO OΛOYΣ MAΣ, ΦIΛAKIA!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.03.06 19:26\",\"\",\"EΛA KOPITΣI ΓΛYKO ΓENNHΣATE; OΛA KAΛA; O ΘOΔΩPHΣ MOY EIΠE OTI ΣHMEPA ΘA MΠAINATE...\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.02.21 15:21\",\"\",\"EΦAΓEΣ; ΠPOΛABEΣ; HΔH MOY ΛEIΠEIΣ...\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.02.21 13:23\",\"\",\"TEΛEIΩΣA KAI ΣE ΠEPIMENΩ\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.02.17 13:04\",\"\",\"ΞEXAΣA NA ΣOY ΠΩ. BΓHKAN KAI OI AITHΣEIΣ ΓIA METAΘEΣH, AN ΘEΛEIΣ NA KANEIΣ TA XAPTIA ΣOY. EINAI MEXPI TIΣ 28 ΦEBPOYAPIOY. ΦIΛAKIA!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.02.15 08:09\",\"\",\"EΛA ΠOΠH KAΛHMEPA! EIMAI H MAMA THΣ IOYΛIAΣ THΣ ΣAMAΛH. H IOYΛIA ΘA EPΘEI ΣXOΛEIO META AΠO ΠOΛΛEΣ MEPEΣ KAI ΦOBATAI. AN MΠOPEIΣ, BOHΘHΣE THN NA ΞEKINHΣEI NA ΠAIZEI ME TA ΠAIΔAKIA ΓIA NA ΞANAΠPOΣAPMOΣTEI. THANK YOU\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"+230983208932\",\"\",\"2017.01.30 19:44\",\"\",\"EIMAI ΣTH MAMA,OTAN TEΛEIΩΣEIΣ ΠEΣ MOY\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.01.28 17:33\",\"\",\"KAI 1 ZAXAPH\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.01.28 17:32\",\"\",\"ENA ΠOYMAPO BAZO, ΓAΛATA, ΓAΛA ΓIA THN ANNA-MAPIA, ΓAΛOΠOYΛA KAI ΓKOYNTA ΓIA TOΣT, BOYTYPO ΦΛOPA, 4 AYΓA, ΓIAOYPTAKIA ΓIA TA ΠAIΔIA, ΣOKOΦPETEΣ, ΛEMONAΔEΣ, XYMOYΣ ΓIA ΣXOΛEIO, ΠATATAKIA lays ΣTO ΦOYPNO,  ΨΩMI ΓIA TOΣT\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2017.01.27 06:44\",\"\",\"KAΛHMEPA AΓAΠH MOY! OΛA KAΛA TO BPAΔY, ΔEN ΞANAEKANE EMETO AΠΛA EBΛEΠE EΦIAΛTEΣ ME TIΣ EPΓAΣIEΣ. ΘA MEINEI ΣTO ΣΠITI ΣHMEPA. ΦIΛAKIA!\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2017.01.26 23:31\",\"\",\"MΩPO MOY EXΩ ΠOΛY KAIPO NA MEINΩ MAKPIA ΣOY ΛOΓΩ YΠHPEΣIAΣ.MOY ΛEIΠEIΣ AΠIΣTEYTA KAI ΘEΛΩ MIA AΓKAΛITΣA ΣOY TΩPA.EYTYXΩΣ EPXETAI KAI ΣAB/KO.AN ΞANAΞYΠNHΣEI H IOYΛIA ΠAPE ME THΛ. OTI ΩPA.Σ'AΓAΠΩ.ΦIΛAKIA!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2016.12.29 20:51\",\"\",\"ΦΛOPA, ΓIAOYPTIA, MANITAPOΣOYΠA, ΦYΛΛA ZEΛATINHΣ, KOYBEPTOYPA MAΓIONEZA XAPTI YΓEIAΣ KAΛAMΠOKI ΣKΛHPO TYPI KITPINO, MEΓAΛH KPEMA ΓAΛAKTOΣ ΓIA ΓΛYKA 500MΛ\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2016.12.16 15:41\",\"\",\"ΠATEP KAΛHΣΠEPA! ΠOTE MΠOPΩ NA EPΘΩ ΣTON NAO NA ΔIABAΣOYME MIA ΦANOYPOΠITA ΠOY EΦTIAΞA;\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2016.12.05 19:44\",\"\",\"ΠEPNA AΠO TH MAMA OTAN TEΛEIΩΣEIΣ. ΣE ΘEΛEI ΓIA ΛIΓO\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2016.11.30 00:31\",\"\",\"EΛA BPE ΣEBH XIΛIA ΣYΓΓNΩMH ΠOY ΔEN EXΩ ΠPOΛABEI NA ΣE ΠAPΩ ΠIΣΩ. EIXA KAI TA MΩPA APPΩΣTA ΣHMEPA, O ΘOΔΩPHΣ EIXE EΦHMEPIA, OΠOTE KATAΛABAINEIΣ... ΘA ΣE ΠAPΩ. ΦIΛAKIA!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2016.11.30 00:19\",\"\",\"ANNOYΛA TI YΠEPOXA ΔΩPA EINAI AYTA ΠOY MAΣ EΣTEIΛEΣ! Σ'EYXAPIΣTOYME ΠOΛY ΠOΛY. ΦIΛAKIA KAΛHNYXTA! \"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"+230983208932\",\"\",\"2016.11.16 11:37\",\"\",\"BAΣIΛEIAΔOY EYΓENIA TOY ΔHMHTPIOY (ΔAΣKAΛA). ΠAPOYΣIAZOMAI ΣTH ΛAPIΣA 1 ΔEKEMBPIOY\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2016.11.16 06:52\",\"\",\"KAΛHMEPA MΩPO MOY.EXΩ XAΛAΣTEI ΓIATI XΘEΣ KAI ΠPOXΘEΣ ΔEN ΣOY ΠPOΣΦEPA THN AΠOΛAYΣH ΠOY ΘEΛΩ.EIΣAI OΠΩΣ EIΠEΣ KAI EΣY ΠPOXΘEΣ TO AΛΛO MOY MIΣO KAI ΘEΛΩ KAΘE TETOIA ΣTIΓMH NA ΣOY MENEI AΞEXAΣTH.AΠO TΩPA ΣKEΦTOMAI ΠΩΣ ΘA ΠEPAΣOYN OI 40 MEPEΣ.Σ'AΓAΠΩ!ΦIΛAKIA!\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2016.10.18 19:56\",\"\",\"MΩPO MOY TO BPAΔY ΘEΛΩ NA EΞEPEYNHΣΩ KAΘE ΣΠIΘAMH ΣOY.TOΣEΣ MEPEΣ ΠOY EΛEIΨEΣ TA EXΩ ΞEXAΣEI.ΦIΛAKIA\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2016.10.17 00:22\",\"\",\"ΜΩΡΑΚΙ ΜΟΥ ΑΥΤΕΣ ΤΙΣ ΜΕΡΕΣ ΤΑ ΒΡΑΔΙΑ ΠΟΥ ΧΑΛΑΡΩΝΩ ΚΑΠΩΣ ΝΙΩΘΩ ΟΤΙ ΚΑΤΙ ΔΕΝ ΜΟΥ ΠΑΕΙ ΚΑΛΑ:ΔΕΝ ΕΙΣΑΙ ΔΙΠΛΑ ΜΟΥ.ΕΙΣΑΙ ΟΝΤΩΣ ΑΥΤΟ ΠΟΥ ΛΕΝΕ Η ΚΟΛΟΝΑ ΤΟΥ ΣΠΙΤΙΟΥ.ΕΧΩ ΜΙΑ ΑΝΑΣΦΑΛΕΙΑ ΤΩΡΑ ΠΟΥ ΛΕΙΠΕΙΣ.ΟΣΟ ΓΙΑ ΤΟ ΜΑΣΑΖ ΣΥΜΦΩΝΟΙ ΑΛΛΑ ΜΕ ΕΝΑΝ ΟΡΟ.ΘΑ ΒΑΛΕΙΣ ΑΠΟ...ΚΑΤΩ ΚΑΤΙ ΠΟΥ ΣΟΥ ΕΧΩ ΑΓΟΡΑΣΕΙ ΕΓΩ\uD83D\uDE09.Σ'ΑΓΑΠΩ!ΦΙΛΑΚΙΑ!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2016.10.16 00:50\",\"\",\"TI KANEI O ANTPOYΛHΣ MOY; KAΘE ΦOPA ΠOY EIMAI MAKPIA ΣYNEIΔHTOΠOIΩ ΠOΣO ΠOΛY MOY ΛEIΠEIΣ... ΘA HΘEΛA NA EBΛEΠA TΩPA TA MHNYMATA ΠOY MOY EΣTEΛNEΣ OΣO ZOYΣAME XΩPIΣTA AΛΛA ENA MIKPO XEPAKI TA ΣBHΣE KATA ΛAΘOΣ... ΓI' AYTO MIA EINAI H ΛYΣH: NA APXIΣEIΣ NA MOY ΣTEΛNEIΣ ΞANA. EYXOMAI NA EINAI TO IΔIO H KAI ΠEPIΣΣOTEPO AΓAΠHΣIAPIKA ME TA ΠAΛIA! ONEIPA ΓΛYKA, ΦIΛAKIA\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2016.10.14 00:29\",\"\",\"AΓAΠH MOY TI KANEIΣ; OΛA KAΛA; TA MΩPA KOIMHΘHKAN; ΠΩΣ EINAI H IOYΛITΣA; TEΛIKA ΞANAΓYPIΣA ΣTH ΣIBA ΓIATI ΠHΓA ΣTO NOΣOKOMEIO KAI MOY EIΠAN NA MHN MEINΩ. ΘA TA ΠOYME AYPIO TOTE. MAKIA ΠOΛΛA!\"\n" +
                "\"sms\",\"READ,RECEIVED\",\"+230983208932\",\"\",\"\",\"2016.10.13 23:40\",\"\",\"EΛA MΩPO MOY TI KANEIΣ;ΣKEΦTOMAI ΠΩΣ ΘA BOΛEYTEIΣ EKEI ΣHMEPA ΓIATI EXEIΣ KAI THN MEΣH ΣOY.EΛΠIZΩ NA MHN KOYPAΣTEIΣ ΠOΛY.MOY ΛEIΠEIΣ AΠIΣTEYTA.Σ'AΓAΠΩ!ΦIΛAKIA!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2016.10.13 13:22\",\"\",\"EΛA ANNOYΛA MOY Σ'EYXAPIΣTOYME ΠOΛY. TΩPA EINAI ΣTO ΔΩMATIO KAI KOIMATAI ΛOΓΩ THΣ ANAIΣΘHΣIAΣ.  ΘA ΣE ΠAPΩ KAI THΛ NA TA ΠOYME ANAΛYTIKA. ΦIΛAKIA!\"\n" +
                "\"sms\",\"SENT\",\"+230983208932\",\"230983208932\",\"\",\"2016.10.12 22:44\",\"\",\"MΩPO MOY TO THΛ TOY ΞENOΔOXEIOY EINAI 230983208932 KAI TO ΔΩMATIO MOY EINAI TO 11 11. ΠAPE ME OΠOTE ΘEΛEIΣ, ΦIΛAKIA!\"\n";
        String patternName = new Actions().guessNow(text);
        Format format = FormatSettings.getInstance().getFormats().get(patternName);

        JunitConvertListener convertListener = importString(text, format);
        Assert.assertEquals (56, convertListener.getMessages().size());
    }

    @Test
    public void testGreekCsv() throws URISyntaxException {
        JunitConvertListener convertListener = importFile("athg2sms/greekSMS.csv", BuiltInFormat.NokiaSuite);
        Assert.assertEquals (56, convertListener.getMessages().size());
    }

    //@Test
    public void testNikilesh() throws URISyntaxException {
        JunitConvertListener convertListener = importFile("athg2sms/sampleNikilesh.txt", BuiltInFormat.Nikilesh);
        Assert.assertEquals (2, convertListener.getMessages().size());
    }

    @Test
    public void jayne() throws URISyntaxException {
        JunitConvertListener convertListener = importString(
                "5/17/2017 11:33:55 PM to +230983208932:\n" +
                        "Hi pip hope that you are alright and  you get some sleep tonight ahead of your big day tomorrow. Fingers crossed. Xxx\n" +
                        "\n" +
                        "5/17/2017 10:41:48 PM from +230983208932:\n" +
                        "Thanks a love you night xx\n" +
                        "\n" +
                        "12/9/2017 1:30:19 PM to +230983208932:\n" +
                        "Good! Enjoy yourself and take some photos. Xxx\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "12/9/2017 1:31:11 PM from +230983208932:\n" +
                        "Awe will do thanks mum Xxx\n\n", BuiltInFormat.Jayne);
        Assert.assertEquals (4, convertListener.getMessages().size());
    }

    @Test
    public void homemade1() throws URISyntaxException {
        JunitConvertListener convertListener = importString(
                "\"read\",\"230983208932\",\"2012-06-15 13:29:27\",\"Test message 1\"\n" +
                        "\"sent\",\"230983208932\",\"2012-06-16 03:32:19\",\"Test message 2\"\n" +
                        "\"read\",\"230983208932\",\"2012-06-15 20:17:22\",\"Test message 3\"\n" +
                        "\"sent\",\"230983208932\",\"2012-06-16 04:18:02\",\"Test message 4\"\n" +
                        "\"read\",\"230983208932\",\"2012-06-15 20:20:46\",\"Test message 5\"\n" +
                        "\"sent\",\"230983208932\",\"2012-06-16 04:21:49\",\"Test message 6\"\n" +
                        "\"read\",\"230983208932\",\"2012-06-15 20:26:27\",\"Test message 7\"\n" +
                        "\"read\",\"230983208932\",\"2012-06-15 20:27:13\",\"Test message 8\"\n" +
                        "\"sent\",\"230983208932\",\"2012-06-16 04:27:24\",\"Test message 9\"\n" +
                        "\"read\",\"230983208932\",\"2012-06-15 20:32:30\",\"終わりました。\"", BuiltInFormat.Homemade1);
        Assert.assertEquals (10, convertListener.getMessages().size());
    }

    //@Test
    public void abdul() throws URISyntaxException {
        JunitConvertListener convertListener = importFile("athg2sms/Abdul.csv", BuiltInFormat.AbdulCsv1);
        Assert.assertEquals (5921, convertListener.getMessages().size());
    }
}
