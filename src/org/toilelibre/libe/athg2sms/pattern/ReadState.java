package org.toilelibre.libe.athg2sms.pattern;

public class ReadState {

    private final StringBuffer pattern;
    private final StringBuffer value;
    private final String       format;

    private boolean            inBrackets;
    private int                afterLastVar;
    private int                index;

    public ReadState (final String format) {
        super ();
        this.index = 0;
        this.format = format;
        this.pattern = new StringBuffer ();
        this.value = new StringBuffer ();
        this.inBrackets = false;
        this.afterLastVar = 0;
    }

    public char charAt (final int i) {
        return i >= 0 ? i < this.format.length () ? this.format.charAt (i) : 0 : 0;
    }

    public int getAfterLastVar () {
        return this.afterLastVar;
    }

    public String getFormat () {
        return this.format;
    }

    public StringBuffer getPattern () {
        return this.pattern;
    }

    public StringBuffer getValue () {
        return this.value;
    }

    public void increment () {
        this.index++;
    }

    public int index () {
        return this.index;
    }

    public boolean isInBrackets () {
        return this.inBrackets;
    }

    public int length () {
        return this.format.length ();
    }

    public StringBuffer patternAppendEscape () {
        return this.pattern.append ('\\');
    }

    public void setAfterLastVar (final int afterLastVar) {
        this.afterLastVar = afterLastVar;
    }

    public void setInBrackets (final boolean inBrackets) {
        this.inBrackets = inBrackets;
    }

    public StringBuffer valueAppendEscape () {
        return this.value.append ('\\');
    }

    @Override
    public String toString () {
        return this.value.toString ();
    }

}
