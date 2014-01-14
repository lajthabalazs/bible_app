package hu.droidium.bibliapp;

import hu.droidium.bibliapp.data.Location;

import java.util.Arrays;
import java.util.List;

import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LocationListActivity extends BibleBaseActivity {

	private static final String TAG = LocationListActivity.class.getName();
	private ListView locationList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationList = new ListView(this);
		locationList.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(locationList);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		List<String> locations = locationAdapter.getLocationNames();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,locations.toArray(new String[locations.size()]));
		locationList.setAdapter(adapter);
	}
	
	@Override
	protected void facebookSessionOpened() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void facebookSessionClosed() {
		// TODO Auto-generated method stub
		
	}

}
