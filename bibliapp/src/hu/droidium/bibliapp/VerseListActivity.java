package hu.droidium.bibliapp;

import java.util.List;

import hu.droidium.bibliapp.data.AssetReader;
import hu.droidium.bibliapp.data.Book;
import hu.droidium.bibliapp.database.Bookmark;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class VerseListActivity extends FacebookEnabledBibleActivity implements OnItemClickListener {

	private VerseAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verse_list);
		Intent intent = getIntent();
		String fileName = intent.getStringExtra(ChapterListActivity.BOOK_FILE_NAME);
		int chapterIndex = intent.getIntExtra(CHAPTER_INDEX, -1);
		Book book = AssetReader.readFile(fileName, this);
		((TextView)findViewById(R.id.verseListTitle)).setText(book.getTitle());
		List<Bookmark> bookmarks = getBookmarksForChapter(book.getId(), chapterIndex);
		adapter = new VerseAdapter(book, chapterIndex, bookmarks, getLayoutInflater(), this);
		ListView verseList = (ListView)findViewById(R.id.verseList);
		verseList.setCacheColorHint(Color.TRANSPARENT);
		verseList.setAdapter(adapter);
		verseList.setOnItemClickListener(this);
		if (intent.hasExtra(VERSE_INDEX)) {
			int verseIndex = intent.getIntExtra(VERSE_INDEX,0);
			verseList.setSelection(verseIndex);
		}
	}

	@Override
	protected void facebookSessionOpened() {
	}

	@Override
	protected void facebookSessionClosed() {
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View view, int itemIndex, long itemId) {
		if (super.isFacebookSessionOpened()) {
			adapter.showOptions(view, itemId);
		}
	}
}
