package org.toilelibre.libe.athg2sms.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.toilelibre.libe.athg2sms.bp.ConvertThread;
import org.toilelibre.libe.athg2sms.bp.ConvertV1;

public class SettingsV1 implements SettingsCommon {

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
		DefaultSettings.load (SettingsV1.sets);
	}

	public void chooseSet (String set) {
		SettingsV1.formats = SettingsV1.sets.get (set);
	}

	public ConvertThread getConvertThread () {
		return new ConvertV1 ();
	}

	public ConvertThread getConvertThreadInstance () {
		try {
			SettingsV1.instance = (ConvertThread) Class.forName (
			        SettingsV1.convertThreadClass).newInstance ();
		} catch (final IllegalAccessException e) {
			e.printStackTrace ();
		} catch (final InstantiationException e) {
			e.printStackTrace ();
		} catch (final ClassNotFoundException e) {
			e.printStackTrace ();
		}
		return SettingsV1.instance;

	}

	public String getDelimiter () {
		return SettingsV1.delimiter;
	}

	public String getFormat (String key) {
		return SettingsV1.formats.get (key);
	}

	public String getPattern (String key) {
		return SettingsV1.patterns.get (key);
	}

	public Set<String> getPatternsKeySet () {
		return SettingsV1.patterns.keySet ();
	}

	public Map<String, String> getSet (String set) {
		return SettingsV1.sets.get (set);
	}

	public Map<String, Map<String, String>> getSets () {
		return SettingsV1.sets;
	}

	public Set<String> getSetsKeySet () {
		return SettingsV1.sets.keySet ();
	}

	public String getValPattern (String key) {
		return SettingsV1.valPatterns.get (key);
	}

	public Set<String> getValPatternsKeySet () {
		return SettingsV1.valPatterns.keySet ();
	}

	public void makePatterns () {
		int afterLastVar = 0;
		String format = null;
		boolean inBrackets = false;
		for (final String key : SettingsV1.formats.keySet ()) {
			format = SettingsV1.formats.get (key);
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
								sb.append (SettingsV1.varPattern);
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
			SettingsV1.patterns.put (key, sb.toString ());
			SettingsV1.valPatterns.put (key, sbV.toString ());
		}
		if (format != null) {
			final String del = format.substring (afterLastVar);
			SettingsV1.delimiter = del;
		}
	}

	public void putSet (String setName, Map<String, String> data) {
		SettingsV1.sets.put (setName, data);
	}

}
