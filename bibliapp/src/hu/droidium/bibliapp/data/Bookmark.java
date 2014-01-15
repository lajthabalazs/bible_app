package hu.droidium.bibliapp.data;

import hu.droidium.bibliapp.database.DatabaseManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Bookmark {
	
	public static final long NO_ID = -1;
	public static final String DEFAULT_COLOR = "DEFAULT_COLOR";
	
	private long id;
	private String note;
	private String bookId;
	private int chapter;
	private int vers;
	private Date lastUpdate;
	private String color;
	
	public Bookmark(String note, String book, int chapter, int vers, String color) {
		this(NO_ID, note, book, chapter, vers, color, new Date());
	}

	protected Bookmark (long id, String note, String book, int chapter, int vers, String color, String lastUpdate) {
		this(id, note, book, chapter, vers, color, getDate(lastUpdate));
	}
	
	public void update(String note, String color) {
		this.note = note;
		this.color = color;
	}

	public static Date getDate(String lastUpdate) {
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
}
