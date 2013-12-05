package hu.droidium.bibliapp;

import hu.droidium.bibliapp.bookmar_ui.CheckableTagAdapter;
import hu.droidium.bibliapp.bookmar_ui.TagMargin;
import hu.droidium.bibliapp.data.BibleDataAdapter;
import hu.droidium.bibliapp.data.TagDataAdapter;
import hu.droidium.bibliapp.database.Bookmark;
import hu.droidium.bibliapp.database.TagMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.app.AlertDialog;
import android.database.DataSetObserver;
import android.util.Log;
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
import android.widget.ListView;
import android.widget.TextView;

public class VerseAdapter implements ListAdapter, OnClickListener {

	private String bookId;
	private int chapterIndex;

	private BibleDataAdapter bibleDataAdapter;
	private TagDataAdapter tagDataAdapter;
	private SparseArray<Bookmark> bookmarks;
	private HashSet<DataSetObserver> observers = new HashSet<DataSetObserver>();
	private long displayMenu = -1;
	private boolean facebookEnabled;
	private BibleBaseActivity activity; 
	private LayoutInflater inflater;
	
	public VerseAdapter(String bookId, int chapterIndex, List<Bookmark> bookmarks, LayoutInflater inflater, BibleBaseActivity activity, BibleDataAdapter bibleDataAdapter, TagDataAdapter tagDataAdapter) {
		this.bookId = bookId;
		this.chapterIndex = chapterIndex;

		this.bibleDataAdapter = bibleDataAdapter;
		this.tagDataAdapter = tagDataAdapter;
		this.activity = activity;
		this.inflater = inflater;
		
		this.bookmarks = new SparseArray<Bookmark>();
		for (Bookmark bookmark : bookmarks) {
			this.bookmarks.put(bookmark.getVers(), bookmark);
		}
	}

	@Override
	public int getCount() {
		return bibleDataAdapter.getVerseCount(bookId, chapterIndex);
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
			convertView = inflater.inflate(R.layout.verse_list_item, null);
			TextView titleView = (TextView)convertView.findViewById(R.id.verseTitle);
			Constants.scaleText(titleView, activity);
			TextView versTextView = (TextView)convertView.findViewById(R.id.verseContent);
			Constants.scaleText(versTextView, activity);
		}
		convertView.setTag(position);
		TextView titleView = (TextView)convertView.findViewById(R.id.verseTitle);
		titleView.setText((chapterIndex + 1) + "," + (position + 1));
		TextView versTextView = (TextView)convertView.findViewById(R.id.verseContent);
		versTextView.setText(bibleDataAdapter.getVerseLine(bookId, chapterIndex, position));
		ImageView facebookButton = (ImageView)convertView.findViewById(R.id.facebookShareButton);
		ImageView bookmarkButton = (ImageView)convertView.findViewById(R.id.saveBookmark);
		ImageView highlightButton = (ImageView)convertView.findViewById(R.id.highlight);
		TagMargin tagMargin = (TagMargin)convertView.findViewById(R.id.tagMargin);
		tagMargin.setTag(position);
		tagMargin.setOnClickListener(this);
		tagMargin.setColors(tagDataAdapter.getTagColors(bookId, chapterIndex, position));
		if (displayMenu == position) {
			displayMenu = -1;
			bookmarkButton.setVisibility(View.VISIBLE);
			bookmarkButton.setOnClickListener(this);
			highlightButton.setVisibility(View.VISIBLE);
			highlightButton.setOnClickListener(this);
			if (facebookEnabled) {
				facebookButton.setVisibility(View.VISIBLE);
				facebookButton.setOnClickListener(this);
			}
			// Animate buttons
			Animation slideInFacebook = AnimationUtils.loadAnimation(activity, R.anim.facebook_share_button_in_from_right);
			slideInFacebook.setDuration(900);
			if (facebookEnabled) {
				facebookButton.startAnimation(slideInFacebook);		
				facebookButton.setTag(position);
			}
			if (bookmarks.get(position) == null) {
				Animation slideInBookmark = AnimationUtils.loadAnimation(activity, R.anim.save_bookmark_button_in_from_right);
				slideInBookmark.setDuration(600);
				bookmarkButton.startAnimation(slideInBookmark);
			}
			Animation slideInHighlight = AnimationUtils.loadAnimation(activity, R.anim.highlight_button_in_from_right);
			slideInHighlight.setDuration(300);
			highlightButton.startAnimation(slideInHighlight);		
			highlightButton.setTag(position);
			
			bookmarkButton.setTag(position);
		} else {
			facebookButton.setVisibility(View.INVISIBLE);
			highlightButton.setVisibility(View.INVISIBLE);
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
		return bibleDataAdapter.getVerseCount(bookId, chapterIndex) == 0;
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

	public void showOptions(View view, long itemId, boolean facebookEnabled) {
		Log.e("Options requested", "For item " + itemId);
		displayMenu = itemId;
		this.facebookEnabled = facebookEnabled;
		for (DataSetObserver observer : observers){
			observer.onChanged();
		}
	}

	@Override
	public void onClick(View v) {
		Integer index = (Integer)v.getTag();
		final int vers = index;
		final String verseId = Constants.getVerseLabel(bookId, chapterIndex, vers, bibleDataAdapter);
		final String versBody = bibleDataAdapter.getVerseLine(bookId, chapterIndex, index);

		switch (v.getId()) {
			case R.id.facebookShareButton: {
				if (facebookEnabled) {
					if (index != null) {
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
								activity.publishStory(commentEditor.getText().toString(), verseId, versBody);
								dialog.dismiss();
							}
						});
					}
				}
				break;
			}
			case R.id.saveBookmark: {
				if (index != null) {
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
							Bookmark bookmark = activity.saveBookmark(commentEditor.getText().toString(), bookId, chapterIndex, vers, Bookmark.DEFAULT_COLOR);
							bookmarks.put(vers, bookmark);
							dialog.dismiss();
							for (DataSetObserver observer : observers){
								observer.onChanged();
							}
						}
					});
				}
				break;
			}
			case R.id.tagMargin:
			case R.id.highlight: {
				if (index != null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(activity);
					builder.setTitle(R.string.highlightDialogTitle);
					final int verseIndex = index;
					final AlertDialog dialog = builder.create();
					final View dialogView = inflater.inflate(R.layout.highlight_dialog, null);
					ListView tagList = (ListView)dialogView.findViewById(R.id.setHighlightList);
					final List<TagMeta> tagMetas = tagDataAdapter.getTagMetas();
					HashMap<String, Boolean> checked = new HashMap<String, Boolean>();
					// Register original state to be able to check for changes
					final HashSet<String> usedTagMetaIds = new HashSet<String>();
					List<TagMeta> tags = tagDataAdapter.getTags(bookId, chapterIndex, verseIndex);
					for (TagMeta tag : tags) {
						usedTagMetaIds.add(tag.getId());
						checked.put(tag.getId(), true);
					}
					final CheckableTagAdapter tagAdapter = new CheckableTagAdapter(tagMetas, checked, inflater);
					tagList.setAdapter(tagAdapter);
					dialogView.findViewById(R.id.setHighlightCancelButton).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					((TextView)dialogView.findViewById(R.id.addBookmarkVersView)).setText("\"" + versBody + "\"");
					dialogView.findViewById(R.id.setHighlightOkButton).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							for (TagMeta tagMeta : tagMetas) {
								// Save changed highlighting
								if (usedTagMetaIds.contains(tagMeta.getId()) != tagAdapter.isChecked(tagMeta.getId())) {
									if (usedTagMetaIds.contains(tagMeta.getId())) {
										tagDataAdapter.removeTag(tagMeta.getId(), bookId, chapterIndex, verseIndex);
									} else {
										tagDataAdapter.addTag(tagMeta.getId(), bookId, chapterIndex, verseIndex);
									}
								}
							}
							dialog.dismiss();
							for (DataSetObserver observer : observers){
								observer.onChanged();
							}
						}
					});
					dialog.setView(dialogView);
					dialog.show();
				}
				break;
			}
			default:
				break;
		}
	}
}
