package hu.droidium.bibliapp;

import java.util.List;

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

	@SuppressWarnings("unused")
	private static final String TAG = VerseListActivity.class.getName();
	private String bookId;
	private VerseAdapter adapter;
	private ListView verseList;
	private int chapterIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verse_list);
		Intent intent = getIntent();
		bookId = intent.getStringExtra(Constants.BOOK_ID);
		chapterIndex = intent.getIntExtra(Constants.CHAPTER_INDEX, 0);
		((TextView)findViewById(R.id.activityTitle)).setText(bibleDataAdapter.getBookTitle(bookId));
		List<Bookmark> bookmarks = bookmarkDataAdapter.getBookmarksForChapter(bookId, chapterIndex);
		adapter = new VerseAdapter(bookId, chapterIndex, bookmarks, getLayoutInflater(), this, bibleDataAdapter, tagDataAdapter);
		verseList = (ListView)findViewById(R.id.verseList);
		verseList.setCacheColorHint(Color.TRANSPARENT);
		verseList.setAdapter(adapter);
		verseList.setOnItemClickListener(this);
		SharedPreferences prefs = Constants.getPrefs(this);
		boolean shouldOpenLastRead = prefs.getBoolean(Constants.SHOULD_OPEN_LAST_READ, false);
		if (intent.hasExtra(Constants.VERSE_INDEX)) {
			int verseIndex = intent.getIntExtra(Constants.VERSE_INDEX,0);
			verseList.setSelection(verseIndex);
		} else if (shouldOpenLastRead) {
			prefs.edit().remove(Constants.SHOULD_OPEN_LAST_READ).commit();
			int verseIndex = prefs.getInt(Constants.LAST_READ_VERS, 0);
			verseList.setSelection(verseIndex);
		}
	}
	
	@Override
	protected void onPause() {
		int visibleVers = verseList.getFirstVisiblePosition();
		Constants.savePosition(bookId, chapterIndex, visibleVers, this);
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
