package hu.droidium.bibliapp.tag_ui;

import hu.droidium.bibliapp.R;
import hu.droidium.bibliapp.database.TagMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.TextView;

public class CheckableTagAdapter implements ListAdapter, OnItemClickListener {

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
		TextView tagNameText = (TextView) convertView.findViewById(R.id.highlightListItemText);
		View tagColorCheck = convertView.findViewById(R.id.highlightListItemColorBox);
		tagColorCheck.setTag(tag);
		tagNameText.setTag(tag);
		tagNameText.setText(tag.getName());
		Boolean tagChecked = checked.get(tag.getId());
		tagColorCheck.getBackground().clearColorFilter();
		if (tagChecked != null) {
			if (tagChecked) {
				tagColorCheck.getBackground().setColorFilter(Color.parseColor(tag.getColor()), Mode.MULTIPLY);
			}
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TagMeta tag = tags.get(position);
		Boolean tagChecked = checked.get(tag.getId());
		if (tagChecked == null) {
			tagChecked = false;
		}
		tagChecked = !tagChecked;
		this.checked.put(tag.getId(), tagChecked);
		for (DataSetObserver observer : observers) {
			observer.onChanged();
		}
	}
}