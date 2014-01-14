package hu.droidium.bibliapp.data;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import hu.droidium.flurry_base.Log;
import hu.droidium.flurry_base.LogCategory;

public class VersionManager {
	
	private static final String VERSION_STORE = "data version store";	
	private static final String UPDATE_PROGRESS_PREFIX = "data update ";
	private static final String TAG = VersionManager.class.getName();

	public static int getInstalledVersion(Context context, String item) {
		SharedPreferences prefs = context.getSharedPreferences(VERSION_STORE, Context.MODE_PRIVATE);
		return prefs.getInt(item, -1);
	}

	public static int getAssetVersion(Context context, String item) {
		HashMap<String, Integer> versions = AssetReader.parseVersions(context);
		try {
			return versions.get(item);
		} catch (Exception e){
			return -1;
		}
	}

	public static int getUpdateProgress(Context context, int version, String item) {
		SharedPreferences prefs = context.getSharedPreferences(VERSION_STORE, Context.MODE_PRIVATE);
		return prefs.getInt(UPDATE_PROGRESS_PREFIX  + item + version, -1);
	}
	
	public static void setInstalledVersion(Context context, String item, int newVersion) {
		SharedPreferences prefs = context.getSharedPreferences(VERSION_STORE, Context.MODE_PRIVATE);
		prefs.edit().putInt(item, newVersion).commit();
	}

	public static void setUpdateProgress(Context context, String item, int newVersion, int progress) {
		SharedPreferences prefs = context.getSharedPreferences(VERSION_STORE, Context.MODE_PRIVATE);
		prefs.edit().putInt(UPDATE_PROGRESS_PREFIX  + item + newVersion, progress).commit();
		Log.v(LogCategory.DATABASE, TAG, item + " update progress " + progress);
	}
}
