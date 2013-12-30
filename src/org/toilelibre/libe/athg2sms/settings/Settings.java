package org.toilelibre.libe.athg2sms.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.toilelibre.libe.athg2sms.bp.ConvertThread;
import org.toilelibre.libe.athg2sms.pattern.MakePatterns;

public abstract class Settings implements SettingsV2, SettingsV3 {

	@Deprecated
	private static String	                        delimiter	= "\n";

	private static Map<String, String>	            formats	    = new HashMap<String, String> ();

	private static ConvertThread	                thread	    = null;

	private static Map<String, String>	            patterns	= new HashMap<String, String> ();

	private static Map<String, Map<String, String>>	sets	    = new HashMap<String, Map<String, String>> ();

	private static Map<String, String>	            valPatterns	= new HashMap<String, String> ();

	static {
		DefaultSettings.load (Settings.sets);
	}

	public void chooseSet (String set) {
		Settings.formats = Settings.sets.get (set);
	}

	public ConvertThread getConvertThreadInstance () {
		Settings.thread = this.getConvertThread ();
		return Settings.thread;

	}

	public String getDelimiter () {
		return Settings.delimiter;
	}

	public String getFormat (String key) {
		return Settings.formats.get (key);
	}

	public String getPattern (String key) {
		return Settings.patterns.get (key);
	}

	public Set<String> getPatternsKeySet () {
		return Settings.patterns.keySet ();
	}

	public Map<String, String> getSet (String set) {
		return Settings.sets.get (set);
	}

	public Map<String, Map<String, String>> getSets () {
		return Settings.sets;
	}

	public Set<String> getSetsKeySet () {
		return Settings.sets.keySet ();
	}

	public String getValPattern (String key) {
		return Settings.valPatterns.get (key);
	}

	public Set<String> getValPatternsKeySet () {
		return Settings.valPatterns.keySet ();
	}

	public void makePatterns () {
		MakePatterns.doAll (Settings.formats, Settings.patterns,
		        Settings.valPatterns);
	}

	public void putSet (String setName, Map<String, String> data) {
		Settings.sets.put (setName, data);
	}

}
