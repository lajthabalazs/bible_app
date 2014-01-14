package hu.droidium.bibliapp.tag_ui;

import hu.droidium.bibliapp.R;
import hu.droidium.bibliapp.data.BibleDataAdapter;
import hu.droidium.bibliapp.data.TagDataAdapter;
import hu.droidium.bibliapp.data.Verse;
import hu.droidium.bibliapp.database.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class TagAdapter implements ListAdapter {

	private HashSet<DataSetObserver> observers = new HashSet<DataSetObserver>();
	private LayoutInflater inflater;
	private List<Tag> tags = new ArrayList<Tag>();
	private BibleDataAdapter bibleDataAdapter;
	private int color;
	private String tagMetaId;
	private TagDataAdapter tagDataAdapter;

	public TagAdapter(String tagMetaId, TagDataAdapter tagDataAdapter, BibleDataAdapter bibleDataAdapter, LayoutInflater inflater) {
		this.inflater = inflater;
		this.tags.addAll(tagDataAdapter.getTags(tagMetaId));
		this.bibleDataAdapter = bibleDataAdapter;
		this.tagDataAdapter = tagDataAdapter;
		this.tagMetaId = tagMetaId;
		color = Color.parseColor(tagDataAdapter.getTagMeta(tagMetaId).getColor());
	}

	public void refresh() {
		tags.clear();
		this.tags.addAll(tagDataAdapter.getTags(tagMetaId));
		for (DataSetObserver observer : observers) {
			observer.onChanged();
		}
	}

	@Override
	public int getCount() {
		return tags.size();
	}

	@Override
	public Object getItem(int position) {
		return tags.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.tag_list_item, null);
		}
		Tag tag = tags.get(position);
		TextView tagNameText = (TextView) convertView.findViewById(R.id.highlightListItemText);
		View tagColorCheck = convertView.findViewById(R.id.highlightListItemColorBox);
		tagColorCheck.setTag(tag);
		tagNameText.setTag(tag);
		tagNameText.setText(Verse.getVerseLabel(tag.getBook(), tag.getChapter(), tag.getVers(), bibleDataAdapter));
		tagColorCheck.getBackground().setColorFilter(color, Mode.MULTIPLY);
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return tags.isEmpty();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		observers.add(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		observers.remove(observer);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}
}