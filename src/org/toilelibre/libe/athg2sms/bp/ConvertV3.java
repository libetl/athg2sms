package org.toilelibre.libe.athg2sms.bp;

import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.toilelibre.libe.athg2sms.pattern.SmsResult;
import org.toilelibre.libe.athg2sms.settings.SettingsFactory;
import org.toilelibre.libe.athg2sms.settings.SettingsV3;
import org.toilelibre.libe.athg2sms.util.LookForMatchReader;

import android.content.ContentValues;
import android.content.SyncRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

public class ConvertV3 extends Thread implements ConvertThread {
	private static String	 folder	= "content://sms/";

	private File	         f;
	private ConvertListener	 convertListener;
	private Exception	     exception;
	private Handler	         handler;
	private int	             inserted;
	private final SettingsV3	settings;

	public ConvertV3 () {
		super ();
		this.inserted = 0;
		this.exception = null;
		this.settings = SettingsFactory.asV3 ();
	}

	private ContentValues buildMessageFromString (String key, SmsResult sms) {
		final ContentValues values = new ContentValues ();
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
						long l = values.getAsLong ("date");
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
			final FileReader fr = new FileReader (this.f);
			final LookForMatchReader lfsr = new LookForMatchReader (fr, this.settings);
			final List<SmsResult> matcher = new LinkedList<SmsResult> ();
			SmsResult sms = new SmsResult (null, null, null);
			while (sms != null) {
				sms = lfsr.readUntilNextResult ();
				if (sms != null) {
					matcher.add (sms);
				}
				this.handler.post (new Runnable () {

					public void run () {
						ConvertV3.this.convertListener
						        .sayIPrepareTheList (matcher.size ());
					}

				});
			}
			final int nb = matcher.size ();

			this.handler.post (new Runnable () {

				public void run () {
					ConvertV3.this.convertListener.setMax (nb);
				}
			});

            Object requestSync = null;

            for (int i = 0; i < matcher.size (); i++) {
				final int ins = this.inserted;
				sms = matcher.get (i);
				final int i2 = i;
				this.handler.post (new Runnable () {

					public void run () {
						ConvertV3.this.convertListener.updateProgress (i2, nb);
						ConvertV3.this.convertListener.displayInserted (ins);
					}

				});

				final String suffix = this.determineSubFolder (sms);
				if (suffix != null) {
					this.inserted++;
					final String folderMsg = ConvertV3.folder + suffix;
					try {
						final ContentValues values = this
						        .buildMessageFromString (suffix, sms);

						this.convertListener.getContentResolver ().insert (
						        Uri.parse (folderMsg), values);
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
				ConvertV3.this.convertListener.end ();
			}

		});
	}

	public void setConvertListener (ConvertListener cl) {
		this.convertListener = cl;
	}

	public void setFile (File f) {
		this.f = f;
	}

	public void setHandler (Handler handler) {
		this.handler = handler;
	}

}
