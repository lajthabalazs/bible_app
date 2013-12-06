package hu.droidium.bibliapp.tag_ui;

import java.util.List;

import hu.droidium.bibliapp.BibleBaseActivity;
import hu.droidium.bibliapp.R;
import hu.droidium.bibliapp.VerseListActivity;
import hu.droidium.bibliapp.database.TagMeta;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TagMetaListActivity extends BibleBaseActivity implements OnItemClickListener {

	private TagAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tag_list);
		List<TagMeta> tags = tagDataAdapter.getTagMetas();
		adapter = new TagAdapter(tags, tagDataAdapter, getLayoutInflater(), this);
		ListView verseList = (ListView)findViewById(R.id.tagList);
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
		startActivity(intent);
	}
}