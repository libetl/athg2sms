package org.toilelibre.libe.athg2sms.util;

import java.io.ByteArrayOutputStream;

/**
 * A class containing static methods to perform decoding from <b>quoted
 * printable</b> content transfer encoding and to encode into
 */
public class QuotedPrintable {
    private static byte ESCAPE_CHAR = '=';

    public static final byte [] decodeQuotedPrintable (byte [] bytes) {
        if (bytes == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream ();
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes [i];
            if (b == ESCAPE_CHAR) {
                try {
                    if ('\r' == (char) bytes [i + 1] && '\n' == (char) bytes [i + 2]) {
                        i += 2;
                        continue;
                    }
                    int u = Character.digit ((char) bytes [++i], 16);
                    int l = Character.digit ((char) bytes [++i], 16);
                    if (u == -1 || l == -1) {
                        return null;
                    }
                    buffer.write ((char) ((u << 4) + l));
                } catch (ArrayIndexOutOfBoundsException e) {
                    return null;
                }
            } else {
                buffer.write (b);
            }
        }
        return buffer.toByteArray ();
    }

}