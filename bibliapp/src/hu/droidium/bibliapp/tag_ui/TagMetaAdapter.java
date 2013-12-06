package hu.droidium.bibliapp.tag_ui;

import hu.droidium.bibliapp.R;
import hu.droidium.bibliapp.data.TagDataAdapter;
import hu.droidium.bibliapp.database.TagMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class TagMetaAdapter implements ListAdapter {

	private HashSet<DataSetObserver> observers = new HashSet<DataSetObserver>();
	private LayoutInflater inflater;
	private List<TagMeta> tags = new ArrayList<TagMeta>();
	private HashMap<String, Integer> occurrences = new HashMap<String, Integer>();
	private TagDataAdapter tagDataAdapter;
	private Context context;

	public TagMetaAdapter(TagDataAdapter tagDataAdapter, LayoutInflater inflater, Context context) {
		this.inflater = inflater;
		this.tags.addAll(tagDataAdapter.getTagMetas());
		this.tagDataAdapter = tagDataAdapter;
		this.context = context;
		for (TagMeta tag : tags){
			occurrences.put(tag.getId(), tagDataAdapter.getTotalTags(tag.getId()));
		}
	}

	public void refresh() {
		tags.clear();
		this.occurrences.clear();
		this.tags.addAll(tagDataAdapter.getTagMetas());
		for (TagMeta tag : tags){
			occurrences.put(tag.getId(), tagDataAdapter.getTotalTags(tag.getId()));
		}
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
		TagMeta tag = tags.get(position);
		TextView tagNameText = (TextView) convertView.findViewById(R.id.highlightListItemText);
		View tagColorCheck = convertView.findViewById(R.id.highlightListItemColorBox);
		tagColorCheck.setTag(tag);
		tagNameText.setTag(tag);
		tagNameText.setText(tag.getName() + " " + context.getString(R.string.tagCountSuffix, occurrences.get(tag.getId())));
		tagColorCheck.getBackground().setColorFilter(Color.parseColor(tag.getColor()), Mode.MULTIPLY);
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