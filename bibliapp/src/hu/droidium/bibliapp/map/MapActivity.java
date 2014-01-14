package hu.droidium.bibliapp.map;

import hu.droidium.bibliapp.BibleBaseActivity;
import hu.droidium.bibliapp.Constants;
import hu.droidium.bibliapp.R;
import hu.droidium.bibliapp.data.Location;
import hu.droidium.bibliapp.data.Verse;
import hu.droidium.flurry_base.Log;
import hu.droidium.flurry_base.LogCategory;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.bonuspack.overlays.ItemizedOverlayWithBubble;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.TilesOverlay;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MapActivity extends BibleBaseActivity implements OnItemGestureListener<OverlayItem> {

	private static final String TAG = MapActivity.class.getName();
	private static final double MIN_BOX_LAT = 5.0;
	private static final double MIN_BOX_LON = 5.0;
	private BibleMapView mapView;
	private String bookId;
	private int chapter;
	private int verseIndex;
	private ItemizedOverlayWithBubble<ExtendedOverlayItem> locationsOverlay;
	private ResourceProxyImpl resourceProxy;
	private TextView verseTitleView;
	private TextView verseTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.osm_map);
		LinearLayout mapParent = (LinearLayout) findViewById(R.id.mapParent);
		resourceProxy = new ResourceProxyImpl(
				getApplicationContext());
		mapView = new BibleMapView(this, 256, resourceProxy);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, 1.0f);		
		mapView.setLayoutParams(params);
		mapView.setBuiltInZoomControls(true);
		mapParent.addView(mapView, 0);
		CloudmadeUtil.retrieveCloudmadeKey(getApplicationContext());
		// Add tiles layer with custom tile source
		final MapTileProviderBasic tileProvider = new MapTileProviderBasic(
				getApplicationContext());
		final ITileSource tileSource = new XYTileSource("FietsRegionaal", null,
				3, 18, 256, ".png",
				"http://toolserver.org/tiles/hikebike/");
		tileProvider.setTileSource(tileSource);
		final TilesOverlay tilesOverlay = new TilesOverlay(tileProvider,
				this.getBaseContext());
		tilesOverlay.setLoadingBackgroundColor(Color.WHITE);
		mapView.getOverlays().add(tilesOverlay);
		verseTitleView = (TextView)findViewById(R.id.mapVerseTitle);
		verseTextView = (TextView)findViewById(R.id.mapVerseText);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		bookId = intent.getStringExtra(Constants.BOOK_ID);
		chapter = intent.getIntExtra(Constants.CHAPTER_INDEX, 0);
		verseIndex = intent.getIntExtra(Constants.VERSE_INDEX, 0);
		verseTitleView.setText(Verse.getVerseLabel(bookId, chapter, verseIndex, bibleDataAdapter));		
		verseTextView.setText(bibleDataAdapter.getVerseLine(bookId, chapter, verseIndex));

		double west = 180.0;
		double east = -180.0;
		double north = -90.0;
		double south = 90.0;

		final ArrayList<ExtendedOverlayItem> items = new ArrayList<ExtendedOverlayItem>();
		List<Location> locations = locationAdapter.getLocations(bookId, chapter + 1, verseIndex + 1);
		Log.d(LogCategory.MAP, TAG, "Getting location for " + bookId + " " + (chapter + 1) + " " + (verseIndex + 1) + ". Found " + locations.size());
		Drawable marker = getResources().getDrawable(R.drawable.map_marker);
		for (Location location : locations) {
			double lat = Double.parseDouble(location.getLat());
			double lon = Double.parseDouble(location.getLon());
			ExtendedOverlayItem item = new ExtendedOverlayItem(location.getName(), null, new GeoPoint(lat, lon), this);
			item.setMarker(marker);
			items.add(item);

			Log.d(LogCategory.MAP, TAG, "Adding location " + location.getName());
			west = Math.min(west, lon);
			east = Math.max(east, lon);
			north = Math.max(north, lat);
			south = Math.min(south, lat);
		}
		Log.d(LogCategory.MAP, TAG, "North " + north + " East " + east + " South " + south + " West " + west);
		// If visible area is too small, increase bounding box.
		double latDif = north - south;
		if (latDif < MIN_BOX_LAT)  {
			north = north + (MIN_BOX_LAT - latDif) / 2;
			south = south - (MIN_BOX_LAT - latDif) / 2;
			Log.d(LogCategory.MAP, TAG, "Corrected coordinated: North " + north + " South " + south);
		}
		double lonDif = east - west;
		if (lonDif < MIN_BOX_LON)  {
			east = east + (MIN_BOX_LON - lonDif) / 2;
			west = west - (MIN_BOX_LON - lonDif) / 2;
			Log.d(LogCategory.MAP, TAG, "Corrected coordinated: East " + east + " West " + west);
		}
		locationsOverlay = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(this, items, mapView, new BibleInfoWindow(this, R.layout.bubble_layout, mapView));
		mapView.getOverlays().add(locationsOverlay);
		Log.d(LogCategory.MAP, TAG, "North " + north + " East " + east + " South " + south + " West " + west);
		mapView.setTargetBox(new BoundingBoxE6(north, east, south, west));
	}

	@Override
	protected void facebookSessionOpened() {
	}

	@Override
	protected void facebookSessionClosed() {
	}
	
	public boolean onItemSingleTapUp(final int index,
			final OverlayItem item) {
		return true;
	}

	@Override
	public boolean onItemLongPress(final int index,
			final OverlayItem item) {
		return false;
	}
}