package org.toilelibre.libe.athg2sms.business.sms;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsList {

    private SmsList () {}

    private boolean built = false;
    private final Map<CharSequence, List<Sms>> smsList = new HashMap<CharSequence, List<Sms>>();
    private static final SmsList INSTANCE = new SmsList();

    public static SmsList getInstance () {
        return INSTANCE;
    }

    public Map<CharSequence, List<Sms>> getSmsList() {
        return smsList;
    }

    public SmsList buildFrom(List<Sms> list) {
        final Map<CharSequence, List<Sms>> smsPerPeer = new HashMap<CharSequence, List<Sms>>();
        for (Sms sms : list){
            Sms smsWithParsedAddress = sms.withParsedAddress();
            String parsedAddress = smsWithParsedAddress.getParsedAddress();
            if (smsPerPeer.get(parsedAddress) == null) {
                smsPerPeer.put(parsedAddress, new ArrayList<Sms>());
            }
            smsPerPeer.get(parsedAddress).add(smsWithParsedAddress);
        }
        this.getSmsList().putAll(smsPerPeer);
        this.built = true;
        return this;
    }

    public boolean isBuilt() {
        return built;
    }

    public void setBuilt(boolean built) {
        this.built = built;
    }
}
