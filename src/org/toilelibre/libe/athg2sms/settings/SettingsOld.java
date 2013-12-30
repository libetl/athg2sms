package org.toilelibre.libe.athg2sms.settings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.toilelibre.libe.athg2sms.bp.ConvertThread;

public class SettingsOld {

	private static String	                        convertThreadClass	= ConvertThread.class
	                                                                           .getPackage ()
	                                                                           .getName ()
	                                                                           + ".ConvertV3";

	private static ConvertThread	                instance	       = null;

	private static Map<String, String>	            formats	           = new HashMap<String, String> ();

	private static Map<String, String>	            patterns	       = new HashMap<String, String> ();

	private static Map<String, String>	            valPatterns	       = new HashMap<String, String> ();

	private static String	                        varPattern	       = "\\$\\(([^\\)]+)\\)";

	private static String	                        delimiter	       = "\n";

	private static Map<String, Map<String, String>>	sets	           = new HashMap<String, Map<String, String>> ();

	static {
		DefaultSettings.load (SettingsOld.sets);
	}

	public static void chooseSet (String set) {
		SettingsOld.formats = SettingsOld.sets.get (set);
	}

	public static ConvertThread getConvertThreadInstance () {
		try {
			SettingsOld.instance = (ConvertThread) Class.forName (
			        SettingsOld.convertThreadClass).newInstance ();
		} catch (final IllegalAccessException e) {
			e.printStackTrace ();
		} catch (final InstantiationException e) {
			e.printStackTrace ();
		} catch (final ClassNotFoundException e) {
			e.printStackTrace ();
		}
		return SettingsOld.instance;

	}

	public static String getDelimiter () {
		return SettingsOld.delimiter;
	}

	public static String getFormat (String key) {
		return SettingsOld.formats.get (key);
	}

	public static String getPattern (String key) {
		return SettingsOld.patterns.get (key);
	}

	public static Iterator<String> getPatternsIterator () {
		return SettingsOld.patterns.keySet ().iterator ();
	}

	public static Map<String, String> getSet (String set) {
		return SettingsOld.sets.get (set);
	}

	public static Map<String, Map<String, String>> getSets () {
		return SettingsOld.sets;
	}

	public static Set<String> getSetsKeySet () {
		return SettingsOld.sets.keySet ();
	}

	public static String getValPattern (String key) {
		return SettingsOld.valPatterns.get (key);
	}

	public static Iterator<String> getValPatternsIterator () {
		return SettingsOld.valPatterns.keySet ().iterator ();
	}

	public static void makePatterns () {
		int afterLastVar = 0;
		String format = null;
		boolean inBrackets = false;
		for (final String key : SettingsOld.formats.keySet ()) {
			format = SettingsOld.formats.get (key);
			final StringBuffer sb = new StringBuffer ();
			final StringBuffer sbV = new StringBuffer ();
			int i = 0;
			while (i < format.length ()) {
				switch (format.charAt (i)) {
					case '$':
						if ( ( (i + 1) == format.length ())
						        || (format.charAt (i + 1) != '(')) {
							sb.append ("\\$");
							sbV.append ("\\$");
						} else if ( (i + 1) < format.length ()) {
							while ( (i < format.length ())
							        && (format.charAt (i) != ')')) {
								i++;
							}
							afterLastVar = i + 1;
							final char expectedChar = format
							        .charAt (afterLastVar);
							if (i < format.length ()) {
								sb.append (SettingsOld.varPattern);
								if (expectedChar >= 0) {
									sbV.append ("([^" + expectedChar + "]*)");
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
						if ( (i < format.length ())
						        && ( (format.charAt (i) == '+') || (format
						                .charAt (i) == '*'))) {
							sb.append ('\\');
							sb.append (format.charAt (i));
							sbV.append (format.charAt (i));
							afterLastVar = i + 1;
						}
						inBrackets = false;
						break;
					case '[':
						inBrackets = true;
					case '\'':
					case '"':
					case '(':
					case ')':
					case '^':
					case '.':
					case '*':
					case '+':
						sb.append ('\\');
						if (!inBrackets) {
							sbV.append ('\\');
						}
					default:
						sb.append (format.charAt (i));
						sbV.append (format.charAt (i));
						break;
				}
				i++;
			}
			SettingsOld.patterns.put (key, sb.toString ());
			SettingsOld.valPatterns.put (key, sbV.toString ());
		}
		if (format != null) {
			final String del = format.substring (afterLastVar);
			SettingsOld.delimiter = del;
		}
	}

	public static void putSet (String setName, Map<String, String> data) {
		SettingsOld.sets.put (setName, data);
	}

}
