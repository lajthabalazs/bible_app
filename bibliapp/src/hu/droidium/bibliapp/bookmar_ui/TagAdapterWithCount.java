package hu.droidium.bibliapp.bookmar_ui;

import hu.droidium.bibliapp.R;
import hu.droidium.bibliapp.database.TagMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;

public class TagAdapterWithCount implements ListAdapter {

	private HashSet<DataSetObserver> observers = new HashSet<DataSetObserver>();
	private LayoutInflater inflater;
	private List<TagMeta> tags = new ArrayList<TagMeta>();
	private List<Integer> occurrences = new ArrayList<Integer>();
	private Resources resources;
	private List<Boolean> checked = new ArrayList<Boolean>();

	public TagAdapterWithCount(List<TagMeta> tags, List<Integer>occurrences, List<Boolean> checked, LayoutInflater inflater, Resources resources) {
		this.inflater = inflater;
		this.occurrences.addAll(occurrences);
		this.tags.addAll(tags);
		this.checked.addAll(checked);
		this.resources = resources;
	}

	public void setTags(List<TagMeta> tags, List<Integer>occurrences) {
		this.tags.clear();
		if (tags != null) {
			this.tags.addAll(tags);
		}
		this.occurrences.clear();
		if (occurrences != null) {
			this.occurrences.addAll(occurrences);
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
		CheckBox tagNameCheck = (CheckBox) convertView.findViewById(R.id.highlightedCheckbox);
		if (occurrences.size() != 0) {
			tagNameCheck.setText(resources.getString(R.string.tagListItemWithCount, tags.get(position).getName(), occurrences.get(position)));
		} else {
			tagNameCheck.setText(tags.get(position).getName());
		}
		tagNameCheck.setBackgroundColor(Color.parseColor(tags.get(position).getColor()));
		tagNameCheck.setChecked(checked.get(position));
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