package hu.droidium.bibliapp;

import hu.droidium.bibliapp.data.Book;

import java.util.HashSet;

import android.app.AlertDialog;
import android.database.DataSetObserver;
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
	private int chapterIndex;
	private long displayMenu = -1;
	
	public VerseAdapter(Book book, int chapterIndex, LayoutInflater inflater, FacebookEnabledBibleActivity activity) {
		this.book = book;
		this.chapterIndex = chapterIndex;
		this.activity = activity;
		this.inflater = inflater;
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
		TextView contentView = (TextView)convertView.findViewById(R.id.verseContent);
		contentView.setText(book.getChapter(chapterIndex).getVerse(position).getLine());
		ImageView facebookButton = (ImageView)convertView.findViewById(R.id.facebookShareButton);
		if (displayMenu == position) {
			displayMenu = -1;
			facebookButton.setVisibility(View.VISIBLE);
			Animation slideIn = AnimationUtils.loadAnimation(activity, R.anim.facebook_share_button_in_from_right);
			slideIn.setDuration(300);
			facebookButton.startAnimation(slideIn);		
			facebookButton.setTag(position);
			facebookButton.setOnClickListener(this);
		} else {
			facebookButton.setVisibility(View.GONE);			
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
	}
}
