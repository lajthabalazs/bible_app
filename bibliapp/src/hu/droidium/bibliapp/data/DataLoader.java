package hu.droidium.bibliapp.data;

import hu.droidium.bibliapp.database.DatabaseManager;
import hu.droidium.bibliapp.database.Tag;
import hu.droidium.bibliapp.database.TagMeta;
import hu.droidium.bibliapp.database.Translation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.util.Log;

public class DataLoader {
	private static final String TAG = "ProgramLoader";
	
	private static final String META_FOLDER = "meta";
	private static final String EXERCISES_FOLDER = "exercises";
	private static final String PROGRAM_FOLDER = "programs";
	private static final String TRANSLATION_FOLDER = "translations";
	
	
	private static final String TAG_FILE = "tags.json";
	private static final String TAG_KEY = "tags";

	public static void loadDataFromAssets(DatabaseManager databaseManager, Context context) throws IOException{
		AssetManager assetManager =  context.getAssets();
		// Load meta data
		
		loadTagMetaFromAsset(databaseManager, META_FOLDER + File.separator + TAG_FILE,context);

		// Load translations
		String[] translationFiles = assetManager.list(TRANSLATION_FOLDER);
		for(String translationFile : translationFiles){
			loadTranslationsFromFile(databaseManager, TRANSLATION_FOLDER + File.separator + translationFile, context);
		}
	}
	
	private static void loadTagMetaFromAsset(DatabaseManager databaseManager, String assetPath, Context context) {
		Log.e(TAG, "Loading tag meta data from asset " + assetPath);
		
		JSONArray tags = null;
		try {
			tags = getRootFromAsset(assetPath, context).getJSONArray(TAG_KEY);
		} catch (JSONException e) {
			Log.e(TAG, "Couldn't parse tags. " + e.getLocalizedMessage());
			e.printStackTrace();
			return;
		} catch (IOException e) {
			Log.e(TAG, "Couldn't parse tags. " + e.getLocalizedMessage());
			e.printStackTrace();
			return;
		}
		for (int i = 0; i < tags.length(); i++) {
			try {
				JSONObject tagJson = tags.getJSONObject(i);
				String tagId = tagJson.getString(TagMeta.COLUMN_NAME_TAG_ID);
				String tagName = tagJson.getString(TagMeta.COLUMN_NAME_TAG_NAME);
				String color = tagJson.getString(TagMeta.COLUMN_NAME_COLOR);
				TagMeta tag = new TagMeta(tagId, tagName, color);
				databaseManager.addTagMeta(tag);
			} catch (Exception e) {
				Log.e(TAG, "Couldn't parse tag meta: " + e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}

	private static void loadTranslationsFromFile(DatabaseManager databaseManager, String assetPath, Context context) {
		Log.e(TAG, "Loading translaitons from asset " + assetPath);
		
		JSONArray translations = null;
		try {
			translations = getRootArrayFromAsset(assetPath, context);
		} catch (IOException e) {
			Log.e(TAG, "Couldn't parse translations. " + e.getLocalizedMessage());
			e.printStackTrace();
			return;
		}
		for (int i = 0; i < translations.length(); i++) {
			try {
				JSONObject translationJson = translations.getJSONObject(i);
				String original = translationJson.getString(Translation.COLUMN_NAME_ORIGINAL);
				String language = translationJson.getString(Translation.COLUMN_NAME_LANGUAGE);
				String translationText = translationJson.getString(Translation.COLUMN_NAME_TRANSLATION);
				Translation translation = new Translation(original, language, translationText);
				databaseManager.addTranslation(translation);
				Log.e(TAG, "Added translation " + translation);
			} catch (Exception e) {
				Log.e(TAG, "Couldn't parse translation: " + e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	} 

	private static StringBuilder loadStringFromAsset(String assetPath, Context context) throws IOException {
        InputStream in = context.getAssets().open(assetPath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
		in.close();
		bufferedReader.close();
		return stringBuilder;
	}

	private static JSONObject getRootFromAsset(String assetPath, Context context) throws IOException {
		StringBuilder stringBuilder = loadStringFromAsset(assetPath, context);
		JSONObject root = null;
		try {
			root = new JSONObject(stringBuilder.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			throw new IOException(e.getLocalizedMessage());
		}
		return root;
	}

	private static JSONArray getRootArrayFromAsset(String assetPath, Context context) throws IOException {
		StringBuilder stringBuilder = loadStringFromAsset(assetPath, context);
		JSONArray root = null;
		try {
			root = new JSONArray(stringBuilder.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			throw new IOException(e.getLocalizedMessage());
		}
		return root;
	}
}