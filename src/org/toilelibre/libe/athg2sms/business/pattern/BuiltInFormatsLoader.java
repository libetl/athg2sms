package org.toilelibre.libe.athg2sms.business.pattern;

import java.util.HashMap;
import java.util.Map;

class BuiltInFormatsLoader {

    Map<String, Format> loadDefaults () {
        final Map<String, Format> formats = new HashMap<String, Format>();
        formats.put(BuiltInFormatName.DavidNokia.getValue(), new Format(BuiltInFormatName.DavidNokia.getValue(), "[\\s]*$(folder),\"$(dateyyyy-MM-dd HH:mm:ss)\",[^,]*,[^,]*,\"$(body)\",Delivered[\\s]+", "$(folder),\"$(dateyyyy-MM-dd HH:mm:ss)\",0,0,\"$(body)\",Delivered\n", "received", "sent"));
        formats.put(BuiltInFormatName.LumiaVmg.getValue(), new Format(BuiltInFormatName.LumiaVmg.getValue(), "[\\s]*BEGIN:VMSG\\s+VERSION:[ ]*[0-9]+(?:.[0-9]+)\\s+BEGIN:VCARD\\s+TEL:$(address..\\s+END:VCARD)\\s+END:VCARD\\s+BEGIN:VBODY\\s+X-BOX:$(folder..\\s+X-READ)\\s+X-READ:[^\\s]+\\s+X-SIMID:[^\\s]+\\s+X-LOCKED:[^\\s]+\\s+X-TYPE:[^\\s]+\\s+Date:$(dateyyyy/MM/dd HH:mm:ss..\\s+Subject)\\s+Subject;ENCODING=$(encoding);CHARSET=$(charset):$(body..\\s+END:VBODY)\\s+END:VBODY\\s+END:VMSG\\s+",
                "BEGIN:VMSG\r\nVERSION: 1.1\r\nBEGIN:VCARD\r\nTEL:$(address)\r\nEND:VCARD\r\nBEGIN:VBODY\r\nX-BOX:$(folder)\r\nX-READ:READ\r\nX-SIMID:0\r\nX-LOCKED:UNLOCKED\r\nX-TYPE:SMS\r\nDate:$(dateyyyy/MM/dd HH:mm:ss)\r\nSubject;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8:$(body)\r\nEND:VBODY\r\nEND:VMSG\r\n", "INBOX", "SENDBOX"));
        formats.put(BuiltInFormatName.NokiaVmgInbox.getValue(), new Format(BuiltInFormatName.NokiaVmgInbox.getValue(), "[\\s]*BEGIN:VENV\nBEGIN:VCARD\nVERSION:[0-9]+(?:.[0-9]+)\nN:$(folder)\nTEL:$(address)\nEND:VCARD\nBEGIN:VENV\nBEGIN:VBODY\nDate:$(datedd-MM-yyyy HH:mm:ss)\n$(body)\nEND:VBODY\nEND:VENV\nEND:VENV\n",
                "BEGIN:VENV\nBEGIN:VCARD\nVERSION:[0-9]+(?:.[0-9]+)\nN:$(folder)\nTEL:$(address)\nEND:VCARD\nBEGIN:VENV\nBEGIN:VBODY\nDate:$(datedd-MM-yyyy HH:mm:ss)\n$(body)\nEND:VBODY\nEND:VENV\nEND:VENV\n", "", "?"));
        formats.put(BuiltInFormatName.NokiaVmgSent.getValue(), new Format(BuiltInFormatName.NokiaVmgSent.getValue(), "[\\s]*BEGIN:VENV\nBEGIN:VCARD\nVERSION:[0-9]+(?:.[0-9]+)\nN:$(folder)\nTEL:$(address)\nEND:VCARD\nBEGIN:VENV\nBEGIN:VBODY\nDate:$(datedd-MM-yyyy HH:mm:ss)\n$(body)\nEND:VBODY\nEND:VENV\nEND:VENV\n",
                "BEGIN:VENV\nBEGIN:VCARD\nVERSION:[0-9]+(?:.[0-9]+)\nN:$(folder)\nTEL:$(address)\nEND:VCARD\nBEGIN:VENV\nBEGIN:VBODY\nDate:$(datedd-MM-yyyy HH:mm:ss)\n$(body)\nEND:VBODY\nEND:VENV\nEND:VENV\n", "", "?"));
        formats.put(BuiltInFormatName.NokiaCsvWithQuotes.getValue(), new Format(BuiltInFormatName.NokiaCsvWithQuotes.getValue(), "[\\s]*\"sms\";\"$(folder)\";(?:\"\";)?\"$(address)\";\"\";(?:\"\";)?\"$(dateyyyy.MM.dd HH:mm)\";\"\";\"$(body)\"[\\s]+",
                "\"sms\";\"$(folder)\";\"$(inbox:address)\";\"$(sent:address)\";\"\";\"$(dateyyyy.MM.dd HH:mm)\";\"\";\"$(body)\"\n", "deliver", "submit"));
        formats.put(BuiltInFormatName.NokiaCsvWithCommas.getValue(), new Format(BuiltInFormatName.NokiaCsvWithCommas.getValue(), "[\\s]*sms,$(folder),(?:\"\",)?\"$(address)\",\"\",(?:\"\",)?\"$(dateyyyy.MM.dd HH:mm)\",\"\",\"$(body)\"[\\s]+",
                "sms,$(folder),\"$(inbox:address)\",\"$(sent:address)\";\"\";\"$(dateyyyy.MM.dd HH:mm)\",\"\",\"$(body)\"\n", "deliver", "submit"));
        formats.put(BuiltInFormatName.IPhoneCsv.getValue(), new Format(BuiltInFormatName.IPhoneCsv.getValue(), "[\\s]*\"$(folder)\",\"$(dateM/d/yy)\",\"$(dateH:mm a)\",\"$(address)\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"$(body)\",\"[^\"]*\"[\\s]+",
                "\"$(folder)\",\"$(dateM/d/yy)\",\"$(dateH:mm a)\",\"$(address)\",\"\",\"\",\"\",\"$(body)\",\"\"\n", "Received", "Sent"));
        formats.put(BuiltInFormatName.BlackberryCsv.getValue(), new Format(BuiltInFormatName.BlackberryCsv.getValue(), "[\\s]*[^,]*,(?:,)?$(dateEEE MMM d HH:mm:ss zzz yyyy),(?:,)?$(folder),$(address),\"$(body)\"[\\s]+",
                "1,$(dateEEE MMM d HH:mm:ss zzz yyyy),$(folder),$(address),\"$(body)\"\n", "false", "true"));
        formats.put(BuiltInFormatName.DateAndFromAndAddressAndbody.getValue(), new Format(BuiltInFormatName.DateAndFromAndAddressAndbody.getValue(), "[\\s]*$(dateM/d/yy HH:mm:ss a);$(folder);$(address);\"\";\"$(body)\"[\\s]+",
                "$(dateM/d/yy HH:mm:ss a);$(folder);$(address);\"\";\"$(body)\"\n", "from", "to"));
        formats.put(BuiltInFormatName.DateAndAddressAndBodyAndINBOX.getValue(), new Format(BuiltInFormatName.DateAndAddressAndBodyAndINBOX.getValue(), "[\\s]*\"$(dateyy-M-d HH:mm:ss)\",\"$(address)\",\"\",\"$(body)\",\"$(folder)\"[\\s]+",
                "\"$(dateyy-M-d HH:mm:ss)\",\"$(address)\",\"\",\"$(body)\",\"$(folder)\"\n", "INBOX", "SENT"));
        formats.put(BuiltInFormatName.NokiaCsv.getValue(), new Format(BuiltInFormatName.NokiaCsv.getValue(), "[\\s]*sms;$(folder);(?:\"\";)?\"$(address)\";\"\";(?:\"\";)?\"$(dateyyyy.MM.dd HH:mm)\";\"\";\"$(body)\"[\\s]+",
                "sms;$(folder);\"$(inbox:address)\";\"$(sent:address)\";\"\";\"$(dateyyyy.MM.dd HH:mm)\";\"\";\"$(body)\"\n", "deliver", "submit"));
        formats.put(BuiltInFormatName.NokiaSuite.getValue(), new Format(BuiltInFormatName.NokiaSuite.getValue(), "[\\s]*\"sms\",\"$(folder)\",(?:\"\",)?\"$(address)\",(?:\"\",)?\"\",\"$(dateyyyy.MM.dd HH:mm)\",\"\",\"$(body)\"[\\s]+",
                "\"sms\",\"$(folder)\",\"$(inbox:address)\",\"$(sent:address)\",\"\",\"$(dateyyyy.MM.dd HH:mm)\",\"\",\"$(body)\"\n", "READ,RECEIVED", "SENT"));
        formats.put(BuiltInFormatName.UnknownSmsFormat1.getValue(), new Format(BuiltInFormatName.UnknownSmsFormat1.getValue(), "[\\s]*\"$(address)\",\"$(dateyyyy-MM-dd HH:mm)\",\"SMS\",\"$(folder)\",\"$(body)\"[\\s]+",
                "\"$(address)\",\"$(dateyyyy-MM-dd HH:mm)\",\"SMS\",\"$(folder)\",\"$(body)\"\n", "0", "1"));
        return formats;
    }
}
