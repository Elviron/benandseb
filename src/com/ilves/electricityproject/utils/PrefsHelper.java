package com.ilves.electricityproject.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

import com.ilves.electricityproject.MainActivity;

public class PrefsHelper {
	
	public static void saveInt(MainActivity context, String field, int value) {
		SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(field, value);
		editor.commit();
	}
	
	public static int getInt(MainActivity context, String field) {
		SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
		return sharedPref.getInt(field, 0);
	}
	
	public static void saveInt(FragmentActivity context, String field, int value) {
		SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(field, value);
		editor.commit();
	}
	
	public static int getInt(FragmentActivity context, String field) {
		SharedPreferences sharedPref = context.getPreferences(Context.MODE_PRIVATE);
		return sharedPref.getInt(field, 0);
	}

}
