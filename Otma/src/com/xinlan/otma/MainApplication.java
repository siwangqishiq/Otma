package com.xinlan.otma;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MainApplication extends Application {
	public static MainApplication context;//
	private SharedPreferences mPrefs;// sp

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	}
	/**
	 * 
	 * @return
	 */
	public static MainApplication getInstance() {
		return context;
	}
}//end class
