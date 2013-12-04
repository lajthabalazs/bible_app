package hu.droidium.bibliapp;

import hu.droidium.bibliapp.data.BibleDataAdapter;

import java.util.HashSet;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class BookTitleAdapter implements ListAdapter {
	
	private HashSet<DataSetObserver> observers = new HashSet<DataSetObserver>();
	private LayoutInflater inflater;
	private BibleDataAdapter bibleDataAdapter;
	private Context context;
	private String[] bookIds;
	
	public BookTitleAdapter(LayoutInflater layoutInflater, BibleDataAdapter bibleDataAdapter, Context context) {
		this.inflater = layoutInflater;
		this.context = context;
		this.bibleDataAdapter = bibleDataAdapter;
		bookIds = bibleDataAdapter.getBookIds();
	}

	@Override
	public int getCount() {
		return bookIds.length;
	}

	@Override
	public Object getItem(int position) {
		return bookIds[position];
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
			TextView titleView = (TextView)convertView.findViewById(R.id.bookTitle);
			Constants.scaleText(titleView, context);
		}
		convertView.setTag(bookIds[position]);
		TextView titleView = (TextView)convertView.findViewById(R.id.bookTitle);
		titleView.setText(bibleDataAdapter.getBookTitle(bookIds[position]));
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
		return bookIds == null || bookIds.length == 0;
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
