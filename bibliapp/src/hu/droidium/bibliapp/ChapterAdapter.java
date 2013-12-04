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

public class ChapterAdapter implements ListAdapter {

	private BibleDataAdapter bibleDataAdapter;
	private String bookId;
	private HashSet<DataSetObserver> observers = new HashSet<DataSetObserver>();
	private Context context;
	private LayoutInflater inflater;
	
	public ChapterAdapter(String bookId, BibleDataAdapter bibleDataAdapter, LayoutInflater inflater, Context context) {
		this.bookId = bookId;
		this.bibleDataAdapter = bibleDataAdapter;
		this.context = context;
		this.inflater = inflater;
	}

	@Override
	public int getCount() {
		return bibleDataAdapter.getChapterCount(bookId);
	}

	@Override
	public Object getItem(int position) {
		return position;
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
			convertView = inflater.inflate(R.layout.chapter_list_item, null);
			TextView titleView = (TextView)convertView.findViewById(R.id.chapterTitle);
			Constants.scaleText(titleView, context);
			TextView detailsView = (TextView)convertView.findViewById(R.id.chapterDetails);
			Constants.scaleText(detailsView, context);
		}
		convertView.setTag(position);
		TextView titleView = (TextView)convertView.findViewById(R.id.chapterTitle);
		titleView.setText((position + 1) +". " +  context.getString(R.string.chapter));
		TextView detailsView = (TextView)convertView.findViewById(R.id.chapterDetails);
		detailsView.setText(bibleDataAdapter.getVerseCount(bookId, position) + " " + context.getString(R.string.versesLabel));
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
		return bibleDataAdapter.getChapterCount(bookId) == 0;
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
