package hu.droidium.bibliapp.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import android.content.Context;

public class AssetReader {
	public static Vector<String[]> readTitles(Context context) {
		Vector<String[]> ret = new Vector<String[]>();
		try {
			for (int i = 1; i <= 73; i++){
				String fileName = null;
				try {
					fileName = "raw" + File.separator + (i < 10?"0":"") + i + ".txt";
					BufferedReader in = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName), "UTF8"));
					ret.add(new String[]{fileName, in.readLine()});
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
	
	public static Book readFile(String fileName, Context context) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName), "UTF8"));
			Book book = new Book(fileName, in);
			return book;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}