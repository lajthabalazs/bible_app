package hu.droidium.bibliapp;

import hu.droidium.bibliapp.bookmar_ui.BookmarkListActivity;
import hu.droidium.bibliapp.tag_ui.TagMetaListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends BibleBaseActivity implements OnClickListener {

	private Button lastReadVers;
	private Button toBookList;
	private View bookmarks;
	private Button settings;
	private Button tags;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		toBookList = (Button) findViewById(R.id.toBookListButton);
		toBookList.setOnClickListener(this);
		lastReadVers = (Button) findViewById(R.id.lastReadVersButton);
		lastReadVers.setOnClickListener(this);
		settings = (Button) findViewById(R.id.toSettingsButton);
		settings.setOnClickListener(this);
		bookmarks = findViewById(R.id.bookmarkLink);
		bookmarks.setOnClickListener(this);
		tags = (Button)findViewById(R.id.tagsButton);
		tags.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (Constants.getPrefs(this).contains(Constants.LAST_READ_BOOK_ID)) {
			lastReadVers.setEnabled(true);
		} else {
			lastReadVers.setEnabled(false);
		}
		tags.setText(getString(R.string.verseByTag, tagDataAdapter.getTotalTags()));
	}
	
	@Override
	protected void facebookSessionOpened() {
	}

	@Override
	protected void facebookSessionClosed() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.toBookListButton: {
				Intent intent = new Intent(this, BookListActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.lastReadVersButton: {
				Intent intent = new Intent(this, BookListActivity.class);
				intent.putExtra(Constants.SHOULD_OPEN_LAST_READ, true);
				startActivity(intent);
				break;
			}
			case R.id.toSettingsButton: {
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.bookmarkLink: {
				Intent intent = new Intent(this, BookmarkListActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.tagsButton: {
				Intent intent = new Intent(this, TagMetaListActivity.class);
				startActivity(intent);
				break;
			}
			default: {
				break;
			}
		}
	}
}