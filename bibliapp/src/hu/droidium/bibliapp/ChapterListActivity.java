package hu.droidium.bibliapp;

import hu.droidium.bibliapp.data.AssetReader;
import hu.droidium.bibliapp.data.Book;
import hu.droidium.bibliapp.data.Chapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ChapterListActivity extends Activity implements OnItemClickListener {

	public static final String BOOK_FILE_NAME = "Book file name";
	private ChapterAdapter adapter;
	private String fileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.chapter_list);
		Intent intent = getIntent();
		fileName = intent.getStringExtra(BOOK_FILE_NAME);
		Log.e("File name", fileName);
		Book book = AssetReader.readFile(fileName, this);
		Log.e("Book","Chapters " +  book.getChapterCount());
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

}
