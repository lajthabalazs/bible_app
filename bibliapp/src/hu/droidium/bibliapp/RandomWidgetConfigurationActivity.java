package hu.droidium.bibliapp;

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

public class RandomWidgetConfigurationActivity extends BibleBaseActivity implements OnItemClickListener, OnClickListener {

	private BookTitleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		setContentView(R.layout.book_list);
		TextView bookmarksLink = (TextView) findViewById(R.id.bookmarkLink);
		bookmarksLink.setOnClickListener(this);
		ListView bookList = (ListView) findViewById(R.id.bookList);
		bookList.setCacheColorHint(Color.TRANSPARENT);
		bookList.setAdapter(adapter);
		bookList.setOnItemClickListener(this);
		if (intent.hasExtra(Constants.SHOULD_OPEN_LAST_READ)) {
			SharedPreferences prefs = Constants.getPrefs(this);
			String fileName = prefs.getString(Constants.LAST_READ_BOOK, null);
			Intent nextIntent = new Intent(this, ChapterListActivity.class);
			nextIntent.putExtra(Constants.BOOK_FILE_NAME, fileName);
			nextIntent.putExtra(Constants.SHOULD_OPEN_LAST_READ, true);
			startActivity(nextIntent);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int selectedIndex, long id) {
		String fileName = ((String[]) adapter.getItem(selectedIndex))[0];
		Intent intent = new Intent(this, ChapterListActivity.class);
		intent.putExtra(Constants.BOOK_FILE_NAME, fileName);
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
		Intent intent = new Intent(this, BookmarkListActivity.class);
		startActivity(intent);
	}
}