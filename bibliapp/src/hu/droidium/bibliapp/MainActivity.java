package hu.droidium.bibliapp;

import java.util.HashMap;
import java.util.Map;

import hu.droidium.bibliapp.bookmar_ui.BookmarkListActivity;
import hu.droidium.bibliapp.tag_ui.TagMetaListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends BibleBaseActivity implements OnClickListener {

	private static final String TAG = MainActivity.class.getName();
	private Button lastReadVers;
	private Button toBookList;
	private View bookmarks;
	private Button settings;
	private Button tags;
	private boolean firstRun = true;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e(TAG, "Create main activity.");
		super.onCreate(savedInstanceState);
		prefs = Constants.getPrefs(this);
		setContentView(R.layout.main_layout);
		toBookList = (Button) findViewById(R.id.toBookListButton);
		toBookList.setOnClickListener(this);
		lastReadVers = (Button) findViewById(R.id.lastReadVersButton);
		lastReadVers.setOnClickListener(this);
		settings = (Button) findViewById(R.id.toSettingsButton);
		settings.setOnClickListener(this);
		bookmarks = findViewById(R.id.bookmarkLink);
		bookmarks.setOnClickListener(this);
		tags = (Button)findViewById(R.id.tagsButton);
		tags.setOnClickListener(this);
		Log.e(TAG, "Main activity created.");
	}
	
	@Override
	protected void onStart() {
		Log.e(TAG, "Starting main activity.");
		super.onStart();
		log(R.string.flurryEventAppStarted);
	}
	
	@Override
	protected void onResume() {
		Log.e(TAG, "Resuming main activity.");
		super.onResume();
		if (firstRun) {
			Log.e(TAG, "First run.");
			firstRun = false;
			// Check if user wants to use Facebook
			int facebookAsk = prefs.getInt(Constants.FACEBOOK_LOGIN_DECISION, Constants.FACEBOOK_UNKNOWN);
			switch(facebookAsk) {
				case Constants.FACEBOOK_UNKNOWN: {
					Log.e(TAG, "Facebook decision not made yet.");
					OnClickListener firstButtonListener = new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							prefs.edit().putInt(Constants.FACEBOOK_LOGIN_DECISION, Constants.FACEBOOK_LOGIN).commit();
							login();
						}
					};
					OnClickListener secondButtonListener = null;
					OnClickListener thirdButtonListener = new OnClickListener() {
						@Override
						public void onClick(View v) {
							prefs.edit().putInt(Constants.FACEBOOK_LOGIN_DECISION, Constants.FACEBOOK_DONT_ASK).commit();
						}
					};
					showDialog(R.string.facebookDialogTitle, 
							R.string.facebookDialogMessage,
							R.string.facebookDialogLogin, firstButtonListener,
							R.string.facebookDialogLater, secondButtonListener,
							R.string.facebookDialogNever, thirdButtonListener,
							Orientation.VERTICAL);
					break;
				}
				case Constants.FACEBOOK_LOGIN: {
					Log.e(TAG, "Facebook login.");
					login();
					break;
				}
				default: {
					Log.e(TAG, "No Facebook login.");
					break;
				}
			}
		}
		if (prefs.contains(Constants.LAST_READ_BOOK_ID)) {
			lastReadVers.setEnabled(true);
		} else {
			lastReadVers.setEnabled(false);
		}
		tags.setText(getString(R.string.verseByTag, tagDataAdapter.getTotalTags()));
		
		// Finding if it's time to rate
		int openedCount = getUsageCount();
		int rateState = prefs.getInt(Constants.RATE_STATE, Constants.RATE_STATE_NOT_DECIDED);
		int lastPower = prefs.getInt(Constants.LAST_RATE_DIALOG_POWER, 0);
		boolean timeToRate = false;
		int limit = ((int)(Math.pow(2, lastPower)) * Constants.RATE_LIMIT);
		if (limit < openedCount) {
			timeToRate = true;
		}
		if (openedCount > Constants.RATE_LIMIT && ((rateState == Constants.RATE_STATE_NOT_DECIDED) || (timeToRate && rateState == Constants.RATE_STATE_LATER))) {
			lastPower ++;
			prefs.edit().putInt(Constants.LAST_RATE_DIALOG_POWER, lastPower).commit();
			OnClickListener rateListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					prefs.edit().putInt(Constants.RATE_STATE, Constants.RATE_STATE_NEVER).commit();
					startActivity( new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=hu.droidium.bibliapp") ) );
				}
			};
			OnClickListener emailListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					prefs.edit().putInt(Constants.RATE_STATE, Constants.RATE_STATE_LATER).commit();
					Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","lajthabalazs@gmail.com", null));
					sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.rateEmailSubject));
					startActivity(Intent.createChooser(sendIntent, getString(R.string.sendSuggestions)));					
				}
			};
			OnClickListener laterListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					prefs.edit().putInt(Constants.RATE_STATE, Constants.RATE_STATE_LATER).commit();
				}
			};
			showDialog(R.string.ratingDialogTitle, R.string.ratingDialogMessage,
					R.string.ratingDialogRate, rateListener,
					R.string.ratingDialogEmail, emailListener,
					R.string.ratingDialogLater, laterListener,
					Orientation.VERTICAL);
		}
	}
	
	@Override
	protected void facebookSessionOpened() {
		Log.e(TAG, "Facebook session opened.");
	}

	@Override
	protected void facebookSessionClosed() {
		Log.e(TAG, "Facebook session closed.");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.toBookListButton: {
				Intent intent = new Intent(this, BookListActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.lastReadVersButton: {
				SharedPreferences prefs = Constants.getPrefs(this);
				prefs.edit().putBoolean(Constants.SHOULD_OPEN_LAST_READ, true).commit();
				Intent intent = new Intent(this, BookListActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.toSettingsButton: {
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.bookmarkLink: {
				Map<String, String> params = new HashMap<String, String>();
				params.put(getString(R.string.flurryParamEventSource), MainActivity.class.getName());
				log(R.string.flurryEventBookmarksOpened, params );
				Intent intent = new Intent(this, BookmarkListActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.tagsButton: {
				Map<String, String> params = new HashMap<String, String>();
				params.put(getString(R.string.flurryParamTagCount), "" + tagDataAdapter.getTotalTags());
				log(R.string.flurryEventTagsOpened, params );
				Intent intent = new Intent(this, TagMetaListActivity.class);
				startActivity(intent);
				break;
			}
			default: {
				break;
			}
		}
	}
}