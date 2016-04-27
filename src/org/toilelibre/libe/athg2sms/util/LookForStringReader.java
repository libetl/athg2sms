package org.toilelibre.libe.athg2sms.util;

import java.io.IOException;
import java.io.Reader;

import org.toilelibre.libe.athg2sms.bp.ConvertException;

public class LookForStringReader extends Reader {

    private final Reader reader;

    private String sequence;

    private final StringBuffer sb;

    public LookForStringReader (final Reader r) {
        super ();
        this.reader = r;
        this.sequence = "\n";
        this.sb = new StringBuffer ();
    }

    public LookForStringReader (final Reader reader, final String sequence) {
        super ();
        this.reader = reader;
        this.sequence = sequence;
        this.sb = new StringBuffer ();
    }

    @Override
    public void close () {
        try {
            this.reader.close ();
        } catch (final IOException e) {
            throw new ConvertException ("Cannot happen if the data to be read is a simple String, please change the impl of the Reader", e);
        }
    }

    public String getSequence () {
        return this.sequence;
    }

    @Override
    public int read (final char [] buf, final int offset, final int count) throws IOException {
        return this.reader.read (buf, offset, count);
    }

    public String readUntilNextSequence () {
        this.sb.delete (0, this.sb.length ());
        int firstChar = 0;
        firstChar = this.safeRead ();
        if (firstChar != -1) {
            this.sb.append ((char) firstChar);
        }
        boolean found = false;
        while (!found && (firstChar != -1)) {
            while ( (firstChar != -1) && (firstChar != this.sequence.charAt (0))) {
                firstChar = this.safeRead ();
                this.sb.append ((char) firstChar);
            }
            if (firstChar != -1) {
                int i = 1;
                boolean matching = true;
                int firstWrongChar = -1;
                while ( (i < this.sequence.length ()) && matching) {
                    firstWrongChar = this.safeRead ();
                    matching &= firstWrongChar == this.sequence.charAt (i);
                    i++;
                }
                if (matching) {
                    found = true;
                    this.sb.append (this.sequence.substring (1));
                } else {
                    this.sb.append (this.sequence.substring (1, i - 1));
                    if (firstWrongChar != -1) {
                        this.sb.append ((char) firstWrongChar);
                    }
                    firstChar = firstWrongChar;
                }
            }
        }
        return this.sb.length () > 0 ? this.sb.toString () : null;
    }

    private int safeRead () {
        try {
            return this.reader.read ();
        } catch (final IOException e) {
            e.printStackTrace ();
        }
        return -1;
    }

    public void setSequence (final String sequence) {
        this.sequence = sequence;
    }

}
