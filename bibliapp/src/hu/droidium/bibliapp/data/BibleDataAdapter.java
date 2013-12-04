package hu.droidium.bibliapp.data;

public interface BibleDataAdapter {

	String getBookAbbreviation(String bookId);

	String[] getBookIds();

	String getBookTitle(String bookId);

	int getChapterCount(String bookId);

	int getVerseCount(String bookId, int chapterId);

	String getVerseLine(String bookId, int chapterIndex, int verseIndex);

}
