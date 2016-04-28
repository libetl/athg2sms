package org.toilelibre.libe.athg2sms.util;

public class StringUtils {
    public static final int     INDEX_NOT_FOUND = -1;
    private static final String EMPTY           = "";
    
    public static String difference (final String str1, final String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        final int at = indexOfDifference (str1, str2);
        if (at == INDEX_NOT_FOUND) {
            return EMPTY;
        }
        return str2.substring (at);
    }
    
    public static String reverse(final String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }
    
    public static int indexOfDifference (final CharSequence... css) {
        if (css == null || css.length <= 1) {
            return INDEX_NOT_FOUND;
        }
        boolean anyStringNull = false;
        boolean allStringsNull = true;
        final int arrayLen = css.length;
        int shortestStrLen = Integer.MAX_VALUE;
        int longestStrLen = 0;
        
        // find the min and max string lengths; this avoids checking to make
        // sure we are not exceeding the length of the string each time through
        // the bottom loop.
        for (int i = 0 ; i < arrayLen ; i++) {
            if (css [i] == null) {
                anyStringNull = true;
                shortestStrLen = 0;
            } else {
                allStringsNull = false;
                shortestStrLen = Math.min (css [i].length (), shortestStrLen);
                longestStrLen = Math.max (css [i].length (), longestStrLen);
            }
        }
        
        // handle lists containing all nulls or all empty strings
        if (allStringsNull || longestStrLen == 0 && !anyStringNull) {
            return INDEX_NOT_FOUND;
        }
        
        // handle lists containing some nulls or some empty strings
        if (shortestStrLen == 0) {
            return 0;
        }
        
        // find the position with the first difference across all strings
        int firstDiff = -1;
        for (int stringPos = 0 ; stringPos < shortestStrLen ; stringPos++) {
            final char comparisonChar = css [0].charAt (stringPos);
            for (int arrayPos = 1 ; arrayPos < arrayLen ; arrayPos++) {
                if (css [arrayPos].charAt (stringPos) != comparisonChar) {
                    firstDiff = stringPos;
                    break;
                }
            }
            if (firstDiff != -1) {
                break;
            }
        }
        
        if (firstDiff == -1 && shortestStrLen != longestStrLen) {
            // we compared all of the characters up to the length of the
            // shortest string and didn't find a match, but the string lengths
            // vary, so return the length of the shortest string.
            return shortestStrLen;
        }
        return firstDiff;
    }
    

    public static String intersection(String s1, String s2) {

       for( int i = Math.min(s1.length(), s2.length()); ; i--) {
          if(s2.endsWith(s1.substring(0, i))) {
             return s1.substring(0, i);
          }
       }    
    }
    
    public static int[] commonPartRanges (String a, String b) {
        String diff1End = StringUtils.difference (a, b);
        String diff2End = StringUtils.difference (b, a);
        String diff1Start = StringUtils.reverse (StringUtils.difference (StringUtils.reverse (a), StringUtils.reverse (b)));
        String diff2Start = StringUtils.reverse (StringUtils.difference (StringUtils.reverse (b), StringUtils.reverse (a)));
        String diff1 = StringUtils.intersection (diff1End, diff1Start);
        String diff2 = StringUtils.intersection (diff2End, diff2Start);
        int [] range = new int [4];
        range [0] = diff2Start.length () - diff2.length ();
        range [1] = diff2Start.length ();
        range [2] = diff1Start.length () - diff1.length ();
        range [3] = diff1Start.length ();
        return range;
    }
}