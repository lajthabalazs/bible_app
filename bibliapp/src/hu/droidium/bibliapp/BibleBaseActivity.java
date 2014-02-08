package hu.droidium.bibliapp;

import hu.droidium.bibliapp.data.AssetBibleDataAdapter;
import hu.droidium.bibliapp.data.BibleDataAdapter;
import hu.droidium.bibliapp.data.Bookmark;
import hu.droidium.bibliapp.data.BookmarkDataAdapter;
import hu.droidium.bibliapp.data.LocationAdapter;
import hu.droidium.bibliapp.data.TagDataAdapter;
import hu.droidium.bibliapp.data.Translator;
import hu.droidium.bibliapp.database.DatabaseManager;
import hu.droidium.bibliapp.database.DatabaseUpdateService;
import hu.droidium.bibliapp.database.TagMeta;
import hu.droidium.flurry_base.Log;
import hu.droidium.flurry_base.LogCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

/**
 * This activity should be added as touch event listener to the views that implement touch listeners,
 * otherwise fling events won't be captured.
 * 
 * @author Balazs Lajtha
 *
 */
public abstract class BibleBaseActivity extends DialogBaseActivity implements
		StatusCallback, OnGestureListener, OnTouchListener {
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private static final String SHARED_PREFS = "Facebook prefs";
	private static final String MESSAGE_KEY = "Post message key";
	private static final String VERS_ID_KEY = "Vers id key";
	private static final String VERS_BODY_KEY = "Vers body key";

	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private static final String TAG = BibleBaseActivity.class.getName();
	private static final float FLING_LIMIT = 3.0f;

	private boolean sessionOnline = false;
	
	private boolean pendingPublishReauthorization = false;
	
	protected BibleDataAdapter bibleDataAdapter;
	protected BookmarkDataAdapter bookmarkDataAdapter;
	protected TagDataAdapter tagDataAdapter;
	protected LocationAdapter locationAdapter;
	
	private Translator translator;
	
	private DatabaseManager databaseManager;
	private static ArrayList<TagMeta> localizedTagMetas;

	private GestureDetector gestureDetector;
	private float screenPixels;
	private double screenInches;
	

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bibleDataAdapter = new AssetBibleDataAdapter(this);
		// Most functionality is covered by a database manager
		databaseManager = new DatabaseManager(this);
		bookmarkDataAdapter = databaseManager;
		tagDataAdapter = databaseManager;
		locationAdapter = databaseManager;
		translator = databaseManager;
		
		
		// Update database
		Log.d(LogCategory.DATABASE, TAG, "Checking for updates.");
		startService(new Intent(this, DatabaseUpdateService.class));
		gestureDetector = new GestureDetector(this, this);
		Display display = getWindowManager().getDefaultDisplay();
		screenPixels = display.getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
		double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
		screenInches = Math.sqrt(x + y);
		Log.d(LogCategory.LIFECYCLE, TAG, "Screen inches : " + screenInches);
		// Check if user wants to use Facebook
		SharedPreferences prefs = Constants.getPrefs(this);
		int facebookAsk = prefs.getInt(Constants.FACEBOOK_LOGIN_DECISION, Constants.FACEBOOK_UNKNOWN);
		if (facebookAsk == Constants.FACEBOOK_LOGIN) {
			login(savedInstanceState);
		}
		
	}
	
	@Override
	public void setContentView(int layoutResID) {
		setContentView(getLayoutInflater().inflate(layoutResID, null));
	}
	
	@Override
	public void setContentView(View view) {
		setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		view.setOnTouchListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Check if user wants to participate in logging
		if (!isLoggingEnabledSelected()) {
			OnClickListener firstButtonListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					setLoggingEnabled(true);
				}
			};
			OnClickListener secondButtonListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					setLoggingEnabled(false);
				}
			};
			showDialog(R.string.flurryDialogTitle, 
					R.string.flurryDialogMessage,
					R.string.flurryDialogEnable, firstButtonListener,
					R.string.flurryDialogDisable, secondButtonListener,
					Orientation.HORIZONTAL);
		}
	}

	protected void login(Bundle savedInstanceState) {
		
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, this, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(this));
            }
        }
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Session session = Session.getActiveSession();
		if (session != null) {
			session.addCallback(this);
		}
	}
	
	@Override
    public void onStop() {
        super.onStop();
		Session session = Session.getActiveSession();
		if (session != null) {
			Session.getActiveSession().removeCallback(this);
		}
    }
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		databaseManager.destroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
        Session session = Session.getActiveSession();
		if (session != null) {
			Session.saveSession(session, outState);
		}
	}

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		 if (exception != null) {
             new AlertDialog.Builder(this)
                     .setTitle(R.string.facebookLoginError)
                     .setMessage(exception.getMessage())
                     .setPositiveButton(android.R.string.ok, null)
                     .show();
         }		
		if (state.isOpened()) {
			checkIfOnline();
		} else if (state.isClosed()) {
			facebookSessionClosed();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session session = Session.getActiveSession();
		if (session != null) {
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		}
	}
	
	private void checkIfOnline() {
        final Session session = Session.getActiveSession();
		if (session != null) {
			Request.newMeRequest(session, new Request.GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user!=null) {
						Log.i(LogCategory.FACEBOOK, TAG, "We have a user:" + user.getName());
						sessionOnline = true;
						if (pendingPublishReauthorization
								&& session.getState().equals(SessionState.OPENED_TOKEN_UPDATED)) {
							pendingPublishReauthorization = false;
							publishStory();
						}
						facebookSessionOpened();
					} else {
						sessionOnline = false;
						facebookSessionClosed();
					}
				}
			}).executeAsync();
		}
	}

	protected void facebookSessionOpened() {}

	protected void facebookSessionClosed() {}

	public List<Bookmark> getBookmarksForChapter(String bookId, int chapterIndex) {
		List<Bookmark> bookmarks = bookmarkDataAdapter.getBookmarksForChapter(bookId, chapterIndex);
		return bookmarks;
	}

	public List<Bookmark> getAllBookmarks() {
		return bookmarkDataAdapter.getAllBookmarks(null, false);
	}
	
	public List<TagMeta> getTags() {
		if (localizedTagMetas == null) {
			localizedTagMetas = new ArrayList<TagMeta>();
			List<TagMeta> tagMetas = tagDataAdapter.getTagMetas();
			for (TagMeta tagMeta : tagMetas) {
				String localizedName = translator.getTranslation(Locale.getDefault().getDisplayLanguage(), tagMeta.getName());
				TagMeta localizedMeta = new TagMeta(tagMeta.getId(), localizedName, tagMeta.getColor());
				localizedTagMetas.add(localizedMeta);
			}
		}
		return localizedTagMetas;
	}
	
	protected int getUsageCount() {
		SharedPreferences prefs = Constants.getPrefs(this);
		return prefs.getInt(Constants.VERSE_LIST_OPENED_COUNT_KEY, 0);
	}
	
	protected void publishStory(String message, String versId, String versBody) {
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(MESSAGE_KEY, message);
		editor.putString(VERS_ID_KEY, versId);
		editor.putString(VERS_BODY_KEY, versBody);
		editor.commit();
		publishStory();
	}

	private void publishStory() {
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
		String message = prefs.getString(MESSAGE_KEY, null);
		String versBody =  prefs.getString(VERS_BODY_KEY, null);
		String versId =  prefs.getString(VERS_ID_KEY, null);
		if (message == null) {
			if (versBody == null) {
				message = "Ma is olvastam a Bibliát.";
			} else {
				message = "Mindenkinek ajánlom ezt az igét: \"" + versBody + "\", " + versId;
			}
		} else {
			if (versBody != null) {
				message = message + " \"" + versBody + "\", " + versId;
			}
		}

		Session session = Session.getActiveSession();

		if (sessionOnline && session != null) {

			// Check for publish permissions
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				pendingPublishReauthorization = true;
				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
						this, PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}

			Bundle postParams = new Bundle();
			postParams.putString("name", "Droidium Biblia-olvasó");
			postParams.putString("message",
					message);
			postParams.putString("caption",
					"Olvasd te is minden nap a Bibliát okostelefonodon!");
			postParams
					.putString(
							"description",
							"A Droidium Biblia-olvasó alkalmazása megkönnyíti a Szentírás tanulmányozását. Kövesd ismerőseidet, vagy lelkészed, papod ajánlásait, mentsd el és oszd meg kedvenc igéidet!");
			postParams.putString("link", "https://play.google.com/store/apps/details?id=hu.droidium.bibliapp");
			postParams
					.putString("picture",
							"https://lh6.ggpht.com/Bwplwh0P2hTx9sxt9GwY9mhc4TnRzTS8NcvjfW2CezbxqV1jN2UCzpJ6QA5g7twovdw=w124");

			Request.Callback callback = new Request.Callback() {
				public void onCompleted(Response response) {
					JSONObject graphResponse = response.getGraphObject()
							.getInnerJSONObject();
					String postId = null;
					try {
						postId = graphResponse.getString("id");
					} catch (JSONException e) {
						Log.i(LogCategory.FACEBOOK, "TFacebookEnabledBibleActivity", "JSON error "
								+ e.getMessage());
					}
					FacebookRequestError error = response.getError();
					if (error != null) {
						Toast.makeText(
								BibleBaseActivity.this
										.getApplicationContext(),
								error.getErrorMessage(), Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(
								BibleBaseActivity.this
										.getApplicationContext(),
								postId, Toast.LENGTH_LONG).show();
					}
				}
			};

			Request request = new Request(session, "me/feed", postParams,
					HttpMethod.POST, callback);

			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}

	}

	private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}
	
	public static void silentLogin(Context context) {
		if(!isLoggedInWithFacebook(context)) {
			Session.openActiveSession(context, null,false, new StatusCallback() {
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					Log.d(LogCategory.FACEBOOK, TAG, "Silent Facebook login succesfull");
				}
			});
		} else {
			Log.d(LogCategory.FACEBOOK, TAG, "User already logged in with Facebook");
		}
	}
	
	public static boolean isLoggedInWithFacebook(Context context) {
		if (Session.getActiveSession() == null) {
			return false;
		} else {
			return true;
		}
	}


	public boolean isFacebookSessionOpened() {
		return sessionOnline;
	}

	public String[] getBookIds() {
		return bibleDataAdapter.getBookIds();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		float physicalVelocityInch = (float) (velocityX / screenPixels * screenInches);
		if (Math.abs(velocityX) > Math.abs(velocityY) * 2) {
			if (physicalVelocityInch > FLING_LIMIT) {
				return flingLeft();
			} else if (physicalVelocityInch < - FLING_LIMIT) {
				return flingRight();
			}
		}
		return false;
	}

	protected boolean flingRight() {
		return false;
	}
	protected boolean flingLeft() {
		return false;
	}
	
	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
}