package org.toilelibre.libe.athg2sms.business.pattern;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Format {

    private static final Pattern VARIABLE_PATTERN  = Pattern.compile ("\\$\\(([^)]+)\\)");
    private final String name;
    private final FormatVarNamesRepresentation varNames;
    private final FormatRegexRepresentation regex;

    public Format(final String formatName, final String regexp, final String exportFormat, final String inboxKeyword, final String sentKeyword) {
        this(formatName, new FormatVarNamesRepresentation(regexp), new FormatRegexRepresentation(regexp, inboxKeyword, sentKeyword, exportFormat));
    }

    private Format(String name, FormatVarNamesRepresentation varNames, FormatRegexRepresentation regex) {
        this.name = name;
        this.varNames = varNames;
        this.regex = regex;
    }

    public String getName() {
        return name;
    }

    public FormatVarNamesRepresentation getVarNames() {
        return varNames;
    }

    public FormatRegexRepresentation getRegex() {
        return regex;
    }

    public static class FormatRegexRepresentation {

        private final String commonRegex;
        private final String inboxKeyword;
        private final String sentKeyword;
        private final String exportFormat;
        private final int indexOfFolderCapturingGroup;

        FormatRegexRepresentation(String commonRegex, String inboxKeyword, String sentKeyword, String exportFormat) {
            this.commonRegex = commonRegex;
            this.inboxKeyword = inboxKeyword;
            this.sentKeyword = sentKeyword;
            this.exportFormat = exportFormat;
            this.indexOfFolderCapturingGroup = findIndexOfFolderCapturingGroup(commonRegex);
        }

        private int findIndexOfFolderCapturingGroup(String regexp) {
            final Matcher findVariablesNames = VARIABLE_PATTERN.matcher (regexp);
            int index = 1;
            while (findVariablesNames.find ()) {
                String fullVarNameWithEndToken = findVariablesNames.group (1);
                String realVariableName = !fullVarNameWithEndToken.contains("..") ? fullVarNameWithEndToken :
                        fullVarNameWithEndToken.substring (0, fullVarNameWithEndToken.indexOf (".."));
                if ("folder".equals (realVariableName)) {
                    return index;
                }
                index++;
            }
            throw new MissingFormatArgumentException("There should be a folder var inside the format pattern ! Please try again.");
        }

        public String getCommonRegex() {
            return commonRegex;
        }

        public String getInboxKeyword() {
            return inboxKeyword;
        }

        public String getSentKeyword() {
            return sentKeyword;
        }

        public String getExportFormat() {
            return exportFormat;
        }

        public int getIndexOfFolderCapturingGroup() {
            return indexOfFolderCapturingGroup;
        }
    }

    public static class FormatVarNamesRepresentation {
        private final List<String> varNames;

        FormatVarNamesRepresentation(String regexAsString) {
            this.varNames = new LinkedList<>();
            final Matcher findVariablesNames = VARIABLE_PATTERN.matcher (regexAsString);
            while (findVariablesNames.find ()) {
                String fullVarNameWithEndToken = findVariablesNames.group (1);
                String realVariableName = !fullVarNameWithEndToken.contains("..") ? fullVarNameWithEndToken :
                        fullVarNameWithEndToken.substring (0, fullVarNameWithEndToken.indexOf (".."));
                varNames.add (realVariableName);
            }
        }

        public List<String> getVarNames() {
            return Collections.unmodifiableList(varNames);
        }

        public int size() {
            return this.varNames.size();
        }
    }
}
