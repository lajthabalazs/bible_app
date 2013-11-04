package hu.droidium.bibliapp.data;

import hu.droidium.bibliapp.database.DatabaseManager;
import hu.droidium.bibliapp.database.TagMeta;

import java.util.ArrayList;
import java.util.List;

public class Verse {

	private Book book;
	private String line;
	private int verseIndex;
	private int chapterIndex;
	
	public Verse(Book book, int chapterIndex, int verseIndex, String line) {
		this.book = book;
		this.chapterIndex = chapterIndex;
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
		return book.getAbbreviation() + ", " + (chapterIndex + 1) + "." + (verseIndex + 1);
	}

	public ArrayList<String> getTagColors(DatabaseManager databaseManager) {
		List<TagMeta> tags = databaseManager.getTags(book.getId(), chapterIndex, verseIndex);
		ArrayList<String> colors = new ArrayList<String>();
		for (TagMeta tag : tags){
			colors.add(tag.getColor());
		}
		return colors;
	}
}
