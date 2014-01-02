package hu.droidium.bibliapp.map;

import hu.droidium.bibliapp.BibleBaseActivity;
import hu.droidium.bibliapp.Constants;
import hu.droidium.bibliapp.R;
import hu.droidium.bibliapp.data.Location;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class MapActivity extends BibleBaseActivity {

	private static final String TAG = MapActivity.class.getName();
	private BibleMapView mapView;
	private String bookId;
	private int chapter;
	private IMapController controller;
	private int verseIndex;
	private String verseText;
	private String verseAbbreviation;
	private ItemizedIconOverlay<OverlayItem> locationsOverlay;
	private ResourceProxyImpl resourceProxy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.osm_map);
		LinearLayout mapParent = (LinearLayout) findViewById(R.id.mapParent);
		resourceProxy = new ResourceProxyImpl(
				getApplicationContext());
		mapView = new BibleMapView(this, 256, resourceProxy);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mapView.setLayoutParams(params);
		mapView.setBuiltInZoomControls(true);
		controller = mapView.getController();
		mapParent.addView(mapView);
		Intent intent = getIntent();
		bookId = intent.getStringExtra(Constants.BOOK_ID);
		chapter = intent.getIntExtra(Constants.CHAPTER_INDEX, 0);
		verseIndex = intent.getIntExtra(Constants.VERSE_INDEX, 0);
		verseText = bibleDataAdapter.getVerseLine(bookId, chapter, verseIndex);
		verseAbbreviation = bibleDataAdapter.getBookAbbreviation(bookId);
	}

	@Override
	protected void onResume() {
		super.onResume();
		double west = 180.0;
		double east = -180.0;
		double north = -90.0;
		double south = 90.0;

		/* Itemized Overlay */
		/*
		 * Create a static ItemizedOverlay showing a some Markers on some
		 * cities.
		 */
		final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		List<Location> locations = locationAdapter.getLocations(bookId, chapter + 1, verseIndex + 1);
		Log.e(TAG, "Getting location for " + bookId + " " + (chapter + 1) + " " + (verseIndex + 1) + ". Found " + locations.size());
		for (Location location : locations) {
			double lat = Double.parseDouble(location.getLat());
			double lon = Double.parseDouble(location.getLon());
			items.add(new OverlayItem(location.getName(), "", new GeoPoint(lat, lon)));
			Log.e(TAG, "Adding location " + location.getName());
			west = Math.min(west, lon);
			east = Math.max(east, lon);
			north = Math.min(north, lat);
			south = Math.max(south, lat);
		}
		/* OnTapListener for the Markers, shows a simple Toast. */
		locationsOverlay = new ItemizedIconOverlay<OverlayItem>(
				items,
				getResources().getDrawable(R.drawable.bookmark),
				new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
					@Override
					public boolean onItemSingleTapUp(final int index,
							final OverlayItem item) {
						Toast.makeText(
								MapActivity.this,
								"Item '" + item.getTitle() + "' (index="
										+ index + ") got single tapped up",
								Toast.LENGTH_LONG).show();
						return true; // We 'handled' this event.
					}

					@Override
					public boolean onItemLongPress(final int index,
							final OverlayItem item) {
						Toast.makeText(
								MapActivity.this,
								"Item '" + item.getTitle() + "' (index="
										+ index + ") got long pressed",
								Toast.LENGTH_LONG).show();
						return false;
					}
				}, resourceProxy);
		mapView.getOverlays().add(locationsOverlay);
		mapView.setTargetBox(new BoundingBoxE6(north, east, south, west));
	}

	@Override
	protected void facebookSessionOpened() {
	}

	@Override
	protected void facebookSessionClosed() {
	}

}
