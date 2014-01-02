package hu.droidium.bibliapp;

import hu.droidium.bibliapp.data.Location;

import java.util.List;

import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LocationListActivity extends BibleBaseActivity {

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
		List<Location> locations = locationAdapter.getAllLocations();
		String[] locationNames = new String[locations.size()];
		for (int i = 0; i < locationNames.length; i++){
			locationNames[i] = locations.get(i).getName() + " >" + locations.get(i).getBookId() + "< >" + locations.get(i).getChapter() + ":" + locations.get(i).getVerse();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, locationNames);
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
