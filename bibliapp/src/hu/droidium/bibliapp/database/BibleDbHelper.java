package hu.droidium.bibliapp.database;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BibleDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "BibleDatabase";

	public BibleDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Bookmark.getCreateTableText());
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
		if (oldVersion < 3) {
			Log.e("BibleDbHelper", "Database updating from version " + oldVersion);
			// Change bookmark's book id's from row/xy.txt to xy
			DatabaseManager databaseManager = new DatabaseManager(db);
			List<Bookmark> bookmarks = databaseManager.getAllBookmarks(null, false);
			for (Bookmark bookmark : bookmarks) {
				String book = bookmark.getBookId();
				if (book.startsWith("raw")) {
					book = book.substring(4,6);
					Bookmark newBookmark = new Bookmark(bookmark.getNote(), book, bookmark.getChapter(), bookmark.getVers(), bookmark.getColor());
					databaseManager.saveBookmark(newBookmark);
				}
			}
			

		}
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}