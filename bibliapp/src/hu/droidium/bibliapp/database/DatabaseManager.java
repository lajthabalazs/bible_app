package hu.droidium.bibliapp.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	BibleDbHelper dbHelper;
	private SQLiteDatabase db;
	
	public DatabaseManager(Context context) {
		dbHelper = new BibleDbHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
	public Bookmark saveBookmark(Bookmark bookmark) {
		ContentValues values = new ContentValues();
		values.put(Bookmark.COLUMN_NAME_NOTE, bookmark.getNote());
		values.put(Bookmark.COLUMN_NAME_BOOK, bookmark.getBook());
		values.put(Bookmark.COLUMN_NAME_CHAPTER, bookmark.getChapter());
		values.put(Bookmark.COLUMN_NAME_VERS, bookmark.getVers());
		values.put(Bookmark.COLUMN_NAME_LAST_UPDATE, new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(bookmark.getLastUpdate()));
		values.put(Bookmark.COLUMN_NAME_COLOR, bookmark.getColor());
		if (bookmark.getId() == Bookmark.NEW_ID) {
			// Insert new value
			long newId = db.insert(Bookmark.TABLE_NAME, null , values);
			return new Bookmark(newId, bookmark.getNote(), bookmark.getBook(), bookmark.getChapter(), bookmark.getVers(), bookmark.getColor(), new Date());
		} else {
			// Update old value
			String selection = Bookmark._ID + " =  ?";
			String[] selectionArgs = { String.valueOf(bookmark.getId()) };
			int result = db.update(Bookmark.TABLE_NAME,
					values,
					selection,
					selectionArgs);
			if (result != 1) {
				return null;
			}
			else {
				return bookmark;
			}
		}
	}
		
	public List<Bookmark> getBookmarksForChapter(String book, int chapter) {
		String[] projection = {
				Bookmark._ID,
				Bookmark.COLUMN_NAME_NOTE,
				Bookmark.COLUMN_NAME_BOOK,
				Bookmark.COLUMN_NAME_CHAPTER,
				Bookmark.COLUMN_NAME_VERS,
				Bookmark.COLUMN_NAME_LAST_UPDATE,
				Bookmark.COLUMN_NAME_COLOR
		};
		String selection = Bookmark.COLUMN_NAME_BOOK + " LIKE ? AND " + Bookmark.COLUMN_NAME_CHAPTER + " = ?" ;
		String[] selectionArgs = { book, String.valueOf(chapter) };
		Cursor c = db.query(
				Bookmark.TABLE_NAME,
				projection,
				selection,
				selectionArgs,
				null,
				null,
				null
				);
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		for (boolean ok = c.moveToFirst(); ok; ok = c.moveToNext()) {
			long id = c.getLong(c.getColumnIndex(Bookmark._ID));
			String note = c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_NOTE));
			int vers = c.getInt(c.getColumnIndex(Bookmark.COLUMN_NAME_VERS));
			String color = c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_COLOR));
			String lastUpdate = c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_LAST_UPDATE));
			bookmarks.add(new Bookmark(id, note, book, chapter, vers, color, lastUpdate));
		}
		return bookmarks;
	}

	public List<Bookmark> getAllBookmarks(String sortColumn, boolean sortDesc) {
		String[] projection = {
				Bookmark._ID,
				Bookmark.COLUMN_NAME_NOTE,
				Bookmark.COLUMN_NAME_BOOK,
				Bookmark.COLUMN_NAME_CHAPTER,
				Bookmark.COLUMN_NAME_VERS,
				Bookmark.COLUMN_NAME_LAST_UPDATE,
				Bookmark.COLUMN_NAME_COLOR
		};
		String sortOrder  = Bookmark.COLUMN_NAME_LAST_UPDATE + " DESC";
		if (sortColumn != null) {
			sortOrder = sortColumn + " " + (sortDesc?"DESC":"ASC");
		}
		Cursor c = db.query(
				Bookmark.TABLE_NAME,
				projection,
				null,
				null,
				null,
				null,
				sortOrder
				);
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		for (boolean ok = c.moveToFirst(); ok; ok = c.moveToNext()) {
			long id = c.getLong(c.getColumnIndex(Bookmark._ID));
			String note = c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_NOTE));
			String book = c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_BOOK));
			int chapter = c.getInt(c.getColumnIndex(Bookmark.COLUMN_NAME_CHAPTER));
			int vers = c.getInt(c.getColumnIndex(Bookmark.COLUMN_NAME_VERS));
			String color = c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_COLOR));
			String lastUpdate = c.getString(c.getColumnIndex(Bookmark.COLUMN_NAME_LAST_UPDATE));
			bookmarks.add(new Bookmark(id, note, book, chapter, vers, color, lastUpdate));
		}
		return bookmarks;
	}
	
	public void deleteBookmark(Bookmark bookmark) {
		String selection = Bookmark._ID + " =  ?";
		String[] selectionArgs = { String.valueOf(bookmark.getId()) };
		db.delete(Bookmark.TABLE_NAME, selection, selectionArgs);
	}
}
