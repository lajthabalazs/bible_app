package hu.droidium.bibliapp.tag_ui;

import hu.droidium.bibliapp.Constants;
import hu.droidium.bibliapp.BibleBaseActivity;
import hu.droidium.bibliapp.R;
import hu.droidium.bibliapp.VerseListActivity;
import hu.droidium.bibliapp.database.Tag;
import hu.droidium.bibliapp.database.TagMeta;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TagListActivity extends BibleBaseActivity implements OnItemClickListener {

	private TagAdapter adapter;
	private String tagId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tagId = getIntent().getStringExtra(Constants.TAG_META_ID);
		setContentView(R.layout.tag_list);
		TagMeta tagMeta = tagDataAdapter.getTagMeta(tagId);
		adapter = new TagAdapter(tagId, tagDataAdapter, bibleDataAdapter, getLayoutInflater());
		ListView tagList = (ListView)findViewById(R.id.tagList);
		tagList.setCacheColorHint(Color.TRANSPARENT);
		tagList.setAdapter(adapter);
		tagList.setOnItemClickListener(this);
		TextView title = (TextView) findViewById(R.id.activityTitle);
		title.setText(tagMeta.getName());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter.refresh();
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
		Tag tag = (Tag)adapter.getItem(itemIndex);
		intent.putExtra(Constants.BOOK_ID, tag.getBook());
		intent.putExtra(Constants.CHAPTER_INDEX, tag.getChapter());
		intent.putExtra(Constants.VERSE_INDEX, tag.getVers());
		startActivity(intent);
	}
}