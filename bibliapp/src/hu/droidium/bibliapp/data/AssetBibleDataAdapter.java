package hu.droidium.bibliapp.data;

import java.util.HashMap;
import java.util.Vector;

import android.content.Context;

public class AssetBibleDataAdapter implements BibleDataAdapter {
	
	private Context context;
	private Vector<String[]> assets;
	private Vector<String> bookIds;
	private HashMap<String, String> titleMap;
	private HashMap<String, String> abbreviationMap;
	private HashMap<String, String> idMap;
	private Book lastAccessedBook = null;
	
	public AssetBibleDataAdapter(Context context) {
		this.context = context;
		assets = AssetReader.readTitles(context);
		abbreviationMap = new HashMap<String, String>();
		idMap = new HashMap<String, String>();
		titleMap = new HashMap<String, String>();
		bookIds = new Vector<String>();
		for (String[] assetLine : assets) {
			titleMap.put(assetLine[0], assetLine[1]);
			abbreviationMap.put(assetLine[0], assetLine[2]);
			idMap.put(assetLine[2], assetLine[0]);
			bookIds.add(assetLine[0]);
		}
	} 

	@Override
	public String getBookAbbreviation(String bookId) {
		return abbreviationMap.get(bookId);
	}
	
	@Override
	public String getBookId(String abbreviation) {
		return idMap.get(abbreviation);
	}

	@Override
	public String[] getBookIds() {
		return bookIds.toArray(new String[bookIds.size()]);
	}
	
	@Override
	public String getNextBookId(String bookId) {
		for (int i = 0; i < bookIds.size(); i++) {
			if (bookIds.get(i).equals(bookId)) {
				if (i == bookIds.size() - 1) {
					return null;
				} else {
					return bookIds.get(i + 1);
				}
			}
		}
		return null;
	}

	@Override
	public String getPreviousBookId(String bookId) {
		for (int i = 0; i < bookIds.size(); i++) {
			if (bookIds.get(i).equals(bookId)) {
				if (i == 0) {
					return null;
				} else {
					return bookIds.get(i - 1);
				}
			}
		}
		return null;
	}

	@Override
	public String getBookTitle(String bookId) {
		return titleMap.get(bookId);
	}

	@Override
	public int getChapterCount(String bookId) {
		if (bookId == null) {
			return 0;
		}
		if (lastAccessedBook == null || !lastAccessedBook.getId().equals(bookId)) {
			lastAccessedBook = AssetReader.parseBook(bookId, context);
		}
		if (lastAccessedBook == null) {
			return 0;
		}
		return lastAccessedBook.getChapterCount();
	}

	@Override
	public int getVerseCount(String bookId, int chapterId) {
		if (bookId == null) {
			return 0;
		}
		if (lastAccessedBook == null || !lastAccessedBook.getId().equals(bookId)) {
			lastAccessedBook = AssetReader.parseBook(bookId, context);
		}
		if (lastAccessedBook == null) {
			return 0;
		}
		return lastAccessedBook.getChapter(chapterId).getVerseCount();
	}

	@Override
	public String getVerseLine(String bookId, int chapterIndex, int verseIndex) {
		if (bookId == null) {
			return null;
		}
		if (lastAccessedBook == null || !lastAccessedBook.getId().equals(bookId)) {
			lastAccessedBook = AssetReader.parseBook(bookId, context);
		}
		if (lastAccessedBook == null) {
			return null;
		}
		return lastAccessedBook.getChapter(chapterIndex).getVerse(verseIndex).getLine();
	}
}
