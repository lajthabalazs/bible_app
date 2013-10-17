package hu.droidium.bibliapp;

import hu.droidium.bibliapp.data.Book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ChapterListActivity extends FacebookEnabledBibleActivity implements OnItemClickListener {

	private ChapterAdapter adapter;
	private String fileName; 
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chapter_list);
		SharedPreferences prefs = Constants.getPrefs(this);
		Intent intent = getIntent();
		fileName = intent.getStringExtra(Constants.BOOK_FILE_NAME);
		Book book = getBook(fileName);
		((TextView)findViewById(R.id.chapterListTitle)).setText(book.getTitle());
		adapter = new ChapterAdapter(book, getLayoutInflater(), this);		
		ListView chapterList = (ListView)findViewById(R.id.chapterList);
		chapterList.setCacheColorHint(Color.TRANSPARENT);
		chapterList.setAdapter(adapter);
		chapterList.setOnItemClickListener(this);
		
		if (intent.hasExtra(Constants.SHOULD_OPEN_LAST_READ)) {
			int chapterIndex = prefs.getInt(Constants.LAST_READ_CHAPTER, 0);
			Intent nextIntent = new Intent(this, VerseListActivity.class);
			nextIntent.putExtra(Constants.BOOK_FILE_NAME, fileName);
			nextIntent.putExtra(Constants.CHAPTER_INDEX, chapterIndex);
			nextIntent.putExtra(Constants.SHOULD_OPEN_LAST_READ, true);
			startActivity(nextIntent);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int selectedIndex, long id) {
		Intent intent = new Intent(this, VerseListActivity.class);
		intent.putExtra(Constants.BOOK_FILE_NAME, fileName);
		intent.putExtra(Constants.CHAPTER_INDEX, selectedIndex);
		startActivity(intent);
	}

	@Override
	protected void facebookSessionOpened() {
	}

	@Override
	protected void facebookSessionClosed() {
	}	

}
