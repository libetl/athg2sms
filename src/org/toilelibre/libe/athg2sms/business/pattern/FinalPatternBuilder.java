package org.toilelibre.libe.athg2sms.business.pattern;

class FinalPatternBuilder {

    private final StringBuffer pattern;
    private final StringBuffer value;
    private final String       format;

    private int                afterLastVar;
    private int                index;

    FinalPatternBuilder(final String format) {
        super ();
        this.index = 0;
        this.format = format;
        this.pattern = new StringBuffer ();
        this.value = new StringBuffer ();
        this.afterLastVar = 0;
    }

    char charAt (final int i) {
        return i >= 0 ? i < this.format.length () ? this.format.charAt (i) : 0 : 0;
    }
    
    String substring (final int i, final int j) {
        return this.format.substring (i, j);
    }

    int getAfterLastVar () {
        return this.afterLastVar;
    }

    StringBuffer getPattern () {
        return this.pattern;
    }

    StringBuffer getValue () {
        return this.value;
    }

    void increment () {
        this.index++;
    }

    int index () {
        return this.index;
    }

    int length () {
        return this.format.length ();
    }

    StringBuffer patternAppendEscape () {
        return this.pattern.append ('\\');
    }

    void setAfterLastVar (final int afterLastVar) {
        this.afterLastVar = afterLastVar;
    }

    StringBuffer valueAppendEscape () {
        return this.value.append ('\\');
    }

}
