package hu.droidium.bibliapp;

import java.util.HashSet;
import java.util.Vector;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class BookTitleAdapter implements ListAdapter {
	
	private Vector<String[]> books;
	private HashSet<DataSetObserver> observers = new HashSet<DataSetObserver>();
	private Context context;  
	private LayoutInflater inflater;
	
	public BookTitleAdapter(Vector<String[]> books, LayoutInflater inflater, Context context) {
		this.books = books;
		this.context = context;
		this.inflater = inflater;
	}

	@Override
	public int getCount() {
		return books.size();
	}

	@Override
	public Object getItem(int position) {
		return books.get(position);
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
			convertView = inflater.inflate(R.layout.book_list_item, null);
		}
		convertView.setTag(books.get(position)[0]);
		TextView titleView = (TextView)convertView.findViewById(R.id.bookTitle);
		titleView.setText(books.get(position)[1]);
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
		return books.isEmpty();
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
