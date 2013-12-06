package hu.droidium.bibliapp.database;

import hu.droidium.bibliapp.data.AssetReader;
import hu.droidium.bibliapp.data.BookmarkDataAdapter;
import hu.droidium.bibliapp.data.TagDataAdapter;
import hu.droidium.bibliapp.data.Translator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager implements BookmarkDataAdapter, TagDataAdapter, Translator {
	
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	BibleDbHelper dbHelper;
	private SQLiteDatabase db;
	private static final String VERSION_STORE = "Database version store";
	private static final String VERSION_KEY = "Database version key";
	
	public DatabaseManager(Context context) {
		dbHelper = new BibleDbHelper(context);
		db = dbHelper.getWritableDatabase();
		loadTagMetaFromAssets(context);
	}
	
	@Override
	public Bookmark saveBookmark(Bookmark bookmark) {
		ContentValues values = new ContentValues();
		values.put(Bookmark.COLUMN_NAME_NOTE, bookmark.getNote());
		values.put(Bookmark.COLUMN_NAME_BOOK, bookmark.getBookId());
		values.put(Bookmark.COLUMN_NAME_CHAPTER, bookmark.getChapter());
		values.put(Bookmark.COLUMN_NAME_VERS, bookmark.getVers());
		values.put(Bookmark.COLUMN_NAME_LAST_UPDATE, new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(bookmark.getLastUpdate()));
		values.put(Bookmark.COLUMN_NAME_COLOR, bookmark.getColor());
		if (bookmark.getId() == Bookmark.NEW_ID) {
			// Insert new value
			long newId = db.insert(Bookmark.TABLE_NAME, null , values);
			return new Bookmark(newId, bookmark.getNote(), bookmark.getBookId(), bookmark.getChapter(), bookmark.getVers(), bookmark.getColor(), new Date());
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
		
	@Override
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

	@Override
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
	
	@Override
	public void deleteBookmark(Bookmark bookmark) {
		String selection = Bookmark._ID + " =  ?";
		String[] selectionArgs = { String.valueOf(bookmark.getId()) };
		db.delete(Bookmark.TABLE_NAME, selection, selectionArgs);
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

	private void loadTagMetaFromAssets(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(VERSION_STORE, Context.MODE_PRIVATE);
		
		if (prefs.getInt(VERSION_KEY, -1) != db.getVersion()) {			
			List<TagMeta> tagMetas = AssetReader.parseTagMetas(context);
			for (TagMeta tag : tagMetas) {
				addTagMeta(tag.getId(), tag.getName(), tag.getColor());
			}
			prefs.edit().putInt(VERSION_KEY, db.getVersion()).commit();
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
		Cursor c = db.query(true, Tag.TABLE_NAME, columns, selection, selectionArgs, null, null, orderString, null, null);
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
}