package hu.droidium.bibliapp.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.provider.BaseColumns;

public class Bookmark implements BaseColumns {
	public static final String TABLE_NAME = "bookmark";
	public static final String COLUMN_NAME_NOTE = "note";
	public static final String COLUMN_NAME_LAST_UPDATE = "lastUpdate";
	public static final String COLUMN_NAME_COLOR = "color";
	public static final String COLUMN_NAME_BOOK = "bookId";
	public static final String COLUMN_NAME_CHAPTER = "chapterNumber";
	public static final String COLUMN_NAME_VERS = "versNumber";
	public static final long NEW_ID = -1;
	public static final String DEFAULT_COLOR = "DEFAULT_COLOR";
	
	private long id;
	private String note;
	private String bookId;
	private int chapter;
	private int vers;
	private Date lastUpdate;
	private String color;
	
	public Bookmark (String note, String book, int chapter, int vers, String color) {
		this(-1, note, book, chapter, vers, color, new Date());
	}

	protected Bookmark (long id, String note, String book, int chapter, int vers, String color, String lastUpdate) {
		this(id, note, book, chapter, vers, color, getDate(lastUpdate));
	}

	private static Date getDate(String lastUpdate) {
		try {
			return new SimpleDateFormat(DatabaseManager.DATE_FORMAT, Locale.getDefault()).parse(lastUpdate);
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		} catch (NullPointerException e) {
			e.printStackTrace();
			return new Date();
		}
	}

	protected Bookmark (long id, String note, String book, int chapter, int vers, String color, Date lastUpdate) {
		this.id = id;
		this.note = note;
		this.bookId = book;
		this.chapter = chapter;
		this.vers = vers;
		this.color = color;
		this.lastUpdate = lastUpdate;
	}

	public long getId() {
		return id;
	}

	public String getNote() {
		return note;
	}

	public String getBookId() {
		return bookId;
	}

	public int getChapter() {
		return chapter;
	}

	public int getVers() {
		return vers;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public String getColor() {
		return color;
	}

	public static String getCreateTableText() {
		String create = "CREATE TABLE ";
		create += TABLE_NAME + " (";
		create += _ID + " INTEGER_PRIMARY_KEY,";
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
