package com.xinlan.otma.net;

import java.util.Locale;

import android.os.Build;

/**
 * 
 * @author panyi
 * 
 *         http://loopj.com/android-async-http/
 * 
 */
public class NetClient {
	public static final String TOKEN = "token";
	public static final String REGTOKEN = "registerToken";
	private static AsyncHttpClient client = new AsyncHttpClient();
	static {
		client.setResponseTimeout(30 * 1000);
		client.setUserAgent("Mozilla/5.0(Linux; U; Android "
				+ Build.VERSION.RELEASE
				+ "; "
				+ Locale.getDefault().getLanguage()
				+ "; "
				+ Build.MODEL
				+ ") AppleWebKit/533.0 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
	}

	/**
	 * 发�?get请求
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(url, params, responseHandler);
	}

	public static void getReg(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(url, params, responseHandler);
	}

	/**
	 * 发�?post请求
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.post(url, params, responseHandler);
	}

}// end class
