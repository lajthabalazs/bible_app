package hu.droidium.bibliapp.data;

import android.util.SparseArray;

public class Chapter {
	
	private SparseArray<Verse> verses = new SparseArray<Verse>();
	private int index;
	private Book book;
	
	public Chapter(Book book, int index) {
		this.book = book;
		this.index = index;
	}

	public Verse getVerse(int verseIndex) {
		try {
			return verses.get(verseIndex);
		} catch (Exception e) {
			return null;
		}
	}

	public void setVerse(int verseIndex, String line) {
		verses.put(verseIndex, new Verse(book, index, verseIndex, line));			
	}
	
	public int getVerseCount() {
		return verses.size();
	}

	public int getIndex() {
		return index;
	}
}