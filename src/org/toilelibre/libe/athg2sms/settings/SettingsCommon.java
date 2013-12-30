package org.toilelibre.libe.athg2sms.settings;

import java.util.Map;
import java.util.Set;

import org.toilelibre.libe.athg2sms.bp.ConvertThread;

public interface SettingsCommon {

	public void chooseSet (String set);

	public ConvertThread getConvertThread ();

	public ConvertThread getConvertThreadInstance ();

	public String getFormat (String key);

	public String getPattern (String key);

	public Set<String> getPatternsKeySet ();

	public Map<String, String> getSet (String set);

	public Map<String, Map<String, String>> getSets ();

	public Set<String> getSetsKeySet ();

	public String getValPattern (String key);

	public Set<String> getValPatternsKeySet ();

	public void putSet (String setName, Map<String, String> data);
}
