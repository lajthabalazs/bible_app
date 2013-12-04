package hu.droidium.bibliapp;

import hu.droidium.bibliapp.data.AssetReader;
import hu.droidium.bibliapp.data.Book;
import hu.droidium.bibliapp.database.Bookmark;
import hu.droidium.bibliapp.database.DatabaseManager;
import hu.droidium.bibliapp.database.TagMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.model.GraphUser;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public abstract class BibleBaseActivity extends Activity implements
		StatusCallback {
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private static final String SHARED_PREFS = "Facebook prefs";
	private static final String MESSAGE_KEY = "Post message key";
	private static final String VERS_ID_KEY = "Vers id key";
	private static final String VERS_BODY_KEY = "Vers body key";

	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private static final String TAG = "FacebookEnabledBibleActivity";

	private Session session;
	private boolean sessionOnline = false;
	private UiLifecycleHelper uiHelper;	
	
	private boolean pendingPublishReauthorization = false;
	protected DatabaseManager databaseManager;
	private SharedPreferences prefs;
	private static ArrayList<TagMeta> localizedTagMetas;

	private static Vector<String[]> titles;

	private static HashMap<String, Book> books = new HashMap<String, Book>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = Constants.getPrefs(this);
		databaseManager = new DatabaseManager(this);
		uiHelper = new UiLifecycleHelper(this, this);
		uiHelper.onCreate(savedInstanceState);
	}
	
	/*
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		TextView title = (TextView)findViewById(R.id.activityTitle);
		if (title != null) {
			Constants.scaleText(title, this);
		}
	}
	*/

	@Override
	protected void onResume() {
		super.onResume();
		// Check if user wants to use Facebook
		int facebookAsk = prefs.getInt(Constants.FACEBOOK_LOGIN_DECISION, Constants.FACEBOOK_UNKNOWN);
		switch(facebookAsk) {
			case Constants.FACEBOOK_UNKNOWN: {
				Builder builder = new Builder(this);
				builder.setTitle(R.string.facebookDialogTitle);
				builder.setMessage(R.string.facebookDialogMessage);
				builder.setPositiveButton(R.string.facebookDialogLogin,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								prefs.edit().putInt(Constants.FACEBOOK_LOGIN_DECISION, Constants.FACEBOOK_LOGIN).commit();
								login();
							}
						});
				builder.setNeutralButton(R.string.facebookDialogLater,
						new DialogInterface.OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) {}});
				builder.setNegativeButton(R.string.facebookDialogNever,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								prefs.edit().putInt(Constants.FACEBOOK_LOGIN_DECISION, Constants.FACEBOOK_DONT_ASK).commit();
							}
						});
				builder.create().show();
				break;
			}
			case Constants.FACEBOOK_LOGIN: {
				login();
				break;
			}
		}
	}

	private void login() {
 		if (session != null) {
 			session.addCallback(this);
		} else {
			Session.openActiveSession(this, true, this);
 		}
	}
	
	@Override
	protected void onPause() {
		uiHelper.onPause();
		if (session != null) {
			session.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		uiHelper.onDestroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		this.session = session;
		session.addCallback(this);
		if (state.isOpened()) {
			this.session = session;
			checkIfOnline();
		} else if (state.isClosed()) {
			facebookSessionClosed();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	@SuppressWarnings("deprecation")
	private void checkIfOnline() {
		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				// TODO process user information if needed
				if (user!=null) {
					Log.i("We have a user!", user.getName());
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
		});
	}

	protected abstract void facebookSessionOpened();

	protected abstract void facebookSessionClosed();

	public List<Bookmark> getBookmarksForChapter(String bookId, int chapterIndex) {
		List<Bookmark> bookmarks = databaseManager.getBookmarksForChapter(bookId, chapterIndex);
		return bookmarks;
	}

	public List<Bookmark> getAllBookmarks() {
		return databaseManager.getAllBookmarks(null, false);
	}
	
	public List<TagMeta> getTags() {
		if (localizedTagMetas == null) {
			localizedTagMetas = new ArrayList<TagMeta>();
			List<TagMeta> tagMetas = databaseManager.getTagMetas();
			for (TagMeta tagMeta : tagMetas) {
				String localizedName = databaseManager.getTranslation(Locale.getDefault().getDisplayLanguage(), tagMeta.getName());
				TagMeta localizedMeta = new TagMeta(tagMeta.getId(), localizedName, tagMeta.getColor());
				localizedTagMetas.add(localizedMeta);
			}
		}
		return localizedTagMetas;
	}
	
	public Bookmark saveBookmark(String note, String book, int chapter, int vers, String color) {
		return databaseManager.saveBookmark(new Bookmark(note, book, chapter, vers, color));
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
						Log.i("TFacebookEnabledBibleActivity", "JSON error "
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
					Log.e(TAG, "Silent Facebook login succesfull");
				}
			});
		} else {
			Log.e(TAG, "User already logged in with Facebook");
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

	public Vector<String[]> getBookTitles() {
		if (titles == null) {
			titles = AssetReader.readTitles(this);
		}
		return titles;
	}
	
	public Book getBook(String bookId) {
		Book book = books.get(bookId);
		if (book == null) {
			book = AssetReader.readFile(bookId, this);
			books.put(bookId, book);
		}
		return book;
	}
}