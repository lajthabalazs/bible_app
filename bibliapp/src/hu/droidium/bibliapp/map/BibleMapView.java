package hu.droidium.bibliapp.map;

import org.osmdroid.ResourceProxy;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapView;

import android.content.Context;
import hu.droidium.flurry_base.Log;
import hu.droidium.flurry_base.LogCategory;

public class BibleMapView extends MapView{

	private static final String TAG = BibleMapView.class.getName();
	private BoundingBoxE6 targetBox = null;

	public BibleMapView(Context context, int tileSizePixels,
			ResourceProxy resourceProxy) {
		super(context, tileSizePixels, resourceProxy);
	}
	
	public void setTargetBox(BoundingBoxE6 targetBox) {
		this.targetBox = targetBox;
	}
	
	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		super.onLayout(arg0, arg1, arg2, arg3, arg4);
		if (targetBox != null) {			
			Log.d(LogCategory.MAP, TAG, "Zooming to bounding box");
			zoomToBoundingBox(targetBox);
			targetBox = null;
		}
	}
}
