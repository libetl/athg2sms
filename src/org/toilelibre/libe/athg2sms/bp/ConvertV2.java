package org.toilelibre.libe.athg2sms.bp;

import java.io.StringReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.toilelibre.libe.athg2sms.settings.SettingsFactory;
import org.toilelibre.libe.athg2sms.settings.SettingsV2;
import org.toilelibre.libe.athg2sms.util.LookForStringReader;


public class ConvertV2 extends Thread implements ConvertThread {
	private static String	 folder	= "content://sms/";

	private String	         f;
	private ConvertListener	 convertListener;
	private Exception	     exception;
	private android.os.Handler  handler;
	private int	             inserted;
	private final SettingsV2	settings;

	public ConvertV2 () {
		super ();
		this.inserted = 0;
		this.exception = null;
		this.settings = SettingsFactory.asV2 ();
	}

	private Map<String, Object> buildMessageFromString (String key, String line) {
		final Map<String, Object> values = new HashMap<String, Object> ();
		final String format = this.settings.getFormat (key);
		final String pattern = this.settings.getPattern (key);
		final String valPattern = this.settings.getValPattern (key);
		final Pattern pVar = Pattern.compile (pattern);
		final Matcher mVar = pVar.matcher (format);
		final Pattern pVal = Pattern.compile (valPattern);
		final Matcher mVal = pVal.matcher (line);
		DateFormat df = null;
		mVar.find ();
		mVal.find ();
		final int groupCount = mVar.groupCount ();
		for (int i = 1; i <= groupCount; i++) {
			final String var = mVar.group (i);
			final String val = mVal.group (i);
			if (!var.startsWith ("date")) {
				values.put (var, val);
			} else {
				df = new SimpleDateFormat (var.substring ("date".length ()),
				        Locale.US);
				try {
					if (values.get ("date") == null) {
						values.put ("date", df.parse (val).getTime ());
					} else {
						long l = Long.parseLong ("" + values.get ("date"));
						l += df.parse (val).getTime ();
						values.put ("date", l);
					}
				} catch (final ParseException e) {
					e.printStackTrace ();
				}
			}
		}
		return values;
	}

	private String determineSubFolder (String line) {
		boolean found = false;
		final Iterator<String> it = this.settings.getValPatternsKeySet ()
		        .iterator ();
		String patternKey = null;

		while (it.hasNext () && !found) {
			patternKey = it.next ();
			final String patternString = this.settings
			        .getValPattern (patternKey);
			found |= Pattern.matches (patternString, line);
		}
		return found ? patternKey : null;
	}

	public ConvertException getException () {
        return new ConvertException ("exception", this.exception);
	}

	public int getInserted () {
		return this.inserted;
	}

	@Override
	public void run () {
		try {
			this.settings.makePatterns ();
			final StringReader fr = new StringReader (this.f);
			final LookForStringReader lfsr = new LookForStringReader (fr,
			        this.settings.getDelimiter ());
			final List<String> texts = new LinkedList<String> ();
			String text = "dummy";
			while (text != null) {
				text = lfsr.readUntilNextSequence ();
				if (text != null) {
					texts.add (text);
				}
				this.handler.post (new Runnable () {

					public void run () {
						ConvertV2.this.convertListener
						        .sayIPrepareTheList (texts.size ());
					}

				});
			}
			final int nb = texts.size ();

			this.handler.post (new Runnable () {

				public void run () {
					ConvertV2.this.convertListener.setMax (nb);
				}
			});

			for (int i = 0; i < texts.size (); i++) {
				final int ins = this.inserted;
				text = texts.get (i);
				final int i2 = i;
				this.handler.post (new Runnable () {

					public void run () {
						ConvertV2.this.convertListener.updateProgress (i2, nb);
						ConvertV2.this.convertListener.displayInserted (ins, 0);
					}

				});

				final String suffix = this.determineSubFolder (text);
				if (suffix != null) {
					this.inserted++;
					final String folderMsg = ConvertV2.folder + suffix;
					try {
						final Map<String, Object> values = this
						        .buildMessageFromString (suffix, text);

						this.convertListener.insert (
						        new URI (folderMsg), values);
					} catch (final IllegalStateException ise) {
						this.exception = ise;
					}
				}
			}
			lfsr.close ();
		} catch (final Exception e) {
			this.exception = e;
		}
		this.handler.post (new Runnable () {

			public void run () {
				ConvertV2.this.convertListener.end ();
			}

		});
	}

	public void setConvertListener (ConvertListener cl) {
		this.convertListener = cl;
	}

	public void setContentToBeParsed (String f) {
		this.f = f;
	}

	public void setHandler (Object handler) {
		this.handler = (android.os.Handler)handler;
	}

}
