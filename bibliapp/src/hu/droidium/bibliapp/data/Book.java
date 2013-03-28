package hu.droidium.bibliapp.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

public class Book {
	private String title;
	private Vector<Chapter> chapters = new Vector<Chapter>();
	//HashMap<String, String> verses = new HashMap<String, String>();
	

	public Book(BufferedReader in) throws IOException {
		boolean firstLine = true;
		int chapterIndex = -1;
		int verseIndex = -1;
		for (String line = in.readLine(); line != null; line = in.readLine()) {
			if (firstLine) {
				firstLine = false;
				title = line;
			} else if (line.startsWith("fa4dh6fed ")){
				String key = line.substring("fa4dh6fed ".length());
				String[] parts = key.split(",");
				chapterIndex = Integer.parseInt(parts[0]) - 1;
				verseIndex = Integer.parseInt(parts[1]) - 1;
			} else {
				if (chapterIndex != -1) {
					Verse verse = null;
					Chapter chapter = null;
					try {
						chapter = chapters.get(chapterIndex);
					} catch(Exception e) {
					}
					if (chapter==null) {
						chapter = new Chapter(this, chapterIndex);
						chapters.add(chapter);
					} else{
						verse = chapter.getVerse(verseIndex);
					}
					if (verse == null) {
						chapter.setVerse(verseIndex, line);	
					} else {
						chapter.setVerse(verseIndex, verse + "\n" + line);
					}
					chapterIndex = -1;
				}
			}
		}
	}


	public Chapter getChapter(int position) {
		return chapters.get(position);
	}


	public int getChapterCount() {
		return chapters.size();
	}


	public String getTitle() {
		return title;
	}


	public String getAbbreviation() {
		return title;
	}
}
