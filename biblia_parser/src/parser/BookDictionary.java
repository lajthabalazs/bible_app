package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class BookDictionary {
	HashMap<String, String> dictionary = new HashMap<String, String>();

	public BookDictionary(InputStream source) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(source));
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split("\"");
				String[] firstParts = parts[0].split(",");
				String name = firstParts[0].trim();
				String[] secondParts = parts[1].split(",");
				for (String english : secondParts) {
					english = english.trim();
					if (dictionary.containsKey(english) && (!name.equals(dictionary.get(english)))) {
						System.out.println("Duplicate " + english +" " + name + " " + dictionary.get(english));
					}
					dictionary.put(english, name);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String get(String bookName) {
		return dictionary.get(bookName);
	}
}
