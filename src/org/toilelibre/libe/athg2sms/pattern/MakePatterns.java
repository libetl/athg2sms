package org.toilelibre.libe.athg2sms.pattern;

import java.util.Map;

public class MakePatterns {

	private static String	varPattern	= "\\$\\(([^\\)]+)\\)";

	private static void addVariable (ReadState rs) {
		while ( (rs.index () < rs.length ())
		        && ( (rs.charAt (rs.index ()) != ')') || (rs.charAt (rs
		                .index () - 1) == '\\'))) {
			rs.increment ();
		}
		rs.setAfterLastVar (rs.index () + 1);
		final char expectedChar = rs.charAt (rs.getAfterLastVar ());
		if (rs.index () < rs.length ()) {
			rs.getPattern ().append (MakePatterns.varPattern);
			if (expectedChar >= 0) {
				rs.getValue ().append ("((?:[^" + expectedChar + "]" + (expectedChar == '"' ? "|\"\"" : (expectedChar == '\'' ? "|''" : "")) + ")*)");
			}
		}

	}

	private static void atIndex (ReadState rs) {

		switch (rs.charAt (rs.index ())) {
			case '$':
				switch (rs.charAt (rs.index () + 1)) {
					case '(':
						MakePatterns.addVariable (rs);
						break;
					default:
						MakePatterns.specialChar (rs);
						MakePatterns.defaultBehavior (rs);
						break;
				}
				break;
			case ']':
				MakePatterns.recognizePattern (rs);
				break;
			case '[':
				rs.setInBrackets (true);
			case '\'':
			case '"':
			case '(':
			case ')':
			case '^':
			case '.':
			case '*':
			case '+':
			case '?':
				MakePatterns.specialChar (rs);
			default:
				MakePatterns.defaultBehavior (rs);
				break;
		}
		rs.increment ();

	}

	private static void defaultBehavior (ReadState rs) {
		rs.getPattern ().append (rs.charAt (rs.index ()));
		rs.getValue ().append (rs.charAt (rs.index ()));

	}

	public static void doAll (Map<String, String> formats,
	        Map<String, String> patterns, Map<String, String> valPatterns) {
		for (final String key : formats.keySet ()) {
			MakePatterns.doOne (key, formats.get (key), patterns, valPatterns);
		}
	}

	private static void doOne (String key, String format,
	        Map<String, String> patterns, Map<String, String> valPatterns) {
		final ReadState rs = new ReadState (format);

		while (rs.index () < format.length ()) {
			MakePatterns.atIndex (rs);
		}
		patterns.put (key, rs.getPattern ().toString ().trim ());
		valPatterns.put (key, rs.getValue ().toString ().trim ());

		if (format != null) {
			format.substring (rs.getAfterLastVar ());
		}

	}

	private static void recognizePattern (ReadState rs) {
		rs.setInBrackets (false);
		rs.patternAppendEscape ();
		MakePatterns.defaultBehavior (rs);
		rs.increment ();
		rs.setAfterLastVar (rs.index ());
		if ( (rs.charAt (rs.index ()) == '+')
		        || (rs.charAt (rs.index ()) == '*')
		        || (rs.charAt (rs.index ()) == '?')) {
			rs.patternAppendEscape ();
			MakePatterns.defaultBehavior (rs);
			rs.setAfterLastVar (rs.index () + 1);
		}

	}

	private static void specialChar (ReadState rs) {
		rs.patternAppendEscape ();
		if (!rs.isInBrackets ()) {
			rs.valueAppendEscape ();
		}

	}
}
