package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Main {
	public static void main(String[] args) {
		List<PlaceMark> points = parseMap();
		BookDictionary dictionary = parseAbbreviations();
		for (PlaceMark point : points) {
			point.translateVerses(dictionary);
		}
		try {
			MapParser.export(points, new FileOutputStream("e:\\git_local\\bible_app\\content\\map\\locations.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static BookDictionary parseAbbreviations() {
		File sourceFile= new File("e:\\git_local\\bible_app\\content\\bible_books.csv");
		try {
			BookDictionary dictionary = new BookDictionary(new FileInputStream(sourceFile));
			return dictionary;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<PlaceMark> parseMap(){
		File sourceFile= new File("e:\\git_local\\bible_app\\content\\map\\english.kml");
		try {
			MapParser parser = new MapParser(new FileInputStream(sourceFile));
			TreeSet<PlaceMark> places = new TreeSet<PlaceMark>();
			places.addAll(parser.getMarks());
			System.out.println(places.size());
			List<PlaceMark> cleanedMarks = new ArrayList<PlaceMark>();
			PlaceMark actualMark = null;
			for (PlaceMark nextPlace : places) {
				if (actualMark == null) {
					actualMark = nextPlace;
					continue;
				} else {
					if (actualMark.equals(nextPlace)) {
						System.out.println(actualMark);
						actualMark = actualMark.merge(nextPlace);
					} else {
						cleanedMarks.add(actualMark);
						actualMark = nextPlace;
					}
				}
			}
			if (actualMark != null) {
				cleanedMarks.add(actualMark);
			}
			System.out.println(cleanedMarks.size());
			return cleanedMarks;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void parse(){
		File sourceFolder= new File("e:\\git_local\\bible_app\\content");
		File outputFolder = new File("e:\\git_local\\bible_app\\content\\raw");
		File xmlFolder = new File("e:\\git_local\\bible_app\\content\\xml");
		Parser.parseAll(sourceFolder, outputFolder, xmlFolder);
	}
}
