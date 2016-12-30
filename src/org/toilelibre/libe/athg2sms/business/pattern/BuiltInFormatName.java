package org.toilelibre.libe.athg2sms.business.pattern;


public enum BuiltInFormatName {
    MxtSmsglobal("Message Xtreme Gateway"), NokiaSuite ("Nokia Suite 3.8"), NokiaVmgInbox ("Nokia Vmg Inbox"), NokiaVmgSent ("Nokia Vmg Sent"), NokiaCsv ("Nokia Csv"), IPhoneCsv ("iPhone Csv"), BlackberryCsv ("Blackberry Csv"), DateAndFromAndAddressAndbody ("Date+'from'+address+body"),
    DateAndAddressAndBodyAndINBOX ("Date+address+body+INBOX"), NokiaCsvWithQuotes ("Nokia Csv with quotes"), NokiaCsvWithCommas ("Nokia Csv with commas"), UnknownSmsFormat1 ("Unknown sms format 1"), LumiaVmg ("Lumia Vmg");

    private final String value;

    BuiltInFormatName(final String value) {
        this.value = value;
    }

    public String getValue () {
        return this.value;
    }
}
