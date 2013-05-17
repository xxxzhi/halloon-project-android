package com.halloon.android.data;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.halloon.android.util.Constants;
import com.halloon.android.util.UrlUtils;

public class APIManager {

	public static final String HTTPS = "https://";
	public static final String HTTP = "http://";

	public static String NO_ERROR = "no_error";
	public static String CONNECTION_ERROR = "connection_error";
	public static String AUTH_ERROR = "auth_error";
	public static String NO_INTERNET_CONNECTION_ERROR = "no_internet_connection_error";
	public static String CONFLICT_ERROR_CODE = "409";

	public enum Method {
		POST, GET, PUT, DELETE
	};

	private static APIManager instance;
	private Context context;

	private APIManager(Context context) {
		this.context = context;
	}

	protected static APIManager getInstance(Context context) {
		if (instance == null) {
			instance = new APIManager(context);
		}
		return instance;
	}

	private String convertStreamToString(InputStream is) {
		return convertStreamToString(is, false);
	}

	private String convertStreamToString(InputStream is, boolean isCompressed) {

		StringBuilder sb = new StringBuilder();

		try {

			GZIPInputStream zis = null;

			if (isCompressed)
				zis = new GZIPInputStream(new BufferedInputStream(is));

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					(isCompressed) ? zis : is));

			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (OutOfMemoryError e) {
		} catch (IOException e) {
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {

		@Override
		public String getMethod() {
			return "DELETE";
		}

		public HttpDeleteWithBody(final String uri) {
			super();
			setURI(URI.create(uri));
		}

		public HttpDeleteWithBody(final URI uri) {
			super();
			setURI(uri);
		}

		public HttpDeleteWithBody() {
			super();
		}
	}

	private String connect(String url, Method method, Bundle headers,
			Bundle params) {

		HttpResponse resp = null;
		HttpClient asyncClient = new DefaultHttpClient();

		// fix for some providers
		HttpProtocolParams.setUseExpectContinue(asyncClient.getParams(), false);

		// create correct HttpUriRequest

		HttpUriRequest httpUriRequest = null;

		switch (method) {

		case POST:
			httpUriRequest = new HttpPost(url);
			((HttpPost) httpUriRequest).setEntity(UrlUtils
					.encodePostParams(params));
			break;
		case GET:
			httpUriRequest = new HttpGet(
					url
							+ ((params != null) ? "&"
									+ UrlUtils.encodeUrl(params) : ""));
			break;
		case PUT:
			httpUriRequest = new HttpPut(url);
			((HttpPut) httpUriRequest).setEntity(UrlUtils
					.encodePostParams(params));
			break;
		case DELETE:
			httpUriRequest = new HttpDeleteWithBody(url);
			((HttpDeleteWithBody) httpUriRequest).setEntity(UrlUtils
					.encodePostParams(params));
			break;
		default:
			httpUriRequest = new HttpGet(
					url
							+ ((params != null) ? "&"
									+ UrlUtils.encodeUrl(params) : ""));
			break;
		}

		// set headers

		if (headers != null) {

			Iterator<String> keys = headers.keySet().iterator();

			while (keys.hasNext()) {
				String key = keys.next();
				httpUriRequest.addHeader(key, headers.getString(key));
			}

		}

		// ///////////////////////////// LOG

		Log.d(Constants.LOG_TAG, "[" + httpUriRequest.getMethod() + "] "
				+ httpUriRequest.getURI());

		for (int i = 0; i < httpUriRequest.getAllHeaders().length; i++) {
			Log.d(Constants.LOG_TAG,
					"[HEADER] " + httpUriRequest.getAllHeaders()[i].getName()
							+ ": "
							+ httpUriRequest.getAllHeaders()[i].getValue());
		}

		try {

			if (method == Method.POST || method == Method.PUT
					|| method == Method.DELETE) {

				HttpEntity bodyEntity = ((HttpEntityEnclosingRequestBase) httpUriRequest)
						.getEntity();
				if (bodyEntity != null) {
					InputStream bodyStream = bodyEntity.getContent();
					String body = convertStreamToString(bodyStream);
					Log.d(Constants.LOG_TAG, "[BODY] " + body);
				}

			}

		} catch (IllegalStateException e) {
		} catch (IOException e) {
		}

		// ///////////////////////////// LOG

		// execute HTTP Request

		try {

			resp = asyncClient.execute(httpUriRequest);

			HttpEntity entity = resp.getEntity();
			InputStream instream = entity.getContent();

			boolean isEncoded = false;
			Header encoding = entity.getContentEncoding();
			if (encoding != null) {
				if (encoding.getValue().equals("gzip")) {
					isEncoded = true;
				}
			}

			String response = convertStreamToString(instream, isEncoded);

			int responseCode = resp.getStatusLine().getStatusCode();

			if (method == Method.GET
					&& responseCode != HttpURLConnection.HTTP_OK) {
				return errorOjbect(String.valueOf(responseCode), response);
			}

			if ((method == Method.POST || method == Method.PUT || method == Method.DELETE)
					&& (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED)) {
				return errorOjbect(String.valueOf(responseCode), response);

			} else if ((method == Method.POST || method == Method.PUT || method == Method.DELETE)
					&& (responseCode == HttpURLConnection.HTTP_OK && responseCode == HttpURLConnection.HTTP_CREATED)) {
				return statusOkObject(String.valueOf(responseCode), response);
			}

			return response;

		} catch (ClientProtocolException e) {
			return errorOjbect("ClientProtocolException", e.getMessage());

		} catch (IOException e) {
			return errorOjbect("IOException", e.getMessage());
		}

	}

	private String statusOkObject(String name, String message) {

		JSONObject responseObject = new JSONObject();

		try {

			JSONObject okObject = new JSONObject();
			okObject.put("code", name);
			okObject.put("message", message);

			responseObject.put("status", okObject);

		} catch (JSONException e) {
		}

		return responseObject.toString();
	}

	private String errorOjbect(String name, String message) {

		JSONObject responseObject = new JSONObject();

		try {

			JSONObject errorObject = new JSONObject();
			errorObject.put("type", name);
			errorObject.put("message", message);

			responseObject.put("error", errorObject);

		} catch (JSONException e) {
		}

		return responseObject.toString();
	}

	public String request(String graphPath, Method method, Bundle params,
			Bundle headers, String tokenId) {

		return "";

	}

}
