package hu.droidium.bibliapp.database;

import java.util.Date;

import hu.droidium.bibliapp.data.Bookmark;
import android.provider.BaseColumns;

public class DbBookmark extends Bookmark implements BaseColumns {
	public static final String TABLE_NAME = "bookmark";
	public static final String COLUMN_NAME_NOTE = "note";
	public static final String COLUMN_NAME_LAST_UPDATE = "lastUpdate";
	public static final String COLUMN_NAME_COLOR = "color";
	public static final String COLUMN_NAME_BOOK = "bookId";
	public static final String COLUMN_NAME_CHAPTER = "chapterNumber";
	public static final String COLUMN_NAME_VERS = "versNumber";
	
	protected DbBookmark (String note, String book, int chapter, int vers, String color) {
		super(NO_ID, note, book, chapter, vers, color, new Date());
	}
	
	protected DbBookmark (long id, String note, String book, int chapter, int vers, String color, Date lastUpdate) {
		super(id, note, book, chapter, vers, color, lastUpdate);
	}

	public DbBookmark(long id, String note, String book, int chapter, int vers, String color, String lastUpdate) {
		super(id, note, book, chapter, vers, color, Bookmark.getDate(lastUpdate));
	}

	public static String getCreateTableText() {
		String create = "CREATE TABLE ";
		create += TABLE_NAME + " (";
		create += _ID + " INTEGER PRIMARY KEY,";
		create += COLUMN_NAME_NOTE + " TEXT,";
		create += COLUMN_NAME_LAST_UPDATE + " INTEGER,";
		create += COLUMN_NAME_COLOR + " TEXT,";
		create += COLUMN_NAME_BOOK + " TEXT,";
		create += COLUMN_NAME_CHAPTER + " INTEGER,";
		create += COLUMN_NAME_VERS + " INTEGER)";
		return create;
	}

	public static String getDeleteTableText() {
		return "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
}
