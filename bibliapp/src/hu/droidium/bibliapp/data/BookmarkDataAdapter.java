package hu.droidium.bibliapp.data;

import java.util.List;


public interface BookmarkDataAdapter {

	Bookmark saveBookmark(Bookmark bookmark);

	List<Bookmark> getBookmarksForChapter(String book, int chapter);

	List<Bookmark> getAllBookmarks(String sortColumn, boolean sortDesc);

	void deleteBookmark(Bookmark bookmark);
}
