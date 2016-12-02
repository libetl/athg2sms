package org.toilelibre.libe.athg2sms.business.convert;

public class ConvertException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -5431859258986390705L;

    public ConvertException(final String detailMessage, final Throwable throwable) {
        super (detailMessage, throwable);
    }

}
