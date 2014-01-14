package hu.droidium.bibliapp.database;

import hu.droidium.bibliapp.data.Bookmark;
import hu.droidium.bibliapp.data.BookmarkDataAdapter;
import hu.droidium.bibliapp.data.Location;
import hu.droidium.bibliapp.data.LocationAdapter;
import hu.droidium.bibliapp.data.TagDataAdapter;
import hu.droidium.bibliapp.data.Translator;
import hu.droidium.bibliapp.data.Verse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import hu.droidium.flurry_base.Log;

public class DatabaseManager implements BookmarkDataAdapter, TagDataAdapter, Translator, LocationAdapter {
	
	private static final String TAG = DatabaseManager.class.getName();
	
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private SQLiteDatabase db;
	static final String LOCATION_UPDATE_STATUS_KEY = "Location update status key";
	
	public DatabaseManager(Context context) {
		BibleDbHelper dbHelper = new BibleDbHelper(context);
		db = dbHelper.getWritableDatabase();
	}

	protected DatabaseManager(SQLiteDatabase db) {
		this.db = db;
	}

	@Override
	public Bookmark saveBookmark(Bookmark bookmark) {
		ContentValues values = new ContentValues();
		values.put(DbBookmark.COLUMN_NAME_NOTE, bookmark.getNote());
		values.put(DbBookmark.COLUMN_NAME_BOOK, bookmark.getBookId());
		values.put(DbBookmark.COLUMN_NAME_CHAPTER, bookmark.getChapter());
		values.put(DbBookmark.COLUMN_NAME_VERS, bookmark.getVers());
		values.put(DbBookmark.COLUMN_NAME_LAST_UPDATE, new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(bookmark.getLastUpdate()));
		values.put(DbBookmark.COLUMN_NAME_COLOR, bookmark.getColor());
		if (bookmark.getId() == Bookmark.NO_ID) {
			// Insert new value
			long newId = db.insert(DbBookmark.TABLE_NAME, null , values);
			return new DbBookmark(newId, bookmark.getNote(), bookmark.getBookId(), bookmark.getChapter(), bookmark.getVers(), bookmark.getColor(), new Date());
		} else {
			// Update old value
			String selection = DbBookmark._ID + "= ?";
			String[] selectionArgs = { String.valueOf(bookmark.getId()) };
			int result = db.update(DbBookmark.TABLE_NAME,
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
		
	@Override
	public List<Bookmark> getBookmarksForChapter(String book, int chapter) {
		String[] projection = {
				DbBookmark._ID,
				DbBookmark.COLUMN_NAME_NOTE,
				DbBookmark.COLUMN_NAME_BOOK,
				DbBookmark.COLUMN_NAME_CHAPTER,
				DbBookmark.COLUMN_NAME_VERS,
				DbBookmark.COLUMN_NAME_LAST_UPDATE,
				DbBookmark.COLUMN_NAME_COLOR
		};
		String selection = DbBookmark.COLUMN_NAME_BOOK + " LIKE ? AND " + DbBookmark.COLUMN_NAME_CHAPTER + " = ?" ;
		String[] selectionArgs = { book, String.valueOf(chapter) };
		Cursor c = db.query(
				DbBookmark.TABLE_NAME,
				projection,
				selection,
				selectionArgs,
				null,
				null,
				null
				);
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		for (boolean ok = c.moveToFirst(); ok; ok = c.moveToNext()) {
			long id = c.getLong(c.getColumnIndex(DbBookmark._ID));
			String note = c.getString(c.getColumnIndex(DbBookmark.COLUMN_NAME_NOTE));
			int vers = c.getInt(c.getColumnIndex(DbBookmark.COLUMN_NAME_VERS));
			String color = c.getString(c.getColumnIndex(DbBookmark.COLUMN_NAME_COLOR));
			String lastUpdate = c.getString(c.getColumnIndex(DbBookmark.COLUMN_NAME_LAST_UPDATE));
			bookmarks.add(new DbBookmark(id, note, book, chapter, vers, color, lastUpdate));
		}
		return bookmarks;
	}

	@Override
	public List<Bookmark> getAllBookmarks(String sortColumn, boolean sortDesc) {
		String[] projection = {
				DbBookmark._ID,
				DbBookmark.COLUMN_NAME_NOTE,
				DbBookmark.COLUMN_NAME_BOOK,
				DbBookmark.COLUMN_NAME_CHAPTER,
				DbBookmark.COLUMN_NAME_VERS,
				DbBookmark.COLUMN_NAME_LAST_UPDATE,
				DbBookmark.COLUMN_NAME_COLOR
		};
		String sortOrder  = DbBookmark.COLUMN_NAME_LAST_UPDATE + " DESC";
		if (sortColumn != null) {
			sortOrder = sortColumn + " " + (sortDesc?"DESC":"ASC");
		}
		Cursor c = db.query(
				DbBookmark.TABLE_NAME,
				projection,
				null,
				null,
				null,
				null,
				sortOrder
				);
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		for (boolean ok = c.moveToFirst(); ok; ok = c.moveToNext()) {
			long id = c.getLong(c.getColumnIndex(DbBookmark._ID));
			String note = c.getString(c.getColumnIndex(DbBookmark.COLUMN_NAME_NOTE));
			String book = c.getString(c.getColumnIndex(DbBookmark.COLUMN_NAME_BOOK));
			int chapter = c.getInt(c.getColumnIndex(DbBookmark.COLUMN_NAME_CHAPTER));
			int vers = c.getInt(c.getColumnIndex(DbBookmark.COLUMN_NAME_VERS));
			String color = c.getString(c.getColumnIndex(DbBookmark.COLUMN_NAME_COLOR));
			String lastUpdate = c.getString(c.getColumnIndex(DbBookmark.COLUMN_NAME_LAST_UPDATE));
			bookmarks.add(new DbBookmark(id, note, book, chapter, vers, color, lastUpdate));
		}
		return bookmarks;
	}
	
	@Override
	public void deleteBookmark(Bookmark bookmark) {
		String selection = DbBookmark._ID + "=?";
		String[] selectionArgs = { String.valueOf(bookmark.getId()) };
		db.delete(DbBookmark.TABLE_NAME, selection, selectionArgs);
	}

	@Override
	public List<TagMeta> getTagMetas() {
		String[] projection = {
				TagMeta.COLUMN_NAME_TAG_ID,
				TagMeta.COLUMN_NAME_TAG_NAME,
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
	
	@Override
	public TagMeta getTagMeta(String tagId) {
		String[] projection = {
				TagMeta.COLUMN_NAME_TAG_ID,
				TagMeta.COLUMN_NAME_TAG_NAME,
				TagMeta.COLUMN_NAME_COLOR
		};
	    String selection = TagMeta.COLUMN_NAME_TAG_ID + "=?";
	    String[] selectionArgs = new String[]{tagId};
		Cursor c = db.query(
				TagMeta.TABLE_NAME,
				projection,
				selection,
				selectionArgs,
				null,
				null,
				null
				);
		TagMeta ret = null;
		if (c.moveToFirst()) {
			String tagName = c.getString(c.getColumnIndex(TagMeta.COLUMN_NAME_TAG_NAME));
			String color = c.getString(c.getColumnIndex(TagMeta.COLUMN_NAME_COLOR));
			ret = new TagMeta(tagId, tagName, color);
		}
		return ret;
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
	
	@Override
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

	@Override
	public List<TagMeta> getTags(String book, int chapterIndex, int verseIndex) {
		ArrayList<TagMeta> tags = new ArrayList<TagMeta>();
		final String query = "SELECT * FROM " + TagMeta.TABLE_NAME + " meta INNER JOIN " + Tag.TABLE_NAME+ " tags " +
				"ON meta." + TagMeta.COLUMN_NAME_TAG_ID + "=tags." + Tag.COLUMN_NAME_TAG_ID + " " + 
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
	
	@Override
	public ArrayList<String> getTagColors(String bookId, int chapterIndex, int verseIndex) {
		List<TagMeta> tags = getTags(bookId, chapterIndex, verseIndex);
		ArrayList<String> colors = new ArrayList<String>();
		for (TagMeta tag : tags){
			colors.add(tag.getColor());
		}
		return colors;
	}
		
	@Override
	public boolean removeTag(String id, String bookId, int chapterIndex,
			int verseIndex) {
		String[] whereArgs = {bookId, ""+chapterIndex, "" + verseIndex, id};
		int result = db.delete(Tag.TABLE_NAME,
				Tag.COLUMN_NAME_BOOK + "=? AND " +
						Tag.COLUMN_NAME_CHAPTER + "=? AND " +
						Tag.COLUMN_NAME_VERS + "=? AND " +
						Tag.COLUMN_NAME_TAG_ID + "=?", whereArgs);
		return (result != 0);
	}

	@Override
	public boolean addTag(String tagId, String bookId, int chapterIndex,
			int verseIndex) {
		ContentValues values = new ContentValues();
		values.put(Tag.COLUMN_NAME_TAG_ID, tagId);
		values.put(Tag.COLUMN_NAME_BOOK, bookId);
		values.put(Tag.COLUMN_NAME_CHAPTER, chapterIndex);
		values.put(Tag.COLUMN_NAME_VERS, verseIndex);
		values.put(Tag.COLUMN_NAME_LAST_UPDATE, (System.currentTimeMillis() / 1000)); // Seconds, not milliseconds!
		long result = db.replace(Tag.TABLE_NAME,
				null,
				values);
		if (result != 1) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean addTagMeta(String tagId, String name, String color) {
		ContentValues values = new ContentValues();
		values.put(TagMeta.COLUMN_NAME_TAG_ID, tagId);
		values.put(TagMeta.COLUMN_NAME_TAG_NAME, name);
		values.put(TagMeta.COLUMN_NAME_COLOR, color);
		long result = db.replace(TagMeta.TABLE_NAME, null , values);
		if (result != 1) {
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public List<Tag> getTags(String tagMetaId) {		
		ArrayList<Tag> tags = new ArrayList<Tag>();
	    String[] columns = new String[]{
	    		Tag.COLUMN_NAME_TAG_ID,
	    		Tag.COLUMN_NAME_BOOK,
	    		Tag.COLUMN_NAME_CHAPTER,
	    		Tag.COLUMN_NAME_VERS,
	    		Tag.COLUMN_NAME_LAST_UPDATE};
	    String selection = Tag.COLUMN_NAME_TAG_ID + "=?";
	    String[] selectionArgs = new String[]{tagMetaId};
	    String orderString = Tag.COLUMN_NAME_LAST_UPDATE + " desc";
		Cursor c = db.query(Tag.TABLE_NAME, columns, selection, selectionArgs, null, null, orderString, null);
	    for (boolean ok = c.moveToFirst(); ok; ok = c.moveToNext()) {
			String tagId = c.getString(c.getColumnIndex(Tag.COLUMN_NAME_TAG_ID));
			String bookId = c.getString(c.getColumnIndex(Tag.COLUMN_NAME_BOOK));
			int chapter = c.getInt(c.getColumnIndex(Tag.COLUMN_NAME_CHAPTER));
			int vers = c.getInt(c.getColumnIndex(Tag.COLUMN_NAME_VERS));
			long updated = c.getLong(c.getColumnIndex(Tag.COLUMN_NAME_LAST_UPDATE));
			tags.add(new Tag(tagId, bookId, chapter, vers, updated));
		}
	    return tags;
	}

	@Override
	public int getTotalTags() {
		final String query = "SELECT COUNT(*) as tagCount FROM " + Tag.TABLE_NAME + ";";
	    Cursor c = db.rawQuery(query, null);
	    if (c.moveToFirst()) {
	    	return c.getInt(c.getColumnIndex("tagCount"));
	    } else {
	    	return 0;
	    }
	}

	@Override
	public int getTotalTags(String tagMetaId) {
		final String query = "SELECT COUNT(*) as tagCount FROM " + Tag.TABLE_NAME +
				" WHERE " + Tag.COLUMN_NAME_TAG_ID + " =?;";
		String[] args = new String[]{tagMetaId};
	    Cursor c = db.rawQuery(query, args);
	    if (c.moveToFirst()) {
	    	return c.getInt(c.getColumnIndex("tagCount"));
	    } else {
	    	return 0;
	    }
	}

	public void destroy() {
		try {
			db.close();
		} catch (Exception e) {
			Log.e(TAG, "Database couldn't be closed.", e);
		}
	}

	boolean addLocation(Location location) {
		ContentValues values = new ContentValues();
		values.put(DbLocation.COLUMN_NAME_NAME, location.getName());
		values.put(DbLocation.COLUMN_NAME_LAT, location.getLat());
		values.put(DbLocation.COLUMN_NAME_LON, location.getLon());
		values.put(DbLocation.COLUMN_NAME_BOOK, location.getBookId());
		values.put(DbLocation.COLUMN_NAME_CHAPTER, location.getChapter());
		values.put(DbLocation.COLUMN_NAME_VERS, location.getVerse());
		long result = db.replace(DbLocation.TABLE_NAME, null , values);
		if (result != 1) {
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public List<Location> getLocations(String bookId, int chapter, int verse) {
		ArrayList<Location> locations = new ArrayList<Location>();
	    String[] columns = new String[]{
	    		DbLocation.COLUMN_NAME_NAME,
	    		DbLocation.COLUMN_NAME_LAT,
	    		DbLocation.COLUMN_NAME_LON};
	    String selection = DbLocation.COLUMN_NAME_BOOK + "=? AND " + DbLocation.COLUMN_NAME_CHAPTER + "=? AND " + DbLocation.COLUMN_NAME_VERS + "=?" ;
	    String[] selectionArgs = new String[]{bookId, Integer.toString(chapter), Integer.toString(verse)};
	    String orderString = DbLocation.COLUMN_NAME_NAME + " asc";
	    Log.e(TAG, "Getting locations for book ");
		Cursor c = db.query(DbLocation.TABLE_NAME, columns, selection, selectionArgs, null, null, orderString, null);
	    for (boolean ok = c.moveToFirst(); ok; ok = c.moveToNext()) {
			String name = c.getString(c.getColumnIndex(DbLocation.COLUMN_NAME_NAME));
			String lat = c.getString(c.getColumnIndex(DbLocation.COLUMN_NAME_LAT));
			String lon = c.getString(c.getColumnIndex(DbLocation.COLUMN_NAME_LON));
			locations.add(new DbLocation(name, lat, lon, bookId, chapter, verse));
		}
	    return locations;
	}

	@Override
	public List<Location> getAllLocations() {
		ArrayList<Location> locations = new ArrayList<Location>();
	    String[] columns = new String[]{
	    		DbLocation.COLUMN_NAME_NAME,
	    		DbLocation.COLUMN_NAME_LAT,
	    		DbLocation.COLUMN_NAME_LON,
	    		DbLocation.COLUMN_NAME_BOOK,
	    		DbLocation.COLUMN_NAME_CHAPTER,
	    		DbLocation.COLUMN_NAME_VERS};
	    String orderString = DbLocation.COLUMN_NAME_NAME + " asc";
		Cursor c = db.query(DbLocation.TABLE_NAME, columns, null, null, null, null, orderString, null);
	    for (boolean ok = c.moveToFirst(); ok; ok = c.moveToNext()) {
			String name = c.getString(c.getColumnIndex(DbLocation.COLUMN_NAME_NAME));
			String lat = c.getString(c.getColumnIndex(DbLocation.COLUMN_NAME_LAT));
			String lon = c.getString(c.getColumnIndex(DbLocation.COLUMN_NAME_LON));
			String bookId = c.getString(c.getColumnIndex(DbLocation.COLUMN_NAME_BOOK));
			int chapter = c.getInt(c.getColumnIndex(DbLocation.COLUMN_NAME_CHAPTER));
			int verse = c.getInt(c.getColumnIndex(DbLocation.COLUMN_NAME_VERS));
			locations.add(new DbLocation(name, lat, lon, bookId, chapter, verse));
		}
	    return locations;
	}

	@Override
	public List<Verse> getVerses(String locationName) {
		ArrayList<Verse> verses = new ArrayList<Verse>();
	    String[] columns = new String[]{
	    		DbLocation.COLUMN_NAME_BOOK,
	    		DbLocation.COLUMN_NAME_CHAPTER,
	    		DbLocation.COLUMN_NAME_VERS};
	    String selection = DbLocation.COLUMN_NAME_NAME + "=?" ;
	    String[] selectionArgs = new String[]{locationName};
		Cursor c = db.query(DbLocation.TABLE_NAME, columns, selection, selectionArgs, null, null, null, null);
	    for (boolean ok = c.moveToFirst(); ok; ok = c.moveToNext()) {
			String bookId = c.getString(c.getColumnIndex(DbLocation.COLUMN_NAME_BOOK));
			int chapter = c.getInt(c.getColumnIndex(DbLocation.COLUMN_NAME_CHAPTER));
			int verse = c.getInt(c.getColumnIndex(DbLocation.COLUMN_NAME_VERS));
			verses.add(new Verse(bookId, chapter, verse, null));
		}
	    return verses;
	}

	public int getVersion() {
		return db.getVersion();
	}
}