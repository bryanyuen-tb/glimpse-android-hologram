package org.glimpseframework.android.hologram;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Raw Android resource loader.
 *
 * @author Sławomir Czerwiński
 */
class RawResourceLoader {

	private static final String LOG_TAG = "RawResourceLoader";

	public RawResourceLoader(Context context) {
		this.context = context;
	}

	public String loadResource(int id) {
		InputStream in = null;
		try {
			in = context.getResources().openRawResource(id);
			byte[] bytes = new byte[in.available()];
			in.read(bytes);
			in.close();
			return new String(bytes);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error loading raw resource", e);
			try {
				in.close();
			} catch (IOException e2) {
				Log.w(LOG_TAG, "Error closing input stream", e);
			}
			return null;
		}
	}

	private Context context;
}
