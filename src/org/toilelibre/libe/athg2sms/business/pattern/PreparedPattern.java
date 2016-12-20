package org.toilelibre.libe.athg2sms.business.pattern;

public class PreparedPattern {
    private final String pattern;
    private final String valPattern;
    private final Format.FormatRegexRepresentation formatRegexRepresentation;

    PreparedPattern(Format.FormatRegexRepresentation formatRegexRepresentation, final String pattern, final String valPattern) {
        this.formatRegexRepresentation = formatRegexRepresentation;
        this.pattern = pattern;
        this.valPattern = valPattern;
    }

    public String getPattern () {
        return pattern;
    }
    public String getValPattern () {
        return valPattern;
    }
    public Format.FormatRegexRepresentation getFormatRegexRepresentation() {
        return formatRegexRepresentation;
    }

    public static PreparedPattern fromFormat(Format.FormatRegexRepresentation formatRegexRepresentation) {
        return new PatternsMaker().makePreparedPattern(formatRegexRepresentation);
    }
}