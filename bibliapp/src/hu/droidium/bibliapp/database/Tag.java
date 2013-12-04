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
	
	private long id;
	private String tagId;
	private String book;
	private int chapter;
	private int vers;
	
	public Tag (String tagId, String book, int chapter, int vers) {
		this(-1, tagId, book, chapter, vers);
	}

	protected Tag (long id, String tagId, String book, int chapter, int vers) {
		this.id = id;
		this.tagId = tagId;
		this.book = book;
		this.chapter = chapter;
		this.vers = vers;
	}

	public long getId() {
		return id;
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

	public static String getCreateTableText() {
		String create = "CREATE TABLE ";
		create += TABLE_NAME + " (";
		create += _ID + " INTEGER_PRIMARY_KEY,";
		create += COLUMN_NAME_TAG_ID + " TEXT,";
		create += COLUMN_NAME_BOOK + " TEXT,";
		create += COLUMN_NAME_CHAPTER + " INTEGER,";
		create += COLUMN_NAME_VERS + " INTEGER)";
		return create;
	}

	public static String getDeleteTableText() {
		return "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
}