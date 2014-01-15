package hu.droidium.bibliapp;

import hu.droidium.flurry_base.Log;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ChapterListActivity extends BibleBaseActivity implements OnItemClickListener {

	private static final String TAG = ChapterListActivity.class.getName();
	private ChapterAdapter adapter;
	private String bookId;
	private int chapterIndex;
	private ListView chapterList;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chapter_list);
		adapter = new ChapterAdapter(bibleDataAdapter, getLayoutInflater(), this);		
		chapterList = (ListView)findViewById(R.id.chapterList);
		chapterList.setCacheColorHint(Color.TRANSPARENT);
		chapterList.setAdapter(adapter);
		chapterList.setOnItemClickListener(this);
		chapterList.setOnTouchListener(this);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences prefs = Constants.getPrefs(this);
		bookId = prefs.getString(Constants.BOOK_ID_TO_OPEN, null);
		chapterIndex = prefs.getInt(Constants.CHAPTER_INDEX_TO_OPEN, -1);
		refresh();
		// Check if verse list should be opened
		boolean shouldOpenChapter = prefs.getBoolean(Constants.SHOULD_OPEN_CHAPTER, false);
		boolean shouldOpenVerse = prefs.getBoolean(Constants.SHOULD_OPEN_VERSE, false);
		Log.e(TAG, "Should open chapter " + shouldOpenChapter + " verse " + shouldOpenVerse);
		prefs.edit().remove(Constants.SHOULD_OPEN_BOOK).commit(); // Removes executed command from preferences
		if (bookId != null && (shouldOpenChapter || shouldOpenVerse)) {
			Intent nextIntent = new Intent(this, VerseListActivity.class);
			startActivity(nextIntent);
		}
	}
	
	private void refresh() {
		((TextView)findViewById(R.id.activityTitle)).setText(bibleDataAdapter.getBookTitle(bookId));
		adapter.setBookId(bookId);
		if (chapterIndex != -1) {
			chapterList.setSelection(chapterIndex);
		}

	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int selectedIndex, long id) {
		SharedPreferences prefs = Constants.getPrefs(this);
		Log.e(TAG, "Item clicked");
		prefs.edit()
			.putInt(Constants.CHAPTER_INDEX_TO_OPEN, selectedIndex)
			.putInt(Constants.VERSE_INDEX_TO_OPEN, 0)
			.putBoolean(Constants.SHOULD_OPEN_BOOK, false)
			.putBoolean(Constants.SHOULD_OPEN_CHAPTER, true)
			.putBoolean(Constants.SHOULD_OPEN_VERSE, false)
			.commit();
		startActivity(new Intent(this, VerseListActivity.class));
	}

	@Override
	protected boolean flingRight() {
		Log.e(TAG, "Go to next book.");
		String nextBookId = bibleDataAdapter.getNextBookId(bookId);
		if (nextBookId != null) {
			SharedPreferences prefs = Constants.getPrefs(this);
			bookId = nextBookId;
			prefs.edit()
				.putString(Constants.BOOK_ID_TO_OPEN, nextBookId)
				.putInt(Constants.CHAPTER_INDEX_TO_OPEN, 0)
				.putInt(Constants.VERSE_INDEX_TO_OPEN, 0)
				.commit();
			refresh();
		} else {
			Toast.makeText(this, R.string.atFirstChapter, Toast.LENGTH_LONG).show();
		}
		return true;
	}

	@Override
	protected boolean flingLeft() {
		Log.e(TAG, "Go to previous book.");
		String previousBookId = bibleDataAdapter.getPreviousBookId(bookId);			
		if (previousBookId != null) {
			int lastChapter = bibleDataAdapter.getChapterCount(previousBookId) - 1;
			int lastVerse = bibleDataAdapter.getVerseCount(previousBookId, lastChapter) - 1;
			SharedPreferences prefs = Constants.getPrefs(this);
			bookId = previousBookId;
			prefs.edit()
				.putString(Constants.BOOK_ID_TO_OPEN, previousBookId)
				.putInt(Constants.CHAPTER_INDEX_TO_OPEN, lastChapter)
				.putInt(Constants.VERSE_INDEX_TO_OPEN, lastVerse)
				.commit();
			refresh();
		} else {
			Toast.makeText(this, R.string.atLastChapter, Toast.LENGTH_LONG).show();
		}
		return true;
	}	
}