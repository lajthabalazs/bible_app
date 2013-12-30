package parser;

import java.util.ArrayList;
import java.util.List;

public class PlaceMark implements Comparable<PlaceMark>{
	public String name;
	public List<String> verses = new ArrayList<String>();
	public String lat;
	public String lon;

	public void setName(String string) {
		string = string.trim();
		name = string.replaceAll("[\\W&&[^\\s]&&[^']&&[^\\-]]", "");
	}

	public void setCoords(String string) {
		string = string.trim();
		String[] parts = string.split(",");
		lat = parts[0];
		lon = parts[1];
	}

	public void setVerses(String string) {
		while (true) {
			int start = string.indexOf(">");
			string = string.substring(start + 1);
			int end = string.indexOf("<");
			String verse = string.substring(0, end);
			verses.add(verse);
			if (string.length() > end + 7) {
				string = string.substring(end + 7);
			} else {
				break;
			}
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PlaceMark) {
			PlaceMark o = (PlaceMark) obj;
			return name.equals(o.name) && lat.equals(o.lat) && lon.equals(o.lon);
		} else {
			return false;
		}
	}
	
	public PlaceMark merge(PlaceMark o) {
		if  (!this.equals(o)){
			throw new RuntimeException("Places are not equal cannot be merged.");
		}
		PlaceMark ret = new PlaceMark();
		ret.name = name;
		ret.lat = lat;
		ret.lon = lon;
		ret.verses.addAll(this.verses);
		ret.verses.addAll(o.verses);
		return ret;
	}

	@Override
	public int compareTo(PlaceMark o) {
		return name.compareTo(o.name);
	}

	public void printVerses() {
		for (String verse : verses) {
			System.out.println(verse);
		}
	}
	
	public void translateVerses(BookDictionary dictionary) {
		List<String> translated = new ArrayList<String>();
		for (String verse : verses) {
			int space = verse.lastIndexOf(" ");
			String bookName = verse.substring(0, verse.lastIndexOf(" "));
			translated.add(dictionary.get(bookName) + " " + verse.substring(space + 1));
			if (dictionary.get(bookName) == null) {
				System.out.println("Unknown >" + bookName + "<" + verse + " " + name);
			}
		}
		verses = translated;
	}

	public String getOutput() {
		String ret = name + "," + lat + "," + lon;
		for (String verse : verses) {
			ret = ret + "," + verse;
		}
		return ret;
	}
}