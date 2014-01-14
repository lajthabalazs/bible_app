package hu.droidium.bibliapp.map;

import hu.droidium.bibliapp.R;

import org.osmdroid.bonuspack.overlays.DefaultInfoWindow;
import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.views.MapView;

import android.view.View;
import android.widget.TextView;

public class BibleInfoWindow extends DefaultInfoWindow {

	@SuppressWarnings("unused")
	private static final String TAG = BibleInfoWindow.class.getName();
	@SuppressWarnings("unused")
	private final MapActivity mapActivity;
	int mSelectedPoint;

	public BibleInfoWindow(MapActivity mapActivity, int layoutResId,
			MapView mapView) {
		super(layoutResId, mapView);
		this.mapActivity = mapActivity;
	}

	@Override
	public void onOpen(Object item) {
		ExtendedOverlayItem extendedOverlayItem = (ExtendedOverlayItem) item;
		String title = extendedOverlayItem.getTitle();
		if (title == null) {
			mView.findViewById(R.id.bubbleTitle).setVisibility(View.GONE);
		} else {
			mView.findViewById(R.id.bubbleTitle).setVisibility(View.VISIBLE);
			((TextView) mView.findViewById(R.id.bubbleTitle)).setText(title);
		}

		String snippet = extendedOverlayItem.getDescription();
		if (snippet == null) {
			mView.findViewById(R.id.bubbleText).setVisibility(View.GONE);
		} else {
			mView.findViewById(R.id.bubbleText).setVisibility(View.VISIBLE);
			((TextView) mView.findViewById(R.id.bubbleText)).setText(snippet);
		}
	}

	@Override
	public void onClose() {
	}
}