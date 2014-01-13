package hu.droidium.bibliapp.data;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class Location {
	
	private static final String TAG = Location.class.getName();
	private String name;
	private String lon;
	private String lat;
	private String bookId;
	private int chapter;
	private int verse;
	
	protected Location(String name, String lon, String lat, String bookId, int chapter, int verse) {
		this.name = name;
		this.lon = lon;
		this.lat = lat;
		this.bookId = bookId;
		this.chapter = chapter;
		this.verse = verse;
	}

	public String getName() {
		return name;
	}

	public String getLon() {
		return lon;
	}

	public String getLat() {
		return lat;
	}

	public String getBookId() {
		return bookId;
	}

	public int getChapter() {
		return chapter;
	}

	public int getVerse() {
		return verse;
	}

	public static List<Location> parse(String line, BibleDataAdapter bibleDataAdapter) {
		List<Location> locations = new ArrayList<Location>();
		String[] parts = line.split(",");
		String name = null;
		String lon = null;
		String lat = null;
		try {
			name = parts[0];
			lon = parts[2];
			lat = parts[1];
		} catch (Exception e) {
			Log.e(TAG, "Error reading locations from line: " + line + " (" + e.getMessage() + ")", e);
			return null;
		}
		for (int i = 3; i < parts.length; i++) {
			try {
				String[] verseParts = parts[i].split("[[ ]||[:]]");
				String bookAbbreviation = verseParts[0];
				String bookId = bibleDataAdapter.getBookId(bookAbbreviation);
				Location location = new Location(name, lon, lat, bookId, Integer.parseInt(verseParts[1]), Integer.parseInt(verseParts[2]));
				locations.add(location);
			} catch (Exception e) {
				
			}
		}
		return locations;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
