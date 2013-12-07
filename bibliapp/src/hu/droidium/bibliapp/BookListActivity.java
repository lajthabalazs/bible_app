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
		SharedPreferences prefs = Constants.getPrefs(this);
		boolean shouldOpenLastRead = prefs.getBoolean(Constants.SHOULD_OPEN_LAST_READ, false);		
		if (shouldOpenLastRead) {
			log(R.string.flurryEventContinuedFromLastTime);
			String bookId = prefs.getString(Constants.LAST_READ_BOOK_ID, null);
			if (bookId.startsWith("raw")) {
				bookId = bookId.substring(4,6);
				prefs.edit().putString(Constants.LAST_READ_BOOK_ID, bookId).commit();
			}
			Intent nextIntent = new Intent(this, ChapterListActivity.class);
			nextIntent.putExtra(Constants.BOOK_ID, bookId);
			startActivity(nextIntent);
		} else {
			log(R.string.flurryEventBooksListed);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int selectedIndex, long id) {
		String bookId = (String)adapter.getItem(selectedIndex);
		Intent intent = new Intent(this, ChapterListActivity.class);
		intent.putExtra(Constants.BOOK_ID, bookId);
		startActivity(intent);
	}

	@Override
	protected void facebookSessionOpened() {
	}

	@Override
	protected void facebookSessionClosed() {
	}

	@Override
	public void onClick(View v) {
		Map<String, String> params = new HashMap<String, String>();
		params.put(getString(R.string.flurryParamEventSource), BookListActivity.class.getName());
		log(R.string.flurryEventBookmarksOpened, params );
		Intent intent = new Intent(this, BookmarkListActivity.class);
		startActivity(intent);
	}
}
