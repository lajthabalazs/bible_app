package hu.droidium.bibliapp.bookmar_ui;

import java.util.List;

import hu.droidium.bibliapp.FacebookEnabledBibleActivity;
import hu.droidium.bibliapp.R;
import hu.droidium.bibliapp.VerseListActivity;
import hu.droidium.bibliapp.database.Bookmark;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BookmarkListActivity extends FacebookEnabledBibleActivity implements OnItemClickListener {

	private BookmarkAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmark_list);
		List<Bookmark> bookmarks = getAllBookmarks();
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
		intent.putExtra(VERSE_INDEX, bookmark.getVers());
		intent.putExtra(CHAPTER_INDEX, bookmark.getChapter());
		intent.putExtra(BOOK_FILE_NAME, bookmark.getBook());
		startActivity(intent);
	}
}