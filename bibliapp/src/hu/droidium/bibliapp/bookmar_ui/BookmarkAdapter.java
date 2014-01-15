package hu.droidium.bibliapp.bookmar_ui;

import hu.droidium.bibliapp.R;
import hu.droidium.bibliapp.data.BibleDataAdapter;
import hu.droidium.bibliapp.data.Bookmark;
import hu.droidium.bibliapp.data.Verse;

import java.util.HashSet;
import java.util.List;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class BookmarkAdapter implements ListAdapter {

	private HashSet<DataSetObserver> observers = new HashSet<DataSetObserver>();
	private LayoutInflater inflater;
	private List<Bookmark> bookmarks;
	private BibleDataAdapter bibleDataAdapter;
	
	public BookmarkAdapter(List<Bookmark> bookmarks, LayoutInflater inflater, BibleDataAdapter bibleDataAdapter) {
		this.inflater = inflater;
		this.bookmarks = bookmarks;
		this.bibleDataAdapter = bibleDataAdapter;
	}
	
	public void refresh(List<Bookmark> bookmarks) {
		this.bookmarks = bookmarks;
		for (DataSetObserver observer : observers) {
			observer.onChanged();
		}
		
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
		Bookmark bookmark = bookmarks.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.bookmark_list_item, null);
		}
		convertView.setTag(bookmark);
		TextView titleView = (TextView)convertView.findViewById(R.id.verseTitle);
		titleView.setText(Verse.getVerseLabel(bookmark.getBookId(), bookmark.getChapter(), bookmark.getVers(), bibleDataAdapter));
		TextView versTextView = (TextView)convertView.findViewById(R.id.verseContent);
		versTextView.setText(bibleDataAdapter.getVerseLine(bookmark.getBookId(), bookmark.getChapter(), bookmark.getVers()));
		TextView noteView = (TextView)convertView.findViewById(R.id.bookmarkNote);
		noteView.setVisibility(View.GONE);
		convertView.findViewById(R.id.noteTitle).setVisibility(View.GONE);
		String note = bookmark.getNote();
		if ((note != null) && (note.length() > 0)) {
			noteView.setText(bookmark.getNote());
			noteView.setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.noteTitle).setVisibility(View.VISIBLE);
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
