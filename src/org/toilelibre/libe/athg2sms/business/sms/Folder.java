package org.toilelibre.libe.athg2sms.business.sms;


public enum Folder {
    INBOX,SENT;

    public String getFolderName() {
        return name().toLowerCase();
    }
}
