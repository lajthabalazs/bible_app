package hu.droidium.bibliapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.TypedValue;
import android.widget.TextView;

public class Constants {

	public static final String LAST_READ_BOOK = "Last read book";
	public static final String LAST_READ_CHAPTER = "Last read chapter";
	public static final String LAST_READ_VERS = "Last read vers";
	public static final String SHOULD_OPEN_LAST_READ = "Open last read";

	
	public static final String BOOK_FILE_NAME = "Book file name";
	public static final String CHAPTER_INDEX = "Chapter index";
	public static final String VERSE_INDEX = "Vers index";
	
	private static final String SHARED_PREFS = "General preferences";
	public static final String FACEBOOK_LOGIN_DECISION = "Facebook login decision";
	public static final int FACEBOOK_UNKNOWN = 0;
	public static final int FACEBOOK_DONT_ASK = 1;
	public static final int FACEBOOK_LOGIN = 2;
	
	public static final String TEXT_SIZE_KEY = "Text size";
	public static final double TEXT_SIZE_FACTOR = 1.2;

	
	public static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
	}

	public static void savePosition(String book, int chapter,
			int vers, Context context) {
		SharedPreferences prefs = getPrefs(context);
		Editor editor = prefs.edit();
		editor.putString(LAST_READ_BOOK, book);
		editor.putInt(LAST_READ_CHAPTER, chapter);
		editor.putInt(LAST_READ_VERS, vers);
		editor.commit();
	}
	
	/**
	 * Scales text view's text size with the stored multiplier. Called multiple times on a text view results in incorrect scale.
	 * Use it only in the onCreate method.
	 * @param textView
	 * @param context
	 */
	public static void scaleText(TextView textView, Context context){
		int currentMultiplierFactor = getPrefs(context).getInt(TEXT_SIZE_KEY, 0);
		double currentMultiplier = Math.pow(Constants.TEXT_SIZE_FACTOR, currentMultiplierFactor);
		float baseSize = (int)textView.getTextSize();
		float actualSize = (float) (baseSize * currentMultiplier);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, actualSize);
	}
}
