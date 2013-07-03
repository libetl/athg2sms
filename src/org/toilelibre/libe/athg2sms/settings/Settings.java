package org.toilelibre.libe.athg2sms.settings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.toilelibre.libe.athg2sms.bp.ConvertThread;

public class Settings {

    private static String                           convertThreadClass = ConvertThread.class
                                                                               .getPackage ()
                                                                            .getName ()
                                                                               + ".ConvertV2";

    private static ConvertThread                    instance           = null;

    private static Map<String, String>              formats            = new HashMap<String, String> ();

    private static Map<String, String>              patterns           = new HashMap<String, String> ();

    private static Map<String, String>              valPatterns        = new HashMap<String, String> ();

    private static String                           varPattern         = "\\$\\(([^\\)]+)\\)";

    private static String                           delimiter          = "\n";

    private static Map<String, Map<String, String>> sets               = new HashMap<String, Map<String, String>> ();

    static {
        DefaultSettings.load (Settings.sets);
    }

    public static void chooseSet (String set) {
        Settings.formats = Settings.sets.get (set);
    }

    public static ConvertThread getConvertThreadInstance () {
        try {
            Settings.instance = (ConvertThread) Class.forName (
                    Settings.convertThreadClass).newInstance ();
        } catch (IllegalAccessException e) {
            e.printStackTrace ();
        } catch (InstantiationException e) {
            e.printStackTrace ();
        } catch (ClassNotFoundException e) {
            e.printStackTrace ();
        }
        return Settings.instance;

    }

    public static String getDelimiter () {
        return Settings.delimiter;
    }

    public static String getFormat (String key) {
        return Settings.formats.get (key);
    }

    public static String getPattern (String key) {
        return Settings.patterns.get (key);
    }

    public static Iterator<String> getPatternsIterator () {
        return Settings.patterns.keySet ().iterator ();
    }

    public static Map<String, String> getSet (String set) {
        return Settings.sets.get (set);
    }

    public static Map<String, Map<String, String>> getSets () {
        return Settings.sets;
    }

    public static Set<String> getSetsKeySet () {
        return Settings.sets.keySet ();
    }

    public static String getValPattern (String key) {
        return Settings.valPatterns.get (key);
    }

    public static Iterator<String> getValPatternsIterator () {
        return Settings.valPatterns.keySet ().iterator ();
    }

    public static void makePatterns () {
        int afterLastVar = 0;
        String format = null;
        boolean inBrackets = false;
        for (String key : Settings.formats.keySet ()) {
            format = Settings.formats.get (key);
            StringBuffer sb = new StringBuffer ();
            StringBuffer sbV = new StringBuffer ();
            int i = 0;
            while (i < format.length ()) {
                switch (format.charAt (i)) {
                    case '$':
                        if (i + 1 == format.length ()
                                || format.charAt (i + 1) != '(') {
                            sb.append ("\\$");
                            sbV.append ("\\$");
                        } else if (i + 1 < format.length ()) {
                            while (i < format.length ()
                                    && format.charAt (i) != ')') {
                                i++ ;
                            }
                            afterLastVar = i + 1;
                            char expectedChar = format.charAt (afterLastVar);
                            if (i < format.length ()) {
                                sb.append (Settings.varPattern);
                                if (expectedChar >= 0) {
                                        sbV.append ("([^"
                                                + expectedChar
                                                + "]*)");
                                }
                            }
                        }
                        break;
                    case ']':
                        inBrackets = false;
                        sb.append ('\\');
                        sb.append (format.charAt (i));
                        sbV.append (format.charAt (i));
                        i++;
                        afterLastVar = i;
                        if (i < format.length () && 
                                (format.charAt (i) == '+' ||
                                 format.charAt (i) == '*')){
                          sb.append ('\\');
                          sb.append (format.charAt (i));
                          sbV.append (format.charAt (i));
                          afterLastVar = i + 1;
                        }
                        inBrackets = false;
                        break;
                    case '[':inBrackets = true;
                    case '\'':
                    case '"':
                    case '(':
                    case ')':
                    case '^':
                    case '.':
                    case '*':
                    case '+':
                          sb.append ('\\');
                          if (!inBrackets){
                            sbV.append ('\\');
                          }
                    default:
                        sb.append (format.charAt (i));
                        sbV.append (format.charAt (i));
                        break;
                }
                i++ ;
            }
            Settings.patterns.put (key, sb.toString ());
            Settings.valPatterns.put (key, sbV.toString ());
        }
        if (format != null) {
            String del = format.substring (afterLastVar);
            Settings.delimiter = del;
        }
    }

    public static void putSet (String setName, Map<String, String> data) {
        Settings.sets.put (setName, data);
    }

}
