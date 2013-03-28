package hu.droidium.bibliapp;

import hu.droidium.bibliapp.data.AssetReader;

import java.util.Vector;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BookListActivity extends FacebookEnabledBibleActivity implements OnItemClickListener {

	private BookTitleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_list);
		Vector<String[]> titles = AssetReader.readTitles(this);
		adapter = new BookTitleAdapter(titles, getLayoutInflater(), this);
		ListView bookList = (ListView) findViewById(R.id.bookList);
		bookList.setCacheColorHint(Color.TRANSPARENT);
		bookList.setAdapter(adapter);
		bookList.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int selectedIndex, long id) {
		String fileName = ((String[]) adapter.getItem(selectedIndex))[0];
		Intent intent = new Intent(this, ChapterListActivity.class);
		intent.putExtra(ChapterListActivity.BOOK_FILE_NAME, fileName);
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
