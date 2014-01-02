package hu.droidium.bibliapp;

import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class MapActivity extends BibleBaseActivity {

	private MapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.osm_map);
		LinearLayout mapParent = (LinearLayout)findViewById(R.id.mapParent);
        ResourceProxyImpl mResourceProxy = new ResourceProxyImpl(getApplicationContext());
        mapView = new MapView(this, 256, mResourceProxy);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mapView.setLayoutParams(params);
		mapParent.addView(mapView);
	}
	
	@Override
	protected void facebookSessionOpened() {
	}

	@Override
	protected void facebookSessionClosed() {
	}

}
