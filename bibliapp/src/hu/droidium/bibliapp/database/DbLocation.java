package hu.droidium.bibliapp.database;

import android.provider.BaseColumns;
import hu.droidium.bibliapp.data.Location;

public class DbLocation extends Location implements BaseColumns {

	public static final String TABLE_NAME = "location";
	public static final String COLUMN_NAME_NAME = "name";
	public static final String COLUMN_NAME_LAT = "lat";
	public static final String COLUMN_NAME_LON = "lon";
	public static final String COLUMN_NAME_BOOK = "bookId";
	public static final String COLUMN_NAME_CHAPTER = "chapterNumber";
	public static final String COLUMN_NAME_VERS = "versNumber";
	
	
	protected DbLocation(String name, String lat, String lon, String bookId,
			int chapter, int verse) {
		super(name, lat, lon, bookId, chapter, verse);
	}
	
	public static String getCreateTableText() {
		String create = "CREATE TABLE ";
		create += TABLE_NAME + " (";
		create += COLUMN_NAME_NAME + " TEXT,";
		create += COLUMN_NAME_LAT + " TEXT,";
		create += COLUMN_NAME_LON + " TEXT,";
		create += COLUMN_NAME_BOOK + " TEXT,";
		create += COLUMN_NAME_CHAPTER + " INTEGER,";
		create += COLUMN_NAME_VERS + " INTEGER,";
		create += "PRIMARY KEY (" + COLUMN_NAME_NAME + "," + COLUMN_NAME_BOOK + "," + COLUMN_NAME_CHAPTER + "," + COLUMN_NAME_VERS + "))";
		return create;
	}

	public static String getDeleteTableText() {
		return "DROP TABLE IF EXISTS " + TABLE_NAME;
	}	
}