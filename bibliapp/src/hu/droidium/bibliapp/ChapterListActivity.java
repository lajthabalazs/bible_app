package hu.droidium.bibliapp;

import hu.droidium.bibliapp.data.AssetReader; 
import hu.droidium.bibliapp.data.Book;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ChapterListActivity extends FacebookEnabledBibleActivity implements OnItemClickListener {

	public static final String BOOK_FILE_NAME = "Book file name";
	private ChapterAdapter adapter;
	private String fileName; 
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chapter_list);
		Intent intent = getIntent();
		fileName = intent.getStringExtra(BOOK_FILE_NAME);
		Book book = AssetReader.readFile(fileName, this);
		((TextView)findViewById(R.id.chapterListTitle)).setText(book.getTitle());
		adapter = new ChapterAdapter(book, getLayoutInflater(), this);		
		ListView chapterList = (ListView)findViewById(R.id.chapterList);
		chapterList.setCacheColorHint(Color.TRANSPARENT);
		chapterList.setAdapter(adapter);
		chapterList.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int selectedIndex, long id) {
		Intent intent = new Intent(this, VerseListActivity.class);
		intent.putExtra(ChapterListActivity.BOOK_FILE_NAME, fileName);
		intent.putExtra(VerseListActivity.CHAPTER_INDEX, selectedIndex);
		startActivity(intent);
	}

	@Override
	protected void facebookSessionOpened() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void facebookSessionClosed() {
		// TODO Auto-generated method stub
		
	}	

}
