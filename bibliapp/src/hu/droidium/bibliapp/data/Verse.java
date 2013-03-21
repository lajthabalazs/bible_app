package hu.droidium.bibliapp.data;

public class Verse {

	private String line;
	private int verseIndex;
	
	public Verse(int verseIndex, String line) {
		this.setLine(line);
		this.setVerseIndex(verseIndex);
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public int getVerseIndex() {
		return verseIndex;
	}

	public void setVerseIndex(int verseIndex) {
		this.verseIndex = verseIndex;
	}
}
