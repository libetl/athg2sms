package org.toilelibre.libe.athg2sms.bp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.os.Handler;

public class ConvertV1 extends Thread implements ConvertThread {

	private String          f;
	private ConvertListener	activity;
	private Exception	    exception;
	private Handler	        handler;

	public ConvertV1 () {
		super ();
		this.exception = null;
	}

	public ConvertException getException () {
		return new ConvertException ("exception", this.exception);
	}

	public int getInserted () {
		return -1;
	}

	@Override
	public void run () {

	    StringReader fr = null;
		BufferedReader br = null;
		try {

			fr = new StringReader (this.f);
			br = new BufferedReader (fr);
			int i = 0;
			int nb = 0;
			while (br.readLine () != null) {
				nb++;
			}

			final int nb2 = nb;
			this.handler.post (new Runnable () {

				public void run () {
					ConvertV1.this.activity.setMax (nb2);
				}

			});
			br = new BufferedReader (fr);
			final SimpleDateFormat df = new SimpleDateFormat (
			        "yyyy.MM.dd hh:mm", Locale.US);
			fr = new StringReader (this.f);
			br = new BufferedReader (fr);
			String line = br.readLine ();
			while (line != null) {
				final int i2 = i;
				this.handler.post (new Runnable () {

					public void run () {
						ConvertV1.this.activity.updateProgress (i2, nb2);
					}

				});
				String folder = "content://sms/";

				if (line.startsWith ("sms;deliver")) {
					folder += "inbox";
					line = line.replace ("sms;deliver;\"", "");
				} else if (line.startsWith ("sms;submit")) {
					folder += "sent";
					line = line.replace ("sms;submit;\"\";\"", "");
					line = line.replace ("sms;submit;\"", "");
				}
				final String [] message = line.split ("\";\"");

				if ( (message.length > 0)
				        && (message [0].startsWith ("0") || message [0]
				                .startsWith ("+"))) {
					final Map<String, Object> values = new HashMap<String, Object> ();
					values.put ("address", message [0]);
					values.put ("date", df.parse (message [3]).getTime ());
					values.put ("body", message [5].substring (0,
					        message [5].length () - 1));
					this.activity.insert (
					        new URI (folder), values);
				}

				line = br.readLine ();
				i++;

			}
		} catch (final Exception e) {
			this.exception = e;
		} finally {
			if (br != null) {
				try {
					br.close ();
				} catch (final IOException e) {
					e.printStackTrace ();
				}
			}
			if (fr != null) {
				fr.close ();
			}
		}
		this.handler.post (new Runnable () {

			public void run () {
				ConvertV1.this.activity.end ();
			}

		});
	}

	public void setConvertListener (ConvertListener activity) {
		this.activity = activity;
	}

	public void setContentToBeParsed (String f) {
		this.f = f;
	}

	public void setHandler (Object handler) {
		this.handler = (android.os.Handler) handler;
	}
}
