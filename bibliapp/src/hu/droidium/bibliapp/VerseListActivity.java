package hu.droidium.bibliapp;

import hu.droidium.bibliapp.data.Bookmark;

import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class VerseListActivity extends BibleBaseActivity implements
		OnItemClickListener, OnGestureListener, OnTouchListener {

	private static final String TAG = VerseListActivity.class.getName();
	private String bookId;
	private VerseAdapter adapter;
	private ListView verseList;
	private int chapterIndex;
	private GestureDetector gestureDetector;
	private float screenPixels;
	private double screenInches;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verse_list);
		Intent intent = getIntent();
		bookId = intent.getStringExtra(Constants.BOOK_ID);
		chapterIndex = intent.getIntExtra(Constants.CHAPTER_INDEX, 0);
		((TextView) findViewById(R.id.activityTitle)).setText(bibleDataAdapter
				.getBookTitle(bookId));
		List<Bookmark> bookmarks = bookmarkDataAdapter.getBookmarksForChapter(
				bookId, chapterIndex);
		adapter = new VerseAdapter(bookId, chapterIndex, bookmarks,
				getLayoutInflater(), this, bibleDataAdapter, tagDataAdapter,
				locationAdapter);
		verseList = (ListView) findViewById(R.id.verseList);
		verseList.setCacheColorHint(Color.TRANSPARENT);
		verseList.setAdapter(adapter);
		verseList.setOnItemClickListener(this);
		verseList.setOnTouchListener(this);
		SharedPreferences prefs = Constants.getPrefs(this);
		int openedCount = prefs
				.getInt(Constants.VERSE_LIST_OPENED_COUNT_KEY, 0);
		openedCount++;
		prefs.edit().putInt(Constants.VERSE_LIST_OPENED_COUNT_KEY, openedCount)
				.commit();
		boolean shouldOpenLastRead = prefs.getBoolean(
				Constants.SHOULD_OPEN_LAST_READ, false);
		if (intent.hasExtra(Constants.VERSE_INDEX)) {
			int verseIndex = intent.getIntExtra(Constants.VERSE_INDEX, 0);
			verseList.setSelection(verseIndex);
		} else if (shouldOpenLastRead) {
			prefs.edit().remove(Constants.SHOULD_OPEN_LAST_READ).commit();
			int verseIndex = prefs.getInt(Constants.LAST_READ_VERS, 0);
			verseList.setSelection(verseIndex);
		}
		gestureDetector = new GestureDetector(this, this);
		Display display = getWindowManager().getDefaultDisplay();
		screenPixels = display.getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
		double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
		screenInches = Math.sqrt(x + y);
		Log.d("debug", "Screen inches : " + screenInches);
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
	public void onItemClick(AdapterView<?> parentView, View view,
			int itemIndex, long itemId) {
		adapter.showOptions(view, itemId, super.isFacebookSessionOpened());
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		float physicalVelocityInch = (float)(velocityX / screenPixels * screenInches);
		if (Math.abs(velocityX) > Math.abs(velocityY) * 2) {
			if (physicalVelocityInch > 5) {
				if (chapterIndex != 0) {
					Intent previusChapterIntent = new Intent(this, VerseListActivity.class);
					previusChapterIntent.putExtra(Constants.BOOK_ID, bookId);
					previusChapterIntent.putExtra(Constants.CHAPTER_INDEX, chapterIndex - 1);
					previusChapterIntent.putExtra(Constants.VERSE_INDEX, bibleDataAdapter.getVerseCount(bookId, chapterIndex - 1) - 1);
					startActivity(previusChapterIntent);
					overridePendingTransition(R.anim.activity_slide_in_from_left,
							R.anim.activity_slide_out_right);
					finish();
				} else {
					// TODO Go to previous book
					finish();
				}
			} else if (physicalVelocityInch < -5) {
				if (chapterIndex == bibleDataAdapter.getChapterCount(bookId) - 1) {
					// TODO Go to next book
					finish();
				} else {
					Intent nextChapterIntent = new Intent(this, VerseListActivity.class);
					nextChapterIntent.putExtra(Constants.BOOK_ID, bookId);
					nextChapterIntent.putExtra(Constants.CHAPTER_INDEX, chapterIndex + 1);
					nextChapterIntent.putExtra(Constants.VERSE_INDEX, 0);
					startActivity(nextChapterIntent);
					overridePendingTransition(R.anim.activity_slide_in_from_right,
							R.anim.activity_slide_out_left);
					finish();
				}
			}
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
}
