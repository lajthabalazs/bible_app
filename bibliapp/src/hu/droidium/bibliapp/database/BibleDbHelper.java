package hu.droidium.bibliapp.database;

import hu.droidium.bibliapp.data.Bookmark;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BibleDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 4;
	private static final String DATABASE_NAME = "BibleDatabase";

	public BibleDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DbBookmark.getCreateTableText());
		db.execSQL(Tag.getCreateTableText());
		db.execSQL(TagMeta.getCreateTableText());
		db.execSQL(Translation.getCreateTableText());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 2) {
			// Add Tag and TagMeta tables
			db.execSQL(Tag.getCreateTableText());
			db.execSQL(TagMeta.getCreateTableText());
			db.execSQL(Translation.getCreateTableText());
		} 
		if (oldVersion < 4) {
			// Change bookmark's book id's from row/xy.txt to xy
			DatabaseManager databaseManager = new DatabaseManager(db);
			List<Bookmark> bookmarks = databaseManager.getAllBookmarks(null, false);
			db.execSQL(DbBookmark.getDeleteTableText());
			db.execSQL(DbBookmark.getCreateTableText());
			for (Bookmark bookmark : bookmarks) {
				String book = bookmark.getBookId();
				if (book.startsWith("raw")) {
					book = book.substring(4,6);
				}
				DbBookmark newBookmark = new DbBookmark(bookmark.getNote(), book, bookmark.getChapter(), bookmark.getVers(), bookmark.getColor());
				databaseManager.saveBookmark(newBookmark);
			}
			bookmarks = databaseManager.getAllBookmarks(null, false);
		}
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}