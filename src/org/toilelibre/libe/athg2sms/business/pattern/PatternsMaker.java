package org.toilelibre.libe.athg2sms.business.pattern;

class PatternsMaker {

    private static final String VAR_PATTERN = "\\$\\(([^\\)]+)\\)";

    private void addVariable (final FinalPatternBuilder patternBuilder) {
        int startOfVarName = patternBuilder.index() + 2;
        while (patternBuilder.index () < patternBuilder.length () && (patternBuilder.charAt (patternBuilder.index ()) != ')' || patternBuilder.charAt (patternBuilder.index () - 1) == '\\')) {
            patternBuilder.increment ();
        }
        String varName = patternBuilder.substring (startOfVarName, patternBuilder.index ());
        String endToken = varName.contains ("..") ? varName.substring (varName.indexOf ("..") + 2) : "";
        patternBuilder.setAfterLastVar (patternBuilder.index () + 1);
        final char firstExpectedChar = patternBuilder.charAt (patternBuilder.getAfterLastVar ());
        String expectedChar = "" + (firstExpectedChar == 0 ? "" : firstExpectedChar);
        if (expectedChar.length() > 0 && expectedChar.charAt (0) == '\\') {
            expectedChar += patternBuilder.charAt (patternBuilder.getAfterLastVar () + 1);
        }
        if (patternBuilder.index () < patternBuilder.length ()) {
            patternBuilder.getPattern ().append (PatternsMaker.VAR_PATTERN);
            if (!"".equals (endToken)){
                patternBuilder.getValue ().append ("((?:.|\\s)*?(?=").append(endToken).append("))");
            }else if (!"".equals (expectedChar)) {
                patternBuilder.getValue ().append ("((?:[^").append(expectedChar).append("]").append(firstExpectedChar == '"' ? "|\"\"" : firstExpectedChar == '\'' ? "|''" : "").append(")*)");
            }
        }

    }

    private void atIndex (final FinalPatternBuilder patternBuilder) {

        switch (patternBuilder.charAt (patternBuilder.index ())) {
            case '$' :
                switch (patternBuilder.charAt (patternBuilder.index () + 1)) {
                    case '(' :
                        this.addVariable (patternBuilder);
                        break;
                    default :
                        this.specialChar (patternBuilder);
                        this.defaultBehavior (patternBuilder);
                        break;
                }
                break;
            default :
                this.defaultBehavior (patternBuilder);
                break;
        }
        patternBuilder.increment ();

    }

    private void defaultBehavior (final FinalPatternBuilder patternBuilder) {
        patternBuilder.getPattern ().append (patternBuilder.charAt (patternBuilder.index ()));
        patternBuilder.getValue ().append (patternBuilder.charAt (patternBuilder.index ()));

    }

    private void specialChar (final FinalPatternBuilder patternBuilder) {
        patternBuilder.patternAppendEscape ();
        patternBuilder.valueAppendEscape ();
    }

    PreparedPattern makePreparedPattern(final Format.FormatRegexRepresentation formatRegexRepresentation) {
        final String formatCommonRegex = formatRegexRepresentation.getCommonRegex();
        final FinalPatternBuilder patternBuilder = new FinalPatternBuilder(formatCommonRegex);

        while (patternBuilder.index () < formatCommonRegex.length ()) {
            this.atIndex (patternBuilder);
        }
        return new PreparedPattern(formatRegexRepresentation, patternBuilder.getPattern ().toString ().trim (), patternBuilder.getValue ().toString ().trim ());
    }
}
