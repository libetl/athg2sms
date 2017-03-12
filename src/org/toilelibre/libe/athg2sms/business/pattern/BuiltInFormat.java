package org.toilelibre.libe.athg2sms.business.pattern;


import java.util.HashMap;
import java.util.Map;

public enum BuiltInFormat {
    MxtSmsglobal ("Message Xtreme SMSGlobal", "[\\s]*\"$(dateyyyy-MM-dd HH:mm:ss)\",[^,]*,$(address),\"$(body)\",$(folder)\n", "\"$(dateyyyy-MM-dd HH:mm:ss)\",0000000000,$(address),\"$(body)\",Delivered[\\s]+", "?", "Delivered"),
    LumiaVmg ("Lumia Vmg", "[\\s]*BEGIN:VMSG\\s+VERSION:[ ]*[0-9]+(?:.[0-9]+)\\s+BEGIN:VCARD\\s+TEL:$(address..\\s+END:VCARD)\\s+END:VCARD\\s+BEGIN:VBODY\\s+X-BOX:$(folder..\\s+X-READ)\\s+X-READ:[^\\s]+\\s+X-SIMID:[^\\s]+\\s+X-LOCKED:[^\\s]+\\s+X-TYPE:[^\\s]+\\s+Date:$(dateyyyy/MM/dd HH:mm:ss..\\s+Subject)\\s+Subject;ENCODING=$(encoding);CHARSET=$(charset):$(body..\\s+END:VBODY)\\s+END:VBODY\\s+END:VMSG\\s+",
            "BEGIN:VMSG\r\nVERSION: 1.1\r\nBEGIN:VCARD\r\nTEL:$(address)\r\nEND:VCARD\r\nBEGIN:VBODY\r\nX-BOX:$(folder)\r\nX-READ:READ\r\nX-SIMID:0\r\nX-LOCKED:UNLOCKED\r\nX-TYPE:SMS\r\nDate:$(dateyyyy/MM/dd HH:mm:ss)\r\nSubject;ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8:$(body)\r\nEND:VBODY\r\nEND:VMSG\r\n", "INBOX", "SENDBOX"),
    NokiaVmgInbox ("Nokia Vmg Inbox", "[\\s]*BEGIN:VENV\nBEGIN:VCARD\nVERSION:[0-9]+(?:.[0-9]+)\nN:$(folder)\nTEL:$(address)\nEND:VCARD\nBEGIN:VENV\nBEGIN:VBODY\nDate:$(datedd-MM-yyyy HH:mm:ss)\n$(body)\nEND:VBODY\nEND:VENV\nEND:VENV\n",
            "BEGIN:VENV\nBEGIN:VCARD\nVERSION:[0-9]+(?:.[0-9]+)\nN:$(folder)\nTEL:$(address)\nEND:VCARD\nBEGIN:VENV\nBEGIN:VBODY\nDate:$(datedd-MM-yyyy HH:mm:ss)\n$(body)\nEND:VBODY\nEND:VENV\nEND:VENV\n", "", "?"),
    NokiaVmgSent ("Nokia Vmg Sent", "[\\s]*BEGIN:VENV\nBEGIN:VCARD\nVERSION:[0-9]+(?:.[0-9]+)\nN:$(folder)\nTEL:$(address)\nEND:VCARD\nBEGIN:VENV\nBEGIN:VBODY\nDate:$(datedd-MM-yyyy HH:mm:ss)\n$(body)\nEND:VBODY\nEND:VENV\nEND:VENV\n",
            "BEGIN:VENV\nBEGIN:VCARD\nVERSION:[0-9]+(?:.[0-9]+)\nN:$(folder)\nTEL:$(address)\nEND:VCARD\nBEGIN:VENV\nBEGIN:VBODY\nDate:$(datedd-MM-yyyy HH:mm:ss)\n$(body)\nEND:VBODY\nEND:VENV\nEND:VENV\n", "", "?"),
    NokiaCsvWithQuotes ("Nokia Csv with quotes", "[\\s]*\"sms\";\"$(folder)\";(?:\"\";)?\"$(address)\";\"\";(?:\"\";)?\"$(dateyyyy.MM.dd HH:mm)\";\"\";\"$(body)\"[\\s]+",
            "\"sms\";\"$(folder)\";\"$(inbox:address)\";\"$(sent:address)\";\"\";\"$(dateyyyy.MM.dd HH:mm)\";\"\";\"$(body)\"\n", "deliver", "submit"),
    NokiaCsvWithCommas ("Nokia Csv with commas", "[\\s]*sms,$(folder),(?:\"\",)?\"$(address)\",\"\",(?:\"\",)?\"$(dateyyyy.MM.dd HH:mm)\",\"\",\"$(body)\"[\\s]+",
            "sms,$(folder),\"$(inbox:address)\",\"$(sent:address)\";\"\";\"$(dateyyyy.MM.dd HH:mm)\",\"\",\"$(body)\"\n", "deliver", "submit"),
    IPhoneCsv ("iPhone Csv", "[\\s]*\"$(folder)\",\"$(dateM/d/yy)\",\"$(dateH:mm a)\",\"$(address)\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"$(body)\",\"[^\"]*\"[\\s]+",
            "\"$(folder)\",\"$(dateM/d/yy)\",\"$(dateH:mm a)\",\"$(address)\",\"\",\"\",\"\",\"$(body)\",\"\"\n", "Received", "Sent"),
    BlackberryCsv ("Blackberry Csv", "[\\s]*[^,]*,(?:,)?$(dateEEE MMM d HH:mm:ss zzz yyyy),(?:,)?$(folder),$(address),\"$(body)\"[\\s]+",
            "1,$(dateEEE MMM d HH:mm:ss zzz yyyy),$(folder),$(address),\"$(body)\"\n", "false", "true"),
    DateAndFromAndAddressAndbody ("Date+'from'+address+body", "[\\s]*$(dateM/d/yy HH:mm:ss a);$(folder);$(address);\"\";\"$(body)\"[\\s]+",
            "$(dateM/d/yy HH:mm:ss a);$(folder);$(address);\"\";\"$(body)\"\n", "from", "to"),
    DateAndAddressAndBodyAndINBOX ("Date+address+body+INBOX", "[\\s]*\"$(dateyy-M-d HH:mm:ss)\",\"$(address)\",\"\",\"$(body)\",\"$(folder)\"[\\s]+",
            "\"$(dateyy-M-d HH:mm:ss)\",\"$(address)\",\"\",\"$(body)\",\"$(folder)\"\n", "INBOX", "SENT"),
    NokiaCsv ("Nokia Csv", "[\\s]*sms;$(folder);(?:\"\";)?\"$(address)\";\"\";(?:\"\";)?\"$(dateyyyy.MM.dd HH:mm)\";\"\";\"$(body)\"[\\s]+",
            "sms;$(folder);\"$(inbox:address)\";\"$(sent:address)\";\"\";\"$(dateyyyy.MM.dd HH:mm)\";\"\";\"$(body)\"\n", "deliver", "submit"),
    NokiaCsvWithoutQuotes ("Nokia Csv Without Quotes", "[\\s]*sms,\"$(folder)\",$(address),,,$(date),,\"$(body)\"[\\s]+",
            "sms,\"$(folder)\",$(address),,,$(date),,\"$(body)\"\n", "", "?"),
    NokiaSuite ("Nokia Suite 3.8", "[\\s]*\"sms\",\"$(folder)\",(?:\"\",)?\"$(address)\",(?:\"\",)?\"\",\"$(dateyyyy.MM.dd HH:mm)\",\"\",\"$(body)\"[\\s]+",
            "\"sms\",\"$(folder)\",\"$(inbox:address)\",\"$(sent:address)\",\"\",\"$(dateyyyy.MM.dd HH:mm)\",\"\",\"$(body)\"\n", "READ,RECEIVED", "SENT"),
    UnknownSmsFormat1 ("Unknown sms format 1", "[\\s]*\"$(address)\",\"$(dateyyyy-MM-dd HH:mm)\",\"SMS\",\"$(folder)\",\"$(body)\"[\\s]+",
            "\"$(address)\",\"$(dateyyyy-MM-dd HH:mm)\",\"SMS\",\"$(folder)\",\"$(body)\"\n", "0", "1"),
    XmlMessage ("XmlMessage", "<Message>\\s*<Recepients(?:[\\s]*\\/>|>\\s*<string>$(sent:address)<\\/string>\\s*<\\/Recepients>)\\s*<Body(?:[\\s]*\\/>|>$(body)<\\/Body>)\\s*<IsIncoming>$(folder)<\\/IsIncoming>\\s*<IsRead>(?:[^<]+)<\\/IsRead>\\s*<Attachments(?:.*?\\/>)\\s*<LocalTimestamp>$(date)<\\/LocalTimestamp>\\s*<Sender(?:[\\s]*\\/>|>$(inbox:address)<\\/Sender>)\\s*<\\/Message>",
            "<Message><Recepients[sent?]><string>$(sent:address)</string></Recepients>[:] />[;]<Body>$(body)</Body><IsIncoming>$(folder)</IsIncoming><IsRead>true</IsRead><Attachments /><LocalTimestamp>$(date)</LocalTimestamp><Sender[inbox?]>$(inbox:address)</Sender>[:] />[;]</Message>", "true", "false");

    private final String completeName;
    private final String format;
    private final String exportFormat;
    private final String inboxKeyword;
    private final String sentKeyword;

    BuiltInFormat(String completeName, String format, String exportFormat, String inboxKeyword, String sentKeyword) {
        this.completeName = completeName;
        this.format = format;
        this.exportFormat = exportFormat;
        this.inboxKeyword = inboxKeyword;
        this.sentKeyword = sentKeyword;
    }

    public String getCompleteName() {
        return completeName;
    }

    private Format getAsFormat() {
        return new Format(completeName, format, exportFormat, inboxKeyword, sentKeyword);
    }

    static Map<String, Format> formats () {
        final Map<String, Format> formats = new HashMap<String, Format>();

        for (BuiltInFormat builtInFormat : BuiltInFormat.values()) {
            formats.put(builtInFormat.getCompleteName(), builtInFormat.getAsFormat());
        }

        return formats;
    }
}
