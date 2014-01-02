package hu.droidium.bibliapp.database;

import hu.droidium.bibliapp.data.AssetReader;
import hu.droidium.bibliapp.data.Location;
import hu.droidium.bibliapp.data.VersionManager;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DatabaseUpdateService extends IntentService {

	private static final String TAG = DatabaseUpdateService.class.getName();
	private static final String TAG_META_FILE = "tags.txt";
	private static final String LOCATION_FILE = "locations.txt";

	public DatabaseUpdateService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		DatabaseManager databaseManager = new DatabaseManager(this);
		updateTagMetas(databaseManager);
		updateLocations(databaseManager);
	}
	
	private void updateTagMetas(DatabaseManager databaseManager) {
		int assetVersion = VersionManager.getAssetVersion(this, TAG_META_FILE);
		int installedVersion = VersionManager.getInstalledVersion(this, TAG_META_FILE);
		if (assetVersion != installedVersion) {
			Log.e(TAG, "Update needed, current version " + installedVersion + ", available version " + assetVersion);
			List<TagMeta> tagMetas = AssetReader.parseTagMetas(this, TAG_META_FILE);
			TagMeta tag;
			int lastUpdated = VersionManager.getUpdateProgress(this, assetVersion, TAG_META_FILE);
			for (int progress = lastUpdated + 1; progress < tagMetas.size(); progress++) {
				tag = tagMetas.get(progress);
				databaseManager.addTagMeta(tag.getId(), tag.getName(), tag.getColor());
				VersionManager.setUpdateProgress(this, TAG_META_FILE, assetVersion, progress);
			}
			VersionManager.setInstalledVersion(this, TAG_META_FILE, assetVersion);
			Log.e(TAG, "Tags updated from version " + installedVersion + " to " + assetVersion);
		} else {
			Log.e(TAG, "Tags up to date, version " + installedVersion);
		}
	}
	
	private void updateLocations(DatabaseManager databaseManager) {
		int assetVersion = VersionManager.getAssetVersion(this, LOCATION_FILE);
		int installedVersion = VersionManager.getInstalledVersion(this, LOCATION_FILE);
		if (assetVersion != installedVersion) {
			Log.e(TAG, "Update needed, current version " + installedVersion + ", available version " + assetVersion);
			List<Location> locations = AssetReader.parseLocations(this, LOCATION_FILE);
			int lastUpdated = VersionManager.getUpdateProgress(this, assetVersion, LOCATION_FILE);
			for (int progress = lastUpdated + 1; progress < locations.size(); progress++) {
				databaseManager.addLocation(locations.get(progress));
				VersionManager.setUpdateProgress(this, LOCATION_FILE, assetVersion, progress);
			}
			VersionManager.setInstalledVersion(this, LOCATION_FILE, assetVersion);
			Log.e(TAG, "Locations updated from version " + installedVersion + " to " + assetVersion);
		} else {
			Log.e(TAG, "Locations up to date, version " + installedVersion);
		}
	}
}