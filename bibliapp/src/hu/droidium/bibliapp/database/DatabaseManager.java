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

	public List<TagMeta> getTagMetas() {
		String[] projection = {
				TagMeta.COLUMN_NAME_TAG_ID,
				TagMeta.COLUMN_NAME_COLOR
		};
		Cursor c = db.query(
				TagMeta.TABLE_NAME,
				projection,
				null,
				null,
				null,
				null,
				null
				);
		List<TagMeta> tags = new ArrayList<TagMeta>();
		for (boolean ok = c.moveToFirst(); ok; ok = c.moveToNext()) {
			String tagId = c.getString(c.getColumnIndex(TagMeta.COLUMN_NAME_TAG_ID));
			String tagName = c.getString(c.getColumnIndex(TagMeta.COLUMN_NAME_TAG_NAME));
			String color = c.getString(c.getColumnIndex(TagMeta.COLUMN_NAME_COLOR));
			tags.add(new TagMeta(tagId, tagName, color));
		}
		return tags;
	}
	
	public boolean addTagMeta(TagMeta tag) {
		ContentValues values = new ContentValues();
		values.put(TagMeta.COLUMN_NAME_TAG_ID, tag.getId());
		values.put(TagMeta.COLUMN_NAME_COLOR, tag.getColor());
		// Try to insert new value
		long newId = db.insert(TagMeta.TABLE_NAME, null , values);
		if (newId == -1) {
			// Update old value
			String selection = TagMeta.COLUMN_NAME_TAG_ID + " =  ?";
			String[] selectionArgs = { String.valueOf(tag.getId()) };
			int result = db.update(TagMeta.TABLE_NAME,
					values,
					selection,
					selectionArgs);
			if (result != 1) {
				return false; // Something got screwed up
			}
			else {
				return true; // Value updated
			}
		} else{
			return true; // New value inserted
		}
	}

	public boolean addTranslation(Translation translation) {
		ContentValues values = new ContentValues();
		values.put(Translation.COLUMN_NAME_ORIGINAL, translation.getOriginal());
		values.put(Translation.COLUMN_NAME_LANGUAGE, translation.getLanguage());
		values.put(Translation.COLUMN_NAME_TRANSLATION, translation.getTranslation());
		// Try to insert new value
		long newId = db.insert(Translation.TABLE_NAME, null , values);
		if (newId == -1) {
			// Update old value
			String selection = Translation.COLUMN_NAME_ORIGINAL + " =  ? AND " + Translation.COLUMN_NAME_LANGUAGE + " =  ?";
			String[] selectionArgs = { translation.getOriginal(), translation.getLanguage()};
			int result = db.update(Translation.TABLE_NAME,
					values,
					selection,
					selectionArgs);
			if (result != 1) {
				return false; // Something got screwed up
			}
			else {
				return true; // Value updated
			}
		} else{
			return true; // New value inserted
		}
	}
	
	public String getTranslation(String language, String original) {
		String[] projection = {
				Translation.COLUMN_NAME_ORIGINAL,
				Translation.COLUMN_NAME_LANGUAGE,
				Translation.COLUMN_NAME_TRANSLATION
		};
		String selection = Translation.COLUMN_NAME_ORIGINAL + " =  ? AND " + Translation.COLUMN_NAME_LANGUAGE + " =  ?";
		String[] selectionArgs = { original, language };
		Cursor c = db.query(
				Translation.TABLE_NAME,
				projection,
				selection,
				selectionArgs,
				null,
				null,
				null
				);
		List<Translation> translations = new ArrayList<Translation>();
		for (boolean ok = c.moveToFirst(); ok; ok = c.moveToNext()) {
			String translation = c.getString(c.getColumnIndex(Translation.COLUMN_NAME_TRANSLATION));
			translations.add(new Translation(original, language, translation));
		}
		if (translations.size() > 0) {
			return translations.get(0).getTranslation();
		} else{
			return original;
		}
	}

	public List<TagMeta> getTags(String book, int chapterIndex, int verseIndex) {
		ArrayList<TagMeta> tags = new ArrayList<TagMeta>();
		final String query = "SELECT * FROM " + TagMeta.TABLE_NAME + " meta INNER JOIN " + Tag.TABLE_NAME+ " tags " +
				"ON meta." + TagMeta.COLUMN_NAME_TAG_ID+ "=tags." + Tag.COLUMN_NAME_TAG_ID + " " + 
				"WHERE tags." + Tag.COLUMN_NAME_BOOK + " =? AND " +
				" tags." + Tag.COLUMN_NAME_CHAPTER + "=? AND " +
				" tags." + Tag.COLUMN_NAME_VERS + "=?;";
		String[] args = new String[]{book, String.valueOf(chapterIndex), String.valueOf(verseIndex)};
	    Cursor c = db.rawQuery(query, args);
	    for (boolean ok = c.moveToFirst(); ok; ok = c.moveToNext()) {
			String tagId = c.getString(c.getColumnIndex(TagMeta.COLUMN_NAME_TAG_ID));
			String tagName = c.getString(c.getColumnIndex(TagMeta.COLUMN_NAME_TAG_NAME));
			String tagColor = c.getString(c.getColumnIndex(TagMeta.COLUMN_NAME_COLOR));
			tags.add(new TagMeta(tagId, tagName, tagColor));
		}
	    return tags;
	}
	
	public String[] getBookIds() {
		return new String[] {"Elso", "Masodik", "Harmadik", "Negyedik"};
	}

	public int getChapterCount(String bookId) {
		return 3;
	}

	public int getVerseCount(String bookId, int chapterId) {
		return 5;
	}

	public String getVerseLine(String bookId, int chapterIndex, int verseIndex) {
		return "No verse yet for book " + bookId + " " + chapterIndex + " " + verseIndex;
	}
}
