package com.nix.loadeer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class Utils {

	private static final String TAG = Utils.class.getSimpleName();
	private static Utils instance;

	private Utils() {
	}

    /**
     * get the current instance of the Utils class
     * @return
     * Utils instance
     */
	public static Utils getInstance() {
		if (instance == null) {
			instance = new Utils();
		}
		return instance;
	}


    /**
     * Chenck weather the Network is available or not
     * @param context Application Context
     * @return
     * true if Newtwork is available
     */
	public static boolean isOnline(Context context) {
		ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected();
	}


    /**
     * Check weather the String is null or empty
     * @param s
     * @return
     * true if String is empty
     */
	public static boolean isNullOrEmpty(String s) {
		if (s == null || s.length() == 0) {
			return true;
		}
		return false;
	}

    /**
     * Read the whole response came from the server
     * @param url
     * @return
     * Response Stream NULL if there is any kind of error in reading the data
     */
	public static String readJson(String url) {
		Log.i(TAG, url);
		url = url.replace(" ", "%20");
		Log.i(TAG, url);
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(TAG, "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        String response = builder.toString();
        response = response.replace(ApiConstant.UNWANTED, "");
        response = response.replace(")", "");
		return response;
	}
}
