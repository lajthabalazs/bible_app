package hu.droidium.bibliapp.data;

import java.util.List;

public interface LocationAdapter {
	public List<Location> getLocations(String bookId, int chapter, int verse);
	public List<Location> getAllLocations();
	List<Verse> getVerses(String locationName);
}