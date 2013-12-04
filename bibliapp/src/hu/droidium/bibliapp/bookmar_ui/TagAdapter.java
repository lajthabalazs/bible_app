package hu.droidium.bibliapp.bookmar_ui;

import hu.droidium.bibliapp.BibleBaseActivity;
import hu.droidium.bibliapp.R;
import hu.droidium.bibliapp.database.Bookmark;
import hu.droidium.bibliapp.database.Tag;

import java.util.HashSet;
import java.util.List;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class TagAdapter implements ListAdapter {

	private HashSet<DataSetObserver> observers = new HashSet<DataSetObserver>();
	private BibleBaseActivity activity; 
	private LayoutInflater inflater;
	private List<Bookmark> bookmarks;
	
	public TagAdapter(List<Tag> bookmarks, LayoutInflater inflater, BibleBaseActivity activity) {
		this.activity = activity;
		this.inflater = inflater;
		//this.bookmarks = bookmarks;
	}

	@Override
	public int getCount() {
		return bookmarks.size();
	}

	@Override
	public Object getItem(int position) {
		return bookmarks.get(position);
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
		return bookmarks.isEmpty();
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
