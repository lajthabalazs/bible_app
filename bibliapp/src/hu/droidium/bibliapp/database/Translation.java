package hu.droidium.bibliapp.database;

import android.provider.BaseColumns;

public class Translation implements BaseColumns {
	public static final String TABLE_NAME = "translation";
	public static final String COLUMN_NAME_ORIGINAL = "original";
	public static final String COLUMN_NAME_LANGUAGE = "language";
	public static final String COLUMN_NAME_TRANSLATION = "translation";
	
	private String original;
	private String language;
	private String translation;
	
	public Translation (String original, String language, String translation) {
		this.original = original;
		this.language = language;
		this.translation = translation;
	}

	public String getOriginal() {
		return original;
	}

	public String getLanguage() {
		return language;
	}

	public String getTranslation() {
		return translation;
	}

	public static String getCreateTableText() {
		String create = "CREATE TABLE ";
		create += TABLE_NAME + " (";
		create += COLUMN_NAME_ORIGINAL + " TEXT,";
		create += COLUMN_NAME_LANGUAGE + " TEXT,";
		create += COLUMN_NAME_TRANSLATION + " TEXT,";
		create += "PRIMARY KEY (" + COLUMN_NAME_ORIGINAL + ", " + COLUMN_NAME_LANGUAGE + "))";
		return create;
	}

	public static String getDeleteTableText() {
		return "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
}