/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package com.xinlan.otma.net;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Used to intercept and handle the responses from requests made using
 * {@link AsyncHttpClient}, with automatic parsing into a {@link JSONObject} or
 * {@link JSONArray}.
 * <p>
 * &nbsp;
 * </p>
 * This class is designed to be passed to get, post, put and delete requests
 * with the
 * {@link #onSuccess(int, org.apache.http.Header[], org.json.JSONArray)} or
 * {@link #onSuccess(int, org.apache.http.Header[], org.json.JSONObject)}
 * methods anonymously overridden.
 * <p>
 * &nbsp;
 * </p>
 * Additionally, you can override the other event methods from the parent class.
 */
public abstract class CustomHttpResponseHandler<T> extends
		TextHttpResponseHandler {

	private static final String LOG_TAG = "JsonHttpResponseHandler";

	/**
	 * Creates new JsonHttpResponseHandler, with JSON String encoding UTF-8
	 */
	public CustomHttpResponseHandler() {
		super(DEFAULT_CHARSET);
	}

	/**
	 * Creates new JsonHttpRespnseHandler with given JSON String encoding
	 * 
	 * @param encoding
	 *            String encoding to be used when parsing JSON
	 */
	public CustomHttpResponseHandler(String encoding) {
		super(encoding);
	}

	/**
	 * Returns when request succeeds
	 * 
	 * @param statusCode
	 *            http response status line
	 * @param headers
	 *            response headers if any
	 * @param response
	 *            parsed response if any
	 */
	public abstract void onSuccess(int statusCode, Header[] headers, T response);

	public abstract void onFailure(int statusCode, Header[] headers,
			Throwable throwable, String errorResponse);

	public void onSuccess(int statusCode, Header[] headers,
			String responseString) {
		Log.w(LOG_TAG,
				"onSuccess(int, Header[], JSONObject) was not overriden, but callback was received");
	}

	@Override
	public void onFailure(int statusCode, Header[] headers,
			String responseString, Throwable throwable) {
	}

	//
	// public void onFailure(int statusCode, Header[] headers,
	// Throwable throwable, T errorResponse) {
	// }

	@Override
	public final void onSuccess(final int statusCode, final Header[] headers,
			final byte[] responseBytes) {
		if (statusCode != HttpStatus.SC_NO_CONTENT) {
			Runnable parser = new Runnable() {
				@Override
				public void run() {
					try {
						final T jsonResponse = parseResponse(responseBytes);
						postRunnable(new Runnable() {
							@Override
							public void run() {
								onSuccess(statusCode, headers, jsonResponse);
							}
						});
					} catch (final Exception ex) {
						postRunnable(new Runnable() {
							@Override
							public void run() {
								onFailure(statusCode, headers, ex,
										ex.toString());
							}
						});
					}
				}
			};
			if (!getUseSynchronousMode()) {
				new Thread(parser).start();
			} else {
				// In synchronous mode everything should be run on one thread
				parser.run();
			}
		} else {
			try {
				onSuccess(statusCode, headers, parseResponse(null));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public final void onFailure(final int statusCode, final Header[] headers,
			final byte[] responseBytes, final Throwable throwable) {
		if (responseBytes != null) {
			Runnable parser = new Runnable() {
				@Override
				public void run() {
					try {
						// final T jsonResponse = parseResponse(responseBytes);
						postRunnable(new Runnable() {
							@Override
							public void run() {
								onFailure(statusCode, headers, throwable,
										throwable.toString());
							}
						});

					} catch (final Exception ex) {
						postRunnable(new Runnable() {
							@Override
							public void run() {
								onFailure(statusCode, headers, ex,
										ex.toString());
							}
						});

					}
				}
			};
			if (!getUseSynchronousMode()) {
				new Thread(parser).start();
			} else {
				// In synchronous mode everything should be run on one thread
				parser.run();
			}
		} else {
			Log.v(LOG_TAG,
					"response body is null, calling onFailure(Throwable, JSONObject)");
			onFailure(statusCode, headers, throwable, throwable.toString());
		}
	}

	protected T parseResponse(byte[] responseBody) throws Exception {
		if (null == responseBody)
			return null;
		T result = null;
		// trim the string to prevent start with blank, and test if the string
		// is valid JSON, because the parser don't do this :(. If JSON is not
		// valid this will return null
		String jsonString = getResponseString(responseBody, getCharset());
		if (jsonString != null) {
			result = parseJson(jsonString);
			Log.e("jsonString", jsonString);
		}
		return result;
	}

	/**
	 * json解析异步操作
	 * 
	 * @param src
	 * @return
	 */
	public abstract T parseJson(String src);
}
