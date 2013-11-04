package hu.droidium.bibliapp;

import java.util.List;

import hu.droidium.bibliapp.data.AssetReader;
import hu.droidium.bibliapp.data.Book;
import hu.droidium.bibliapp.database.Bookmark;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class VerseListActivity extends BibleBaseActivity implements OnItemClickListener {

	private VerseAdapter adapter;
	private ListView verseList;
	private String fileName;
	private int chapterIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verse_list);
		Intent intent = getIntent();
		fileName = intent.getStringExtra(Constants.BOOK_FILE_NAME);
		chapterIndex = intent.getIntExtra(Constants.CHAPTER_INDEX, 0);
		Book book = AssetReader.readFile(fileName, this);
		((TextView)findViewById(R.id.activityTitle)).setText(book.getTitle());
		List<Bookmark> bookmarks = getBookmarksForChapter(book.getId(), chapterIndex);
		adapter = new VerseAdapter(book, chapterIndex, bookmarks, getLayoutInflater(), this, databaseManager);
		verseList = (ListView)findViewById(R.id.verseList);
		verseList.setCacheColorHint(Color.TRANSPARENT);
		verseList.setAdapter(adapter);
		verseList.setOnItemClickListener(this);
		if (intent.hasExtra(Constants.VERSE_INDEX)) {
			int verseIndex = intent.getIntExtra(Constants.VERSE_INDEX,0);
			verseList.setSelection(verseIndex);
		} else if (intent.hasExtra(Constants.SHOULD_OPEN_LAST_READ)) {
			SharedPreferences prefs = Constants.getPrefs(this);
			int verseIndex = prefs.getInt(Constants.LAST_READ_VERS, 0);
			
			verseList.setSelection(verseIndex);
		}
	}
	
	@Override
	protected void onPause() {
		int visibleVers = verseList.getFirstVisiblePosition();
		Constants.savePosition(fileName, chapterIndex, visibleVers, this);
		super.onPause();
	}

	@Override
	protected void facebookSessionOpened() {
	}

	@Override
	protected void facebookSessionClosed() {
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View view, int itemIndex, long itemId) {
		adapter.showOptions(view, itemId, super.isFacebookSessionOpened());
	}
}
