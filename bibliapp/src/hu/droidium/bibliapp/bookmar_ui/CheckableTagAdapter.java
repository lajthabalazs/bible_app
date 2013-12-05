package hu.droidium.bibliapp.bookmar_ui;

import hu.droidium.bibliapp.R;
import hu.droidium.bibliapp.database.TagMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;

public class CheckableTagAdapter implements ListAdapter, OnCheckedChangeListener {

	private HashSet<DataSetObserver> observers = new HashSet<DataSetObserver>();
	private LayoutInflater inflater;
	private List<TagMeta> tags = new ArrayList<TagMeta>();
	private HashMap<String, Boolean> checked = new HashMap<String, Boolean>();

	public CheckableTagAdapter(List<TagMeta> tags, HashMap<String, Boolean> checked, LayoutInflater inflater) {
		this.inflater = inflater;
		this.tags.addAll(tags);
		for (String key : checked.keySet()) {
			this.checked.put(key, checked.get(key));
		}
	}

	public void setTags(List<TagMeta> tags, HashMap<String, Boolean> checked) {
		this.tags.clear();
		if (tags != null) {
			this.tags.addAll(tags);
		}
		this.checked.clear();
		if (checked != null) {
			for (String key : checked.keySet()) {
				this.checked.put(key, checked.get(key));
			}
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
		CheckBox tagNameCheck = (CheckBox) convertView.findViewById(R.id.highlightedCheckbox);
		tagNameCheck.setTag(tag);
		tagNameCheck.setOnCheckedChangeListener(this);
		tagNameCheck.setText(tag.getName());
		tagNameCheck.setBackgroundColor(Color.parseColor(tag.getColor()));
		Boolean tagChecked = checked.get(tag.getId());
		if (tagChecked != null) {
			tagNameCheck.setChecked(tagChecked);
		} else {
			tagNameCheck.setChecked(false);
		}
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

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		TagMeta tag = (TagMeta) buttonView.getTag();
		this.checked.put(tag.getId(), isChecked);
	}
	
	public boolean isChecked(String tagId) {
		if (checked.get(tagId) != null) {
			return checked.get(tagId);
		} else {
			return false;
		}
	}

	public boolean isChecked(int position) {
		String tagId = tags.get(position).getId();
		if (checked.get(tagId) != null) {
			return checked.get(tagId);
		} else {
			return false;
		}
	}
}