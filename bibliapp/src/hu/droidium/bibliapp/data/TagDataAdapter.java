package hu.droidium.bibliapp.data;

import hu.droidium.bibliapp.database.TagMeta;

import java.util.ArrayList;
import java.util.List;

public interface TagDataAdapter {

	List<TagMeta> getTagMetas();

	ArrayList<String> getTagColors(String bookId, int chapterIndex,
			int verseIndex);

	List<TagMeta> getTags(String book, int chapterIndex, int verseIndex);

	void removeTag(String id, String bookId, int chapterIndex, int verseIndex);

	void addTag(String id, String bookId, int chapterIndex, int verseIndex);

}
