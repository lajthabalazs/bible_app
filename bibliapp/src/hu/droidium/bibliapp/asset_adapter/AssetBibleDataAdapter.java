package hu.droidium.bibliapp.asset_adapter;

import java.util.HashMap;
import java.util.Vector;

import android.content.Context;
import hu.droidium.bibliapp.data.AssetReader;
import hu.droidium.bibliapp.data.BibleDataAdapter;
import hu.droidium.bibliapp.data.Book;

public class AssetBibleDataAdapter implements BibleDataAdapter {
	
	private Context context;
	private Vector<String[]> assets;
	private Vector<String> bookIds;
	private HashMap<String, String> titleMap;
	private HashMap<String, String> abbreviationMap;
	private Book lastAccessedBook = null;
	
	public AssetBibleDataAdapter(Context context) {
		this.context = context;
		assets = AssetReader.readTitles(context);
		abbreviationMap = new HashMap<String, String>();
		titleMap = new HashMap<String, String>();
		bookIds = new Vector<String>();
		for (String[] assetLine : assets) {
			titleMap.put(assetLine[0], assetLine[1]);
			abbreviationMap.put(assetLine[0], assetLine[2]);
			bookIds.add(assetLine[0]);
		}
	} 

	@Override
	public String getBookAbbreviation(String bookId) {
		return abbreviationMap.get(bookId);
	}

	@Override
	public String[] getBookIds() {
		return bookIds.toArray(new String[bookIds.size()]);
	}

	@Override
	public String getBookTitle(String bookId) {
		return titleMap.get(bookId);
	}

	@Override
	public int getChapterCount(String bookId) {
		if (lastAccessedBook == null || !lastAccessedBook.getId().equals(bookId)) {
			lastAccessedBook = AssetReader.parseBook(bookId, context);
		}
		return lastAccessedBook.getChapterCount();
	}

	@Override
	public int getVerseCount(String bookId, int chapterId) {
		if (lastAccessedBook == null || !lastAccessedBook.getId().equals(bookId)) {
			lastAccessedBook = AssetReader.parseBook(bookId, context);
		}
		return lastAccessedBook.getChapter(chapterId).getVerseCount();
	}

	@Override
	public String getVerseLine(String bookId, int chapterIndex, int verseIndex) {
		if (lastAccessedBook == null || !lastAccessedBook.getId().equals(bookId)) {
			lastAccessedBook = AssetReader.parseBook(bookId, context);
		}
		return lastAccessedBook.getChapter(chapterIndex).getVerse(verseIndex).getLine();
	}

}
