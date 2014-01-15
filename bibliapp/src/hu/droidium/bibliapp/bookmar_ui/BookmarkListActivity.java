package hu.droidium.bibliapp.bookmar_ui;

import java.util.List;

import hu.droidium.bibliapp.Constants;
import hu.droidium.bibliapp.BibleBaseActivity;
import hu.droidium.bibliapp.R;
import hu.droidium.bibliapp.VerseListActivity;
import hu.droidium.bibliapp.data.Bookmark;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class BookmarkListActivity extends BibleBaseActivity implements OnItemClickListener, OnItemLongClickListener {

	private BookmarkAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmark_list);
		List<Bookmark> bookmarks = getAllBookmarks();
		adapter = new BookmarkAdapter(bookmarks, getLayoutInflater(), bibleDataAdapter);
		ListView verseList = (ListView)findViewById(R.id.bookmarkList);
		verseList.setCacheColorHint(Color.TRANSPARENT);
		verseList.setAdapter(adapter);
		verseList.setOnItemClickListener(this);
		verseList.setOnItemLongClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		List<Bookmark> bookmarks = getAllBookmarks();
		adapter.refresh(bookmarks);
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View view, int itemIndex, long itemId) {
		// Show verse
		Intent intent = new Intent(this, VerseListActivity.class);
		Bookmark bookmark = (Bookmark)view.getTag();
		intent.putExtra(Constants.VERSE_INDEX, bookmark.getVers());
		intent.putExtra(Constants.CHAPTER_INDEX, bookmark.getChapter());
		intent.putExtra(Constants.BOOK_ID, bookmark.getBookId());
		log(R.string.flurryEventBookmarkOpened);
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		final Bookmark bookmark = (Bookmark)view.getTag();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.modifyBookmarkTtle);
		final AlertDialog dialog = builder.create();
		final View dialogView = getLayoutInflater().inflate(R.layout.modify_bookmark_dialog, null);
		dialog.setView(dialogView);
		dialog.show();
		dialogView.findViewById(R.id.modifyBookmarkCancelButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		final String verseBody = bibleDataAdapter.getVerseLine(bookmark.getBookId(), bookmark.getChapter(), bookmark.getVers());
		((TextView)dialogView.findViewById(R.id.modifyBookmarkVersView)).setText("\"" + verseBody + "\"");
		final EditText commentEditor = (EditText)dialogView.findViewById(R.id.modifyBookmarkNoteEditor);
		commentEditor.setText(bookmark.getNote());
		dialogView.findViewById(R.id.modifyBookmarkSaveButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bookmark.update(commentEditor.getText().toString(), Bookmark.DEFAULT_COLOR);
				Bookmark resultBookmark = bookmarkDataAdapter.saveBookmark(bookmark);
				if (resultBookmark != null) {
					dialog.dismiss();
					BookmarkListActivity.this.log(R.string.flurryEventBookmarkEdited);
					List<Bookmark> bookmarks = getAllBookmarks();
					adapter.refresh(bookmarks);
				} else {
					Toast.makeText(BookmarkListActivity.this, R.string.errorCouldntCreateBookmark, Toast.LENGTH_LONG).show();
				}
				dialog.dismiss();
			}
		});
		dialogView.findViewById(R.id.modifyBookmarkDeleteButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bookmarkDataAdapter.deleteBookmark(bookmark);
				dialog.dismiss();
				List<Bookmark> bookmarks = getAllBookmarks();
				adapter.refresh(bookmarks);
			}
		});
		return true;
	}
	
}