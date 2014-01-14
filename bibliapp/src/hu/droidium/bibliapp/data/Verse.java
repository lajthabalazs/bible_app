package hu.droidium.bibliapp.data;

import hu.droidium.bibliapp.database.DatabaseManager;
import hu.droidium.bibliapp.database.TagMeta;

import java.util.ArrayList;
import java.util.List;

public class Verse {

	private String line;
	private String bookId;
	private int verseIndex;
	private int chapterIndex;
	
	public Verse(int chapterIndex, int verseIndex, String line) {
		this.chapterIndex = chapterIndex;
		this.setLine(line);
		this.setVerseIndex(verseIndex);
	}

	public Verse(String bookId, int chapter, int verse, String line) {
		this.chapterIndex = chapter;
		this.setLine(line);
		this.setVerseIndex(verseIndex);
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public int getVerseIndex() {
		return verseIndex;
	}

	public void setVerseIndex(int verseIndex) {
		this.verseIndex = verseIndex;
	}

	public String getId() {
		return bookId + ", " + (chapterIndex + 1) + "." + (verseIndex + 1);
	}

	public ArrayList<String> getTagColors(DatabaseManager databaseManager) {
		List<TagMeta> tags = databaseManager.getTags(bookId, chapterIndex, verseIndex);
		ArrayList<String> colors = new ArrayList<String>();
		for (TagMeta tag : tags){
			colors.add(tag.getColor());
		}
		return colors;
	}
	
	public static String getVerseLabel(String bookId, int chapterIndex, int verseIndex, BibleDataAdapter bibleDataAdapter) {
		return bibleDataAdapter.getBookAbbreviation(bookId) + ", " + (chapterIndex + 1) + "." + (verseIndex + 1);
	}
}