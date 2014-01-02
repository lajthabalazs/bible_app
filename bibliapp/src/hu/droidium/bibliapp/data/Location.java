package hu.droidium.bibliapp.data;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class Location {
	
	private static final String TAG = Location.class.getName();
	private String name;
	private String lat;
	private String lon;
	private String bookId;
	private int chapter;
	private int verse;
	
	protected Location(String name, String lat, String lon, String bookId, int chapter, int verse) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.bookId = bookId;
		this.chapter = chapter;
		this.verse = verse;
	}

	public String getName() {
		return name;
	}

	public String getLat() {
		return lat;
	}

	public String getLon() {
		return lon;
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
		String lat = null;
		String lon = null;
		try {
			name = parts[0];
			lat = parts[1];
			lon = parts[2];
		} catch (Exception e) {
			Log.e(TAG, "Error reading locations from line: " + line + " (" + e.getMessage() + ")", e);
			return null;
		}
		for (int i = 3; i < parts.length; i++) {
			try {
				String[] verseParts = parts[i].split("[[ ]||[:]]");
				String bookAbbreviation = verseParts[0];
				String bookId = bibleDataAdapter.getBookId(bookAbbreviation);
				Location location = new Location(name, lat, lon, bookId, Integer.parseInt(verseParts[1]), Integer.parseInt(verseParts[2]));
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
