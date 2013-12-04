package hu.droidium.bibliapp.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import android.content.Context;
import android.util.Log;

public class AssetReader {
	private static final String TAG = "AssetReader";
	private static final String RAW_DIRECTORY = "raw";

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
	
}