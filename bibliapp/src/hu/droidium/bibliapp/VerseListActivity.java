package hu.droidium.bibliapp;

import hu.droidium.bibliapp.data.AssetReader;
import hu.droidium.bibliapp.data.Book;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class VerseListActivity extends Activity {

	public static final String CHAPTER_INDEX = "Chapter index";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verse_list);
		Intent intent = getIntent();
		String fileName = intent.getStringExtra(ChapterListActivity.BOOK_FILE_NAME);
		int chapterIndex = intent.getIntExtra(CHAPTER_INDEX, -1);
		Book book = AssetReader.readFile(fileName, this);
		((TextView)findViewById(R.id.verseListTitle)).setText(book.getTitle());
		VerseAdapter adapter = new VerseAdapter(book, chapterIndex, getLayoutInflater(), this);
		ListView verseList = (ListView)findViewById(R.id.verseList);
		verseList.setCacheColorHint(Color.TRANSPARENT);
		verseList.setAdapter(adapter);
	}
}
