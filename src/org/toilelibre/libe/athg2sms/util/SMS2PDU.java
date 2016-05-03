package org.toilelibre.libe.athg2sms.util;

//Useful tools for coding and decoding SMS

//
//file:    SMSTools.java
//used by: SMS.java
//
//This is a library with useful tools for PDU and SMS coding and decoding.
//
//28. May   2003 - V 6, rw: add date and time in SMS extraction,
//                        improved conversion from GSM characters to Unicode characters
//                        1st published version
//26. May   2003 - V 5, rw: fix concerning a bug in expand method
//15. May   2003 - V 4, rw: improved documentation and clarifications
//24. April 2003 - V 3, rw: code review and optimisation, improved documentation
//15. Oct.  2002 - V 2, rw: improved documentation
//19. Dec.  2002 - V 1, rw: initial runnable version
//---------------------------------------------------------------------------------------

class SMS2PDU {
    /**
     * Creates a PDU
     * 
     * @return PDU
     */
    public static byte [] getPDU (final String number, final String message,
            final boolean numberType) {
        byte [] pdu;
        byte [] no = convertDialNumber (number);
        byte [] msg = compress (convertUnicode2GSM (message));
        int l = no.length;
        int m = msg.length;
        pdu = new byte [4 + l + 4 + m];
        
        // first byte
        pdu [0] = 0x11; // validity period byte used ???
        
        // message reference number
        pdu [1] = 0x00; // use default value ???
        
        // destination address
        pdu [2] = (byte) number.length (); // set length of dialing number
        if (numberType == true)
            pdu [3] = (byte) 0x81; // indicator for a national number
        else
            pdu [3] = (byte) 0x91; // indicator for a international number
            
        System.arraycopy (no, 0, pdu, 4, l); // set dialing number
        
        // protocol identifier
        pdu [4 + l] = 0x00; // use GSM 03.40 default value
        
        // coding scheme
        pdu [4 + l + 1] = 0x00; // use GSM 03.38 character set (= default value)
        
        // validity period
        pdu [4 + l + 2] = (byte) 0xAA; // message validity period = 4 days
        
        // message (= SMS content)
        pdu [4 + l + 3] = (byte) message.length (); // set length of message
        System.arraycopy (msg, 0, pdu, 4 + l + 4, m); // set message
        
        return pdu;
    } // getPDU
    
    /**
     * Convert a dialing number into the GSM format
     * 
     * @param number
     *            dialing number
     * @return coded dialing number
     */
    private static byte [] convertDialNumber (String number) {
        int l = number.length ();
        int j = 0; // index in addr
        int n; // length of converted dial number
        byte [] data;
        
        // calculate length of converted dialing number
        n = l / 2;
        if (l % 2 != 0) {
            n++;
        }
        data = new byte [n];
        for (int i = 0 ; i < n ; i++) {
            switch (number.charAt (j)) {
            case '0' :
                data [i] += 0x00;
                break;
            case '1' :
                data [i] += 0x01;
                break;
            case '2' :
                data [i] += 0x02;
                break;
            case '3' :
                data [i] += 0x03;
                break;
            case '4' :
                data [i] += 0x04;
                break;
            case '5' :
                data [i] += 0x05;
                break;
            case '6' :
                data [i] += 0x06;
                break;
            case '7' :
                data [i] += 0x07;
                break;
            case '8' :
                data [i] += 0x08;
                break;
            case '9' :
                data [i] += 0x09;
                break;
            } // switch
            if (j + 1 < l) {
                switch (number.charAt (j + 1)) {
                case '0' :
                    data [i] += 0x00;
                    break;
                case '1' :
                    data [i] += 0x10;
                    break;
                case '2' :
                    data [i] += 0x20;
                    break;
                case '3' :
                    data [i] += 0x30;
                    break;
                case '4' :
                    data [i] += 0x40;
                    break;
                case '5' :
                    data [i] += 0x50;
                    break;
                case '6' :
                    data [i] += 0x60;
                    break;
                case '7' :
                    data [i] += 0x70;
                    break;
                case '8' :
                    data [i] += 0x80;
                    break;
                case '9' :
                    data [i] += 0x90;
                    break;
                } // switch
            } // if
            else {
                data [i] += 0xF0;
            } // else
            j += 2;
        } // for
        return data;
    } // convertDialNumber
    
    /**
     * Convert a Unicode text string into the GSM standard alphabet
     * 
     * @param msg
     *            text string in ASCII
     * @return text string in GSM standard alphabet
     */
    private static byte [] convertUnicode2GSM (String msg) {
        byte [] data = new byte [msg.length ()];
        
        for (int i = 0 ; i < msg.length () ; i++) {
            switch (msg.charAt (i)) {
            case '@' :
                data [i] = 0x00;
                break;
            case '$' :
                data [i] = 0x02;
                break;
            case '\n' :
                data [i] = 0x0A;
                break;
            case '\r' :
                data [i] = 0x0D;
                break;
            case '_' :
                data [i] = 0x11;
                break;
            case 'ß' :
                data [i] = 0x1E;
                break;
            case ' ' :
                data [i] = 0x20;
                break;
            case '!' :
                data [i] = 0x21;
                break;
            case '\"' :
                data [i] = 0x22;
                break;
            case '#' :
                data [i] = 0x23;
                break;
            case '%' :
                data [i] = 0x25;
                break;
            case '&' :
                data [i] = 0x26;
                break;
            case '\'' :
                data [i] = 0x27;
                break;
            case '(' :
                data [i] = 0x28;
                break;
            case ')' :
                data [i] = 0x29;
                break;
            case '*' :
                data [i] = 0x2A;
                break;
            case '+' :
                data [i] = 0x2B;
                break;
            case ',' :
                data [i] = 0x2C;
                break;
            case '-' :
                data [i] = 0x2D;
                break;
            case '.' :
                data [i] = 0x2E;
                break;
            case '/' :
                data [i] = 0x2F;
                break;
            case '0' :
                data [i] = 0x30;
                break;
            case '1' :
                data [i] = 0x31;
                break;
            case '2' :
                data [i] = 0x32;
                break;
            case '3' :
                data [i] = 0x33;
                break;
            case '4' :
                data [i] = 0x34;
                break;
            case '5' :
                data [i] = 0x35;
                break;
            case '6' :
                data [i] = 0x36;
                break;
            case '7' :
                data [i] = 0x37;
                break;
            case '8' :
                data [i] = 0x38;
                break;
            case '9' :
                data [i] = 0x39;
                break;
            case ':' :
                data [i] = 0x3A;
                break;
            case ';' :
                data [i] = 0x3B;
                break;
            case '<' :
                data [i] = 0x3C;
                break;
            case '=' :
                data [i] = 0x3D;
                break;
            case '>' :
                data [i] = 0x3E;
                break;
            case '?' :
                data [i] = 0x3F;
                break;
            case 'A' :
                data [i] = 0x41;
                break;
            case 'B' :
                data [i] = 0x42;
                break;
            case 'C' :
                data [i] = 0x43;
                break;
            case 'D' :
                data [i] = 0x44;
                break;
            case 'E' :
                data [i] = 0x45;
                break;
            case 'F' :
                data [i] = 0x46;
                break;
            case 'G' :
                data [i] = 0x47;
                break;
            case 'H' :
                data [i] = 0x48;
                break;
            case 'I' :
                data [i] = 0x49;
                break;
            case 'J' :
                data [i] = 0x4A;
                break;
            case 'K' :
                data [i] = 0x4B;
                break;
            case 'L' :
                data [i] = 0x4C;
                break;
            case 'M' :
                data [i] = 0x4D;
                break;
            case 'N' :
                data [i] = 0x4E;
                break;
            case 'O' :
                data [i] = 0x4F;
                break;
            case 'P' :
                data [i] = 0x50;
                break;
            case 'Q' :
                data [i] = 0x51;
                break;
            case 'R' :
                data [i] = 0x52;
                break;
            case 'S' :
                data [i] = 0x53;
                break;
            case 'T' :
                data [i] = 0x54;
                break;
            case 'U' :
                data [i] = 0x55;
                break;
            case 'V' :
                data [i] = 0x56;
                break;
            case 'W' :
                data [i] = 0x57;
                break;
            case 'X' :
                data [i] = 0x58;
                break;
            case 'Y' :
                data [i] = 0x59;
                break;
            case 'Z' :
                data [i] = 0x5A;
                break;
            case 'Ä' :
                data [i] = 0x5B;
                break;
            case 'Ö' :
                data [i] = 0x5C;
                break;
            case 'Ü' :
                data [i] = 0x5E;
                break;
            case '§' :
                data [i] = 0x5F;
                break;
            case 'a' :
                data [i] = 0x61;
                break;
            case 'b' :
                data [i] = 0x62;
                break;
            case 'c' :
                data [i] = 0x63;
                break;
            case 'd' :
                data [i] = 0x64;
                break;
            case 'e' :
                data [i] = 0x65;
                break;
            case 'f' :
                data [i] = 0x66;
                break;
            case 'g' :
                data [i] = 0x67;
                break;
            case 'h' :
                data [i] = 0x68;
                break;
            case 'i' :
                data [i] = 0x69;
                break;
            case 'j' :
                data [i] = 0x6A;
                break;
            case 'k' :
                data [i] = 0x6B;
                break;
            case 'l' :
                data [i] = 0x6C;
                break;
            case 'm' :
                data [i] = 0x6D;
                break;
            case 'n' :
                data [i] = 0x6E;
                break;
            case 'o' :
                data [i] = 0x6F;
                break;
            case 'p' :
                data [i] = 0x70;
                break;
            case 'q' :
                data [i] = 0x71;
                break;
            case 'r' :
                data [i] = 0x72;
                break;
            case 's' :
                data [i] = 0x73;
                break;
            case 't' :
                data [i] = 0x74;
                break;
            case 'u' :
                data [i] = 0x75;
                break;
            case 'v' :
                data [i] = 0x76;
                break;
            case 'w' :
                data [i] = 0x77;
                break;
            case 'x' :
                data [i] = 0x78;
                break;
            case 'y' :
                data [i] = 0x79;
                break;
            case 'z' :
                data [i] = 0x7A;
                break;
            case 'ä' :
                data [i] = 0x7B;
                break;
            case 'ö' :
                data [i] = 0x7C;
                break;
            case 'ü' :
                data [i] = 0x7E;
                break;
            default :
                data [i] = 0x3F;
                break; // '?'
            } // switch
        } // for
        return data;
    } // convertUnicode2GSM
    
    
    /**
     * Compress a readable text message into the GSM standard alphabet (1
     * character -> 7 bit data)
     * 
     * @param data
     *            text string in Unicode
     * @return text string in GSM standard alphabet
     */
    private static byte [] compress (byte [] data) {
        int l;
        int n; // length of compressed data
        byte [] comp;
        
        // calculate length of message
        l = data.length;
        n = (l * 7) / 8;
        if ( (l * 7) % 8 != 0) {
            n++;
        } // if
        
        comp = new byte [n];
        int j = 0; // index in data
        int s = 0; // shift from next data byte
        for (int i = 0 ; i < n ; i++) {
            comp [i] = (byte) ( (data [j] & 0x7F) >>> s);
            s++;
            if (j + 1 < l) {
                comp [i] += (byte) ( (data [j + 1] << (8 - s)) & 0xFF);
            } // if
            if (s < 7) {
                j++;
            } // if
            else {
                s = 0;
                j += 2;
            } // else
        } // for
        return comp;
    } // compress
    
} // SMSTools