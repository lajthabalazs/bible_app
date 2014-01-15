package hu.droidium.bibliapp;

import java.util.HashMap;
import java.util.Map;

import hu.droidium.bibliapp.bookmar_ui.BookmarkListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class BookListActivity extends BibleBaseActivity implements OnItemClickListener, OnClickListener {

	private BookTitleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_list);
		TextView bookmarksLink = (TextView) findViewById(R.id.bookmarkLink);
		bookmarksLink.setOnClickListener(this);
		adapter = new BookTitleAdapter(getLayoutInflater(), bibleDataAdapter, this);
		ListView bookList = (ListView) findViewById(R.id.bookList);
		bookList.setCacheColorHint(Color.TRANSPARENT);
		bookList.setAdapter(adapter);
		bookList.setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Checks if a book, chapter or verse should be opened.
		SharedPreferences prefs = Constants.getPrefs(this);
		boolean shouldOpenBook = prefs.getBoolean(Constants.SHOULD_OPEN_BOOK, false);
		boolean shouldOpenChapter = prefs.getBoolean(Constants.SHOULD_OPEN_CHAPTER, false);
		boolean shouldOpenVerse = prefs.getBoolean(Constants.SHOULD_OPEN_VERSE, false);		
		String bookId = prefs.getString(Constants.BOOK_ID_TO_OPEN, null);
		if (bookId != null && (shouldOpenBook || shouldOpenChapter || shouldOpenVerse)) {
			Intent nextIntent = new Intent(this, ChapterListActivity.class);
			startActivity(nextIntent);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int selectedIndex, long id) {
		String bookId = (String)adapter.getItem(selectedIndex);
		SharedPreferences prefs = Constants.getPrefs(this);
		prefs.edit()
			.putString(Constants.BOOK_ID_TO_OPEN, bookId)
			.putInt(Constants.CHAPTER_INDEX_TO_OPEN, 0)
			.putInt(Constants.VERSE_INDEX_TO_OPEN, 0)
			.putBoolean(Constants.SHOULD_OPEN_BOOK, true)
			.putBoolean(Constants.SHOULD_OPEN_CHAPTER, false)
			.putBoolean(Constants.SHOULD_OPEN_VERSE, false)
			
			.commit();
		startActivity(new Intent(this, ChapterListActivity.class));
	}

	@Override
	public void onClick(View v) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(getString(R.string.flurryParamEventSource), BookListActivity.class.getName());
		log(R.string.flurryEventBookmarksOpened, params);
		Intent intent = new Intent(this, BookmarkListActivity.class);
		startActivity(intent);
	}
}
