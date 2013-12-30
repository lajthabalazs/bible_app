package hu.droidium.bibliapp.data;


import hu.droidium.bibliapp.database.TagMeta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.util.Log;

public class AssetReader {
	private static final String TAG = "AssetReader";
	private static final String RAW_DIRECTORY = "raw";
	private static final String TAG_META_FILE = "tags.txt";
	private static final String LOCATION_FILE = "locations.txt";

	/**
	 * Reads book titles.
	 * @param context The context to use when accessing assets
	 * @return A vector of arrays containing the book id, the book title and the abbreviation for the book
	 */
	public static Vector<String[]> readTitles(Context context) {
		Vector<String[]> ret = new Vector<String[]>();
		// Read abbreviations
		try {
			BufferedReader abbreviationsFile = new BufferedReader(new InputStreamReader(context.getAssets().open("abbreviations.txt"), "UTF8"));
			Vector<String> abbreviations = new Vector<String>();
			String line = abbreviationsFile.readLine();
			while (line != null){
				abbreviations.add(line);
				line = abbreviationsFile.readLine();
			}
			for (int i = 1; i <= 73; i++){
				String bookId = (i < 10?"0":"") + i;
				String fileName = RAW_DIRECTORY + File.separator + bookId + ".txt";
				try {
					BufferedReader in = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName), "UTF8"));
					ret.add(new String[]{bookId, in.readLine(), abbreviations.get(i - 1)});
				} catch (FileNotFoundException e) {
					System.out.println("File not found "+ fileName);
				}
			}
			return ret;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null; 
		}
	}
	
	public static Book parseBook(String bookId, Context context) {
		String fileName =  RAW_DIRECTORY + File.separator + bookId + ".txt";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName), "UTF8"));
			Book book = new Book(bookId, in);
			return book;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			Log.e(TAG, "Couldn't read " + fileName);
			e.printStackTrace();
			return null;
		}
	}

	public static List<TagMeta> parseTagMetas(Context context) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(context.getAssets().open(TAG_META_FILE), "UTF8"));
			List<TagMeta> tags = new ArrayList<TagMeta>();
			String line = in.readLine();
			while (line != null) {
				TagMeta tag = TagMeta.parse(line);
				if (tag != null) {
					tags.add(tag);
				} else {
					Log.e(TAG, "Couldn't load tag meta from line " + line);
				}
				line = in.readLine();
			}
			return tags;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			Log.e(TAG, "Couldn't read " + TAG_META_FILE);
			e.printStackTrace();
			return null;
		}
	}

	public static List<Location> parseLocations(Context context) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(context.getAssets().open(LOCATION_FILE), "UTF8"));
			List<Location> locations = new ArrayList<Location>();
			String line = in.readLine();
			while (line != null) {
				List<Location> parsedLocations = Location.parse(line);
				if (parsedLocations != null) {
					locations.addAll(parsedLocations);
				} else {
					Log.e(TAG, "Couldn't load location from line " + line);
				}
				line = in.readLine();
			}
			return locations;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			Log.e(TAG, "Couldn't read " + LOCATION_FILE);
			e.printStackTrace();
			return null;
		}
	}

}