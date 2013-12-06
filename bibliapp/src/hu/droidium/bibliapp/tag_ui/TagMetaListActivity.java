package hu.droidium.bibliapp.tag_ui;

import hu.droidium.bibliapp.BibleBaseActivity;
import hu.droidium.bibliapp.Constants;
import hu.droidium.bibliapp.R;
import hu.droidium.bibliapp.database.TagMeta;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TagMetaListActivity extends BibleBaseActivity implements OnItemClickListener {

	private TagMetaAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tag_list);
		adapter = new TagMetaAdapter(tagDataAdapter, getLayoutInflater(), this);
		ListView verseList = (ListView)findViewById(R.id.tagList);
		verseList.setCacheColorHint(Color.TRANSPARENT);
		verseList.setAdapter(adapter);
		verseList.setOnItemClickListener(this);
		TextView title = (TextView) findViewById(R.id.activityTitle);
		title.setText(R.string.tagsTitle);		
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
		TagMeta tag = (TagMeta)adapter.getItem(itemIndex);
		Intent intent = new Intent(this, TagListActivity.class);
		intent.putExtra(Constants.TAG_META_ID, tag.getId());
		startActivity(intent);
	}
}