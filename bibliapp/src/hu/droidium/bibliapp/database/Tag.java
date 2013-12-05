package hu.droidium.bibliapp.database;

import android.provider.BaseColumns;

/**
 * Represents a tag added to a verse.
 * @author Balazs Lajtha
 *
 */
public class Tag implements BaseColumns {
	public static final String TABLE_NAME = "tag";
	public static final String COLUMN_NAME_TAG_ID = "tagId";
	public static final String COLUMN_NAME_BOOK = "bookId";
	public static final String COLUMN_NAME_CHAPTER = "chapterNumber";
	public static final String COLUMN_NAME_VERS = "versNumber";
	public static final String COLUMN_NAME_LAST_UPDATE = "updated"; // Seconds not milliseconds!
	
	private String tagId;
	private String book;
	private int chapter;
	private int vers;
	private long updated;
	
	protected Tag (String tagId, String book, int chapter, int vers, long updated) {
		this.tagId = tagId;
		this.book = book;
		this.chapter = chapter;
		this.vers = vers;
		this.updated = updated;
	}

	public String getTagId() {
		return tagId;
	}

	public String getBook() {
		return book;
	}

	public int getChapter() {
		return chapter;
	}

	public int getVers() {
		return vers;
	}

	public long getUpdated() {
		return updated;
	}

	public static String getCreateTableText() {
		String create = "CREATE TABLE ";
		create += TABLE_NAME + " (";
		create += COLUMN_NAME_TAG_ID + " TEXT,";
		create += COLUMN_NAME_BOOK + " TEXT,";
		create += COLUMN_NAME_CHAPTER + " INTEGER,";
		create += COLUMN_NAME_VERS + " INTEGER,";
		create += COLUMN_NAME_LAST_UPDATE + " INTEGER,";
		create += "PRIMARY KEY (" + COLUMN_NAME_BOOK + "," + COLUMN_NAME_CHAPTER + "," + COLUMN_NAME_VERS + "," + COLUMN_NAME_TAG_ID + "))";
		return create;
	}

	public static String getDeleteTableText() {
		return "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
}