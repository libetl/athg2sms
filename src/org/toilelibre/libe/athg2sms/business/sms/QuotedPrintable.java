package org.toilelibre.libe.athg2sms.business.sms;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * A class containing static methods to perform decoding from <b>quoted
 * printable</b> content transfer encoding and to encode into
 */
public class QuotedPrintable {

    private static final byte ESCAPE_CHAR = '=';

    private static final List<Integer> PRINTABLE_CHARS = Arrays.asList(33, 34, 35, 36, 37, 38, 39,
            40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 62,
            63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84,
            85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104,
            105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121,
            122, 123, 124, 125, 126, 9, 32);

    public byte[] encodeQuotedPrintable(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int b : bytes) {
            if (b < 0) {
                b = 256 + b;
            }
            if (PRINTABLE_CHARS.contains(b)) {
                buffer.write(b);
            } else {
                encodeQuotedPrintable(b, buffer);
            }
        }
        return buffer.toByteArray();
    }

    private void encodeQuotedPrintable(int b, ByteArrayOutputStream buffer) {
        buffer.write(ESCAPE_CHAR);
        char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
        char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
        buffer.write(hex1);
        buffer.write(hex2);
    }

    byte [] decodeQuotedPrintable (byte [] bytes) {
        if (bytes == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream ();
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes [i];
            if (b == ESCAPE_CHAR) {
                try {
                    if (bytes.length > i + 1 && '\n' == (char) bytes [i + 1]) {
                        i+= 1;
                        continue;
                    }
                    if (bytes.length > i + 2 && '\r' == (char) bytes [i + 1] && '\n' == (char) bytes [i + 2]) {
                        i += 2;
                        continue;
                    }
                    if (bytes.length > i + 2) {
                        int u = Character.digit((char) bytes[++i], 16);
                        int l = Character.digit((char) bytes[++i], 16);
                        if (u == -1 || l == -1) {
                            return null;
                        }
                        buffer.write((char) ((u << 4) + l));
                    }
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