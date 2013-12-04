package hu.droidium.bibliapp.bookmar_ui;

import java.util.List;

import hu.droidium.bibliapp.Constants;
import hu.droidium.bibliapp.BibleBaseActivity;
import hu.droidium.bibliapp.R;
import hu.droidium.bibliapp.VerseListActivity;
import hu.droidium.bibliapp.database.Bookmark;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TagListActivity extends BibleBaseActivity implements OnItemClickListener {

	private BookmarkAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tag_list);
		List<Bookmark> bookmarks = getAllBookmarks();
		((TextView)findViewById(R.id.activityTitle)).setText(R.string.tagsTitle);
		adapter = new BookmarkAdapter(bookmarks, getLayoutInflater(), this);
		ListView verseList = (ListView)findViewById(R.id.bookmarkList);
		verseList.setCacheColorHint(Color.TRANSPARENT);
		verseList.setAdapter(adapter);
		verseList.setOnItemClickListener(this);
	}

	@Override
	protected void facebookSessionOpened() {
	}

	@Override
	protected void facebookSessionClosed() {
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View view, int itemIndex, long itemId) {
		// Show verse
		Intent intent = new Intent(this, VerseListActivity.class);
		Bookmark bookmark = (Bookmark)view.getTag();
		intent.putExtra(Constants.VERSE_INDEX, bookmark.getVers());
		intent.putExtra(Constants.CHAPTER_INDEX, bookmark.getChapter());
		intent.putExtra(Constants.BOOK_FILE_NAME, bookmark.getBook());
		startActivity(intent);
	}
}