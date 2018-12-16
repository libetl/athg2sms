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
    MixedNokiaCsv ("Mixed Nokia Csv", "[\\s]*sms[,;]$(folder..[,;])[,;](?:\"\"[,;])?\"$(address)\"[,;]\"\"[,;](?:\"\"[,;])?\"$(dateyyyy.MM.dd HH:mm..)\"[,;]\"\"[,;]\"$(body)\"[\\s]+",
            "sms,$(folder),\"$(inbox:address)\",\"$(sent:address)\";\"\";\"$(dateyyyy.MM.dd HH:mm)\",\"\",\"$(body)\"\n", "deliver", "\"submit\""),
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
    NokiaSuite ("Nokia Suite 3.8", "[\\s]*\"sms\",\"$(folder)\",(?:\"\",)?\"$(address)\",(?:[^,]*,)?(?:\"\",)?\"\",\"$(dateyyyy.MM.dd HH:mm)\",\"\",\"$(body)\"[\\s]+",
            "\"sms\",\"$(folder)\",\"$(inbox:address)\",\"$(sent:address)\",\"\",\"$(dateyyyy.MM.dd HH:mm)\",\"\",\"$(body)\"\n", "READ,RECEIVED", "SENT"),
    UnknownSmsFormat1 ("Unknown sms format 1", "[\\s]*\"$(address)\",\"$(dateyyyy-MM-dd HH:mm)\",\"SMS\",\"$(folder)\",\"$(body)\"[\\s]+",
            "\"$(address)\",\"$(dateyyyy-MM-dd HH:mm)\",\"SMS\",\"$(folder)\",\"$(body)\"\n", "0", "1"),
    XmlMessage ("XmlMessage", "<Message>\\s*<Recepients(?:[\\s]*\\/>|>\\s*<string>$(sent:address)<\\/string>\\s*<\\/Recepients>)\\s*<Body(?:[\\s]*\\/>|>$(body)<\\/Body>)\\s*<IsIncoming>$(folder)<\\/IsIncoming>\\s*<IsRead>(?:[^<]+)<\\/IsRead>\\s*<Attachments(?:.*?\\/>)\\s*<LocalTimestamp>$(localtimestamp)<\\/LocalTimestamp>\\s*<Sender(?:[\\s]*\\/>|>$(inbox:address)<\\/Sender>)\\s*<\\/Message>",
            "<Message><Recepients[sent?]><string>$(sent:address)</string></Recepients>[:] />[;]<Body>$(body)</Body><IsIncoming>$(folder)</IsIncoming><IsRead>true</IsRead><Attachments /><LocalTimestamp>$(localtimestamp)</LocalTimestamp><Sender[inbox?]>$(inbox:address)</Sender>[:] />[;]</Message>", "true", "false"),
    SonyEricsson ("SonyEricsson",
            "(?:\n|^)$(dateYYYY-MM-DD HH:mm:ss),$(address),[^,]+,$(folder),\"$(body)\",[^,]+,[^,]+,[^,]+,[^,]+,[^,]+,[^,]+,[^,]+,[^,]+,[^,]+,[^,]+,[^,]+,[^,]+,[^,]+,(?:[^,]+,)?[^,]+,[^,]+,[^,]+,[^,]+,[^,]+,[^,]+,[^,]+,[^,]+,[^,]+,[^,]+,[^\\s]+",
            "", "Received", "Sent"),
    Vmg2018 ("Vmg2018",
            "\\s*BEGIN:\\s*VMSG\\s+VERSION:\\s*1.1\\s+X-IRMC-STATUS:\\s*[^\\s]*?\\s+X-IRMC-BOX:\\s*$(folder..\\s+X-NOK-DT)\\s+X-NOK-DT:\\s*[^\\s]*?\\s+X-MESSAGE-TYPE:\\s*[^\\s]*\\s+BEGIN:\\s*VCARD\\s+VERSION:\\s*3.0\\s+N:\\s*[^\\s]*?\\s+TEL:$(address..\\s+END:VCARD)\\s+END:VCARD\\s+BEGIN:VENV\\s+BEGIN:VBODY\\s+Date:\\s*$(dateDD.MM.YYYY HH:mm:ss..[\r\n]+)[\r\n]+$(body..\\s+END:VBODY)\\s+END:VBODY\\s+END:VENV\\s+END:VMSG\\s+",
            "BEGIN:VMSG\nVERSION:1.1\nX-IRMC-STATUS:READ\nX-IRMC-BOX:$(folder)\nX-NOK-DT:20091022T064825Z\nX-MESSAGE-TYPE:DELIVER\nBEGIN:VCARD\nVERSION:3.0\nN:\nTEL:$(address)\nEND:VCARD\nBEGIN:VENV\nBEGIN:VBODY\nDate:$(dateDD.MM.YYYY HH:mm:ss)\n$(body)\nEND:VBODY\nEND:VENV\nEND:VMSG\n", "INBOX", "DELIVER"),
    WeirdVictorFormat("Weird Victor Format", "[\\s]*[^,]*,$(address),$(body),$(dated/M/yy HH:mm),$(folder),[^,]*,,[^\\n]*\\n",
            "1,$(address),$(body),$(dated/M/yy HH:mm),$(folder),1,,", "receive", "send"),
    Nikilesh("Nikilesh VMG", "\\s*BEGIN:\\s*VMSG\\s+VERSION:\\s*1.1\\s+X-IRMC-STATUS:\\s*[^\\s]*?\\s+X-IRMC-BOX:\\s*$(folder..\\s+X-NOK-DT)\\s+X-NOK-DT:\\s*[^\\s]*?\\s+X-MESSAGE-TYPE:\\s*[^\\s]*?\\s+BEGIN:\\s*VCARD\\s+VERSION:\\s*3.0\\s+N:\\s*[^\\s]*?\\s+TEL:$(address..\\s+END:VCARD)\\s+END:VCARD\\s+BEGIN:VENV\\s+BEGIN:VBODY\\s+Date:\\s*$(dateDD.MM.YYYY HH:mm:ss..[\\r\\n]+)[\\r\\n]+$(body..\\s+END:VBODY)\\s+END:VBODY\\s+END:VENV\\s+END:VMSG\\s+",
            "", "INBOX", "SENT"),
    //Jayne("Jayne", "\\s*$(dateM/d/yyyy h:mm:ss a..[from|to])\\s*$(folder)\\s*$(address):[\\r\\n]+$(body..[\\n]{2,})[\\n]{2,}","", "from", "to"),
    Homemade1("Homemade1", "\"$(folder)\",\"$(address)\",\"$(dateyyyy-MM-dd HH:mm:ss)\",\"$(body)\"\n", "\"$(folder)\",\"$(address)\",\"$(dateyyyy-MM-dd HH:mm:ss)\",\"$(body)\"\n", "read", "sent"),
    AbdulCsv1("Abdul Csv1", "$(dateyyyy-MM-dd HH:mm:ss),$(address),[^,]*,$(folder),$(body),[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^\r\n]*[\r\n]+", "", "Received", "Sent"),
    AbdulCsv2("Abdul Csv2", "$(dateyyyy-MM-dd HH:mm:ss),$(address),[^,]*,$(folder),\"$(body)\",[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^\r\n]*[\r\n]+", "", "Received", "Sent"),
    Momin1("Momin1", "[0-9]+,$(address),$(folder),$(dateyyyy-MM-dd'T'HH:mm:ss.SSSX),(?!\")$(body),[^\r\n]*[\r\n]+", "", "RECEIVED", "SENT"),
    Momin2("Momin2", "[0-9]+,$(address),$(folder),$(dateyyyy-MM-dd'T'HH:mm:ss.SSSX),\"$(body)\",[^\r\n]*[\r\n]+", "", "RECEIVED", "SENT"),
    DaddyCsv1("DaddyCsv1", "$(dateyyyy-MM-dd),$(dateHH:mm:ss),$(folder),$(address),[^,]*,$(body)\n", "", "in", "out"),
    DaddyCsv2("DaddyCsv2", "$(dateyyyy-MM-dd),$(dateHH:mm:ss),$(folder),$(address),[^,]*,\"$(body..\")\"\n", "", "in", "out"),
    BenCohen("BenCohen", "$(dateMMM dd), $(dateyyyy h:mm:ss aaa),$(address),\"[^\"]*\",[^,]*,\"$(body)\",[^,]*,[^,]*,[^,]*,$(folder)\n", "", "Received", "Sent"),
    MySms("MySms", "$(dated.M.yyyy HH:mm:ss);$(address);[^;]*;$(folder);\"$(body)\";[^;]*;[^;]*;[^;]*;[^\\n]*\\n", "1", "0",
            "$(dated.M.yyyyHH:mm:ss);$(address);\"\";$(folder);\"$(body)\";1;0;mobile carrier;Complete\\n"),
    MiSuite("MiSuite", "$(folder),$(address),$(dateyyyy-MM-dd HH:mm:ss),\"$(body..\",)\",[^,]*,[^,]*,[^\\n]*\\n",
            "$(folder),$(address),$(dateyyyy-MM-dd HH:mm:ss),\"$(body)\",??,??,NONE\\n", "RECEIVED", "SENT"),
    KamenSent("KamenSend", "\"[^\"]*\",\"[^\"]*\",\"SELF$(folder)\",\"$(address)\",\"[^\"]*\",\"$(body)\",\"text/plain\",\"[^\"]*\",\"$(date)\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\"\n",
            "", "", "-1"),
    KamenReceived("KamenReceived",         "\"[^\"]*\",\"[^\"]*\",\"$(address)\",\"SELF$(folder)\",\"[^\"]*\",\"$(body)\",\"text/plain\",\"[^\"]*\",\"$(date)\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\",\"[^\"]*\"\n",
            "", "", "-1"),
    ;

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
