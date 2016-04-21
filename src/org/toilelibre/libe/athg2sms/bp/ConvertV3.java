package org.toilelibre.libe.athg2sms.bp;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.toilelibre.libe.athg2sms.pattern.SmsResult;
import org.toilelibre.libe.athg2sms.settings.SettingsFactory;
import org.toilelibre.libe.athg2sms.settings.SettingsV3;
import org.toilelibre.libe.athg2sms.util.LookForMatchReader;


public class ConvertV3 extends Thread implements ConvertThread {
	private static String	 folder	= "content://sms/";

	private InputStream	         f;
	private ConvertListener	 convertListener;
	private Exception	     exception;
	private Object	         handler;
	private int	             inserted;
	private final SettingsV3	settings;

	public ConvertV3 () {
		super ();
		this.inserted = 0;
		this.exception = null;
		this.settings = SettingsFactory.asV3 ();
	}

	private Map<String, Object> buildMessageFromString (String key, SmsResult sms) {
		final Map<String, Object> values = new HashMap<String, Object> ();
		final String format = this.settings.getFormat (key);
		final String pattern = this.settings.getPattern (key);
		final String valPattern = this.settings.getValPattern (key);
		final String value = sms.getCatched ();
		final Pattern pVar = Pattern.compile (pattern);
		final Matcher mVar = pVar.matcher (format);
		final Pattern pVal = Pattern.compile (valPattern);
		final Matcher mVal = pVal.matcher (value);
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

	private String determineSubFolder (SmsResult sms) {
		return sms.getKey ();
	}

	public Exception getException () {
		return this.exception;
	}

	public int getInserted () {
		return this.inserted;
	}

	@Override
	public void run () {
		try {
			this.settings.makePatterns ();
			final InputStreamReader fr = new InputStreamReader (this.f);
			final LookForMatchReader lfsr = new LookForMatchReader (fr,
			        this.settings);
			final List<SmsResult> matcher = new LinkedList<SmsResult> ();
			SmsResult sms = new SmsResult (null, null, null);
			while (sms != null) {
				sms = lfsr.readUntilNextResult ();
				if (sms != null) {
					matcher.add (sms);
				}
				if (this.handler instanceof android.os.Handler)
				((android.os.Handler)this.handler).post (new Runnable () {

					public void run () {
						ConvertV3.this.convertListener
						        .sayIPrepareTheList (matcher.size ());
					}

				});
			}
			final int nb = matcher.size ();
			int nbDuplicate = 0;

			if (this.handler instanceof android.os.Handler)
                ((android.os.Handler)this.handler).post (new Runnable () {

				public void run () {
					ConvertV3.this.convertListener.setMax (nb);
				}
			});

			for (int i = 0; i < matcher.size (); i++) {
				final int dupl = nbDuplicate;
				final int ins = this.inserted;
				sms = matcher.get (i);
				final int i2 = i;
				if (this.handler instanceof android.os.Handler)
	                ((android.os.Handler)this.handler).post (new Runnable () {

					public void run () {
						ConvertV3.this.convertListener.updateProgress (i2, nb);
						ConvertV3.this.convertListener.displayInserted (ins, dupl);
					}

				});

				final String suffix = this.determineSubFolder (sms);
				if (suffix != null) {
					this.inserted++;
					final URI uri = new URI (ConvertV3.folder + suffix + "/");
					final URI uriDelete = new URI (ConvertV3.folder);
					try {
						final Map<String, Object> values = this
						        .buildMessageFromString (suffix, sms);
						
						String where = this.getWhere (values);
						nbDuplicate +=
								this.convertListener.delete (
										uriDelete, where, new String[0]);
						this.convertListener.insert (uri, values);
					} catch (final IllegalStateException ise) {
						this.exception = ise;
					}
				}
			}
			lfsr.close ();
			} catch (final Exception e) {
			this.exception = e;
		}
		if (this.handler instanceof android.os.Handler)
            ((android.os.Handler)this.handler).post (new Runnable () {

			public void run () {
				ConvertV3.this.convertListener.end ();
			}

		});
	}

	private String getWhere (Map<String, Object> values) {
		StringBuffer sb = new StringBuffer ();
		for (Entry<String, Object> entry : values.entrySet ()){
			sb.append (entry.getKey ());
			sb.append (" = ");
			if (entry.getValue () instanceof String){
				sb.append ("'");
				sb.append (entry.getValue ().toString ().replace ("'", "''"));
				sb.append ("'");
			}else{
				sb.append (entry.getValue ());
			}
			sb.append (" and ");
		}
	    return sb.substring (0, sb.length () - " and ".length ());
    }

	public void setConvertListener (ConvertListener cl) {
		this.convertListener = cl;
	}

	public void setInputStream (InputStream f) {
		this.f = f;
	}

	public void setHandler (Object handler) {
		this.handler = handler;
	}

}
