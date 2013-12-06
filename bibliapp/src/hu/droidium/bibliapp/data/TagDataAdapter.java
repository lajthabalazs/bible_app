package hu.droidium.bibliapp.data;

import hu.droidium.bibliapp.database.Tag;
import hu.droidium.bibliapp.database.TagMeta;

import java.util.ArrayList;
import java.util.List;

public interface TagDataAdapter {

	List<TagMeta> getTagMetas();

	ArrayList<String> getTagColors(String bookId, int chapterIndex,
			int verseIndex);

	List<TagMeta> getTags(String book, int chapterIndex, int verseIndex);

	boolean removeTag(String id, String bookId, int chapterIndex, int verseIndex);

	boolean addTag(String id, String bookId, int chapterIndex, int verseIndex);
	
	List<Tag> getTags(String tagMetaId);
	
	int getTotalTags();
	
	int getTotalTags(String tagMetaId);
}
