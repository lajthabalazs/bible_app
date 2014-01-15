package hu.droidium.bibliapp;

import hu.droidium.bibliapp.data.Bookmark;
import hu.droidium.flurry_base.Log;

import java.util.List;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VerseListActivity extends BibleBaseActivity implements
		OnItemClickListener {

	private static final String TAG = VerseListActivity.class.getName();
	private String bookId;
	private VerseAdapter adapter;
	private ListView verseList;
	private int chapterIndex;
	private TextView titleView;
	private int verseIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verse_list);
		titleView = ((TextView) findViewById(R.id.activityTitle));
		adapter = new VerseAdapter(getLayoutInflater(), this, bibleDataAdapter, tagDataAdapter,
				locationAdapter);
		verseList = (ListView) findViewById(R.id.verseList);
		verseList.setCacheColorHint(Color.TRANSPARENT);
		verseList.setAdapter(adapter);
		verseList.setOnItemClickListener(this);
		verseList.setOnTouchListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences prefs = Constants.getPrefs(this);
		int openedCount = prefs.getInt(Constants.VERSE_LIST_OPENED_COUNT_KEY, 0);
		openedCount++;
		prefs.edit()
			.putInt(Constants.VERSE_LIST_OPENED_COUNT_KEY, openedCount)
			.commit();

		bookId = prefs.getString(Constants.BOOK_ID_TO_OPEN, null);
		chapterIndex = prefs.getInt(Constants.CHAPTER_INDEX_TO_OPEN, -1);
		prefs.edit().remove(Constants.SHOULD_OPEN_CHAPTER).commit(); // Removes executed command from preferences
		if (prefs.getBoolean(Constants.SHOULD_OPEN_VERSE, false)) {
			prefs.edit().remove(Constants.SHOULD_OPEN_VERSE).commit(); // Removes done command from preferences
			verseIndex = prefs.getInt(Constants.VERSE_INDEX_TO_OPEN, -1);
		} else {
			verseIndex = -1;
		}
		refresh();
		// Remove commands from shared preferences
		prefs.edit()
				.remove(Constants.SHOULD_OPEN_BOOK)
				.remove(Constants.SHOULD_OPEN_CHAPTER)
				.remove(Constants.SHOULD_OPEN_VERSE)
				.commit();
	}
	
	private void refresh(){
		titleView.setText(bibleDataAdapter.getBookTitle(bookId));
		List<Bookmark> bookmarks = bookmarkDataAdapter.getBookmarksForChapter(
				bookId, chapterIndex);
		adapter.setData(bookId, chapterIndex, bookmarks);
		if (verseIndex != -1) {
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
	public void onItemClick(AdapterView<?> parentView, View view,
			int itemIndex, long itemId) {
		adapter.showOptions(view, itemId, super.isFacebookSessionOpened());
	}

	@Override
	protected boolean flingRight() {
		if (chapterIndex == bibleDataAdapter.getChapterCount(bookId) - 1) {
			Log.e(TAG, "Go to next book.");
			String nextBookId = bibleDataAdapter.getNextBookId(bookId);
			if (nextBookId != null) {
				SharedPreferences prefs = Constants.getPrefs(this);
				bookId = nextBookId;
				chapterIndex = 0;
				verseIndex = 0;
				prefs.edit()
					.putString(Constants.BOOK_ID_TO_OPEN, nextBookId)
					.putInt(Constants.CHAPTER_INDEX_TO_OPEN, chapterIndex)
					.putInt(Constants.VERSE_INDEX_TO_OPEN, verseIndex)
					.commit();
				refresh();
			} else {
				Toast.makeText(this, R.string.atFirstChapter, Toast.LENGTH_LONG).show();
			}
		} else {
			chapterIndex = chapterIndex + 1;
			verseIndex = 0;
			SharedPreferences prefs = Constants.getPrefs(this);
			prefs.edit()
				.putInt(Constants.CHAPTER_INDEX_TO_OPEN, chapterIndex)
				.putInt(Constants.VERSE_INDEX_TO_OPEN, verseIndex)
				.commit();
			refresh();
		}
		return true;
	}

	@Override
	protected boolean flingLeft() {
		if (chapterIndex == 0) {
			Log.e(TAG, "Go to previous book.");
			String previousBookId = bibleDataAdapter.getPreviousBookId(bookId);			
			if (previousBookId != null) {
				chapterIndex = bibleDataAdapter.getChapterCount(previousBookId) - 1;
				verseIndex = bibleDataAdapter.getVerseCount(previousBookId, chapterIndex) - 1;
				bookId = previousBookId;
				SharedPreferences prefs = Constants.getPrefs(this);
				prefs.edit()
					.putString(Constants.BOOK_ID_TO_OPEN, previousBookId)
					.putInt(Constants.CHAPTER_INDEX_TO_OPEN, chapterIndex)
					.putInt(Constants.VERSE_INDEX_TO_OPEN, verseIndex)
					.commit();
				refresh();
			} else {
				Toast.makeText(this, R.string.atLastChapter, Toast.LENGTH_LONG).show();
			}
		} else {
			chapterIndex = chapterIndex - 1;
			verseIndex = bibleDataAdapter.getVerseCount(bookId, chapterIndex) - 1;
			SharedPreferences prefs = Constants.getPrefs(this);
			prefs.edit()
				.putInt(Constants.CHAPTER_INDEX_TO_OPEN, chapterIndex)
				.putInt(Constants.VERSE_INDEX_TO_OPEN, verseIndex)
				.commit();
			refresh();
		}
		return true;
	}	
}
