package hu.droidium.bibliapp;

import hu.droidium.bibliapp.bookmar_ui.BookmarkListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class BookListActivity extends FacebookEnabledBibleActivity implements OnItemClickListener, OnClickListener {

	private BookTitleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_list);
		TextView bookmarksLink = (TextView) findViewById(R.id.bookmarkLink);
		bookmarksLink.setOnClickListener(this);
		adapter = new BookTitleAdapter(super.getBookTitles(), getLayoutInflater(), this);
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

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, BookmarkListActivity.class);
		startActivity(intent);
	}
}
