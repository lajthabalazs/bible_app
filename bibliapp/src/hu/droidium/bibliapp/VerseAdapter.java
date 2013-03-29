package hu.droidium.bibliapp;

import hu.droidium.bibliapp.data.Book;
import hu.droidium.bibliapp.database.Bookmark;

import java.util.HashSet;
import java.util.List;

import android.app.AlertDialog;
import android.database.DataSetObserver;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class VerseAdapter implements ListAdapter, OnClickListener {

	private Book book;
	private HashSet<DataSetObserver> observers = new HashSet<DataSetObserver>();
	private FacebookEnabledBibleActivity activity; 
	private LayoutInflater inflater;
	private SparseArray<Bookmark> bookmarks;
	private int chapterIndex;
	private long displayMenu = -1;
	
	public VerseAdapter(Book book, int chapterIndex, List<Bookmark> bookmarks, LayoutInflater inflater, FacebookEnabledBibleActivity activity) {
		this.book = book;
		this.chapterIndex = chapterIndex;
		this.activity = activity;
		this.inflater = inflater;
		this.bookmarks = new SparseArray<Bookmark>();
		for (Bookmark bookmark : bookmarks) {
			this.bookmarks.put(bookmark.getVers(), bookmark);
		}
	}

	@Override
	public int getCount() {
		return book.getChapter(chapterIndex).getVerseCount();
	}

	@Override
	public Object getItem(int position) {
		return book.getChapter(chapterIndex).getVerse(position);
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
		convertView.setTag(position);
		TextView titleView = (TextView)convertView.findViewById(R.id.verseTitle);
		titleView.setText((book.getChapter(chapterIndex).getIndex() + 1) + "," + (position + 1));
		TextView versTextView = (TextView)convertView.findViewById(R.id.verseContent);
		versTextView.setText(book.getChapter(chapterIndex).getVerse(position).getLine());
		ImageView facebookButton = (ImageView)convertView.findViewById(R.id.facebookShareButton);
		ImageView bookmarkButton = (ImageView)convertView.findViewById(R.id.saveBookmark);
		if (displayMenu == position) {
			displayMenu = -1;
			bookmarkButton.setVisibility(View.VISIBLE);
			facebookButton.setVisibility(View.VISIBLE);
			bookmarkButton.setOnClickListener(this);
			facebookButton.setOnClickListener(this);
			// Animate buttons
			Animation slideInFacebook = AnimationUtils.loadAnimation(activity, R.anim.facebook_share_button_in_from_right);
			slideInFacebook.setDuration(600);
			facebookButton.startAnimation(slideInFacebook);		
			facebookButton.setTag(position);
			if (bookmarks.get(position) == null) {
				Animation slideInBookmark = AnimationUtils.loadAnimation(activity, R.anim.save_bookmark_button_in_from_right);
				slideInBookmark.setDuration(300);
				bookmarkButton.startAnimation(slideInBookmark);
				bookmarkButton.setTag(position);
			}
		} else {
			facebookButton.setVisibility(View.INVISIBLE);
			if (bookmarks.get(position) != null) {
				convertView.findViewById(R.id.saveBookmark).setVisibility(View.VISIBLE);
			} else {
				convertView.findViewById(R.id.saveBookmark).setVisibility(View.INVISIBLE);
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
		return book.getChapter(chapterIndex).getVerseCount() == 0;
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

	public void showOptions(View view, long itemId) {
		displayMenu = itemId;
		for (DataSetObserver observer : observers){
			observer.onChanged();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.facebookShareButton) {
			Integer index = (Integer)v.getTag();
			if (index != null) {
				final String versId = book.getChapter(chapterIndex).getVerse(index).getId();
				final String versBody = book.getChapter(chapterIndex).getVerse(index).getLine();
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle(R.string.facebookShareDialogTitle);
				final AlertDialog dialog = builder.create();
				final View dialogView = inflater.inflate(R.layout.share_vers_dialog, null);
				dialog.setView(dialogView);
				dialog.show();
				dialogView.findViewById(R.id.facebookPostCancelButton).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				((TextView)dialogView.findViewById(R.id.facebookPostVersView)).setText("\"" + versBody + "\"");
				final EditText commentEditor = (EditText)dialogView.findViewById(R.id.facebookPostEditor);
				dialogView.findViewById(R.id.facebookPostSendButton).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						activity.publishStory(commentEditor.getText().toString(), versId, versBody);
						dialog.dismiss();
					}
				});
			}
		} else {
			Integer index = (Integer)v.getTag();
			if (index != null) {
				final String bookId = book.getId();
				final int chapter = chapterIndex;
				final int vers = index;
				final String versBody = book.getChapter(chapterIndex).getVerse(index).getLine();
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle(R.string.addBookmarkButton);
				final AlertDialog dialog = builder.create();
				final View dialogView = inflater.inflate(R.layout.add_bookmark_dialog, null);
				dialog.setView(dialogView);
				dialog.show();
				dialogView.findViewById(R.id.addBookmarkCancelButton).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				((TextView)dialogView.findViewById(R.id.addBookmarkVersView)).setText("\"" + versBody + "\"");
				final EditText commentEditor = (EditText)dialogView.findViewById(R.id.addBookmarkNoteEditor);
				dialogView.findViewById(R.id.addBookmarkButton).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Bookmark bookmark = activity.saveBookmark(commentEditor.getText().toString(), bookId, chapter, vers, Bookmark.DEFAULT_COLOR);
						bookmarks.put(vers, bookmark);
						dialog.dismiss();
						for (DataSetObserver observer : observers){
							observer.onChanged();
						}
					}
				});
			}
		}
	}
}
