package hu.droidium.bibliapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends FacebookEnabledBibleActivity implements OnClickListener {

	private Button lastReadVers;
	private Button toBookList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		toBookList = (Button) findViewById(R.id.toBookListButton);
		toBookList.setOnClickListener(this);
		lastReadVers = (Button) findViewById(R.id.lastReadVers);
		lastReadVers.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (Constants.getPrefs(this).contains(Constants.LAST_READ_BOOK)) {
			lastReadVers.setEnabled(true);
		} else {
			lastReadVers.setEnabled(false);
		}
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
			case R.id.lastReadVers: {
				Intent intent = new Intent(this, BookListActivity.class);
				intent.putExtra(Constants.SHOULD_OPEN_LAST_READ, true);
				startActivity(intent);
				break;
			}
			default: {
				break;
			}
		}
	}
}