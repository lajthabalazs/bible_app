package hu.droidium.bibliapp;

import hu.droidium.bibliapp.data.Book;
import hu.droidium.bibliapp.data.Chapter;

import java.util.HashSet;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class VerseAdapter implements ListAdapter {

	private Book book;
	private HashSet<DataSetObserver> observers = new HashSet<DataSetObserver>();
	private Context context; 
	private LayoutInflater inflater;
	private Chapter chapter;
	
	public VerseAdapter(Book book, int chapterIndex, LayoutInflater inflater, Context context) {
		this.book = book;
		this.chapter = book.getChapter(chapterIndex);
		this.context = context;
		this.inflater = inflater;
	}

	@Override
	public int getCount() {
		return chapter.getVerseCount();
	}

	@Override
	public Object getItem(int position) {
		return chapter.getVerse(position);
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
			convertView = inflater.inflate(R.layout.verse_list_item, null);
		}
		convertView.setTag(chapter.getVerse(position));
		TextView titleView = (TextView)convertView.findViewById(R.id.verseTitle);
		titleView.setText((chapter.getIndex() + 1) + "," + (position + 1));
		TextView contentView = (TextView)convertView.findViewById(R.id.verseContent);
		contentView.setText(chapter.getVerse(position).getLine());
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
		return chapter.getVerseCount() == 0;
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
