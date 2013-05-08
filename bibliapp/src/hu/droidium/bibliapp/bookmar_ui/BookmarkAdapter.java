package hu.droidium.bibliapp.bookmar_ui;

import hu.droidium.bibliapp.FacebookEnabledBibleActivity;
import hu.droidium.bibliapp.R;
import hu.droidium.bibliapp.data.Book;
import hu.droidium.bibliapp.database.Bookmark;

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
	private FacebookEnabledBibleActivity activity; 
	private LayoutInflater inflater;
	private List<Bookmark> bookmarks;
	
	public BookmarkAdapter(List<Bookmark> bookmarks, LayoutInflater inflater, FacebookEnabledBibleActivity activity) {
		this.activity = activity;
		this.inflater = inflater;
		this.bookmarks = bookmarks;
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
		Book book = activity.getBook(bookmark.getBook());
		titleView.setText(book.getChapter(bookmark.getChapter()).getVerse(bookmark.getVers()).getId());
		TextView versTextView = (TextView)convertView.findViewById(R.id.verseContent);
		versTextView.setText(book.getChapter(bookmark.getChapter()).getVerse(bookmark.getVers()).getLine());
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
