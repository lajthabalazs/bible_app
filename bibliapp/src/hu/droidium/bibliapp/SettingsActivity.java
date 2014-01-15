package hu.droidium.bibliapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import hu.droidium.flurry_base.Log;
import hu.droidium.flurry_base.LogCategory;

public class SettingsActivity extends BibleBaseActivity implements OnClickListener {
	
	private static final String TAG = "SettingsActivity";
	private TextView smallerText;
	private TextView largerText;
	private SharedPreferences prefs;
	private TextView actualText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = Constants.getPrefs(this);
		setContentView(R.layout.settings);
		smallerText = (TextView)findViewById(R.id.settingsSmallerText);
		smallerText.setOnClickListener(this);
		actualText = (TextView)findViewById(R.id.settingsActualText);
		largerText = (TextView)findViewById(R.id.settingsLargerText);
		largerText.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshUI();
	}
	
	private void refreshUI() {
		int currentMultiplierFactor = prefs.getInt(Constants.TEXT_SIZE_KEY, 0);
		double currentMultiplier = Math.pow(Constants.TEXT_SIZE_FACTOR, currentMultiplierFactor);
		Log.v(LogCategory.UI, TAG, "Factor " + currentMultiplierFactor + " -> " + currentMultiplier);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int baseSize = (int)(getResources().getDimension(R.dimen.text_size_list_item_text_normal) / metrics.scaledDensity);
		Log.v(LogCategory.UI, TAG, "Base dimension " + baseSize);
		int actualSize = (int) (baseSize * currentMultiplier);
		Log.v(LogCategory.UI, TAG, "Actual dimension " + baseSize);
		int smallerSize = (int) (baseSize * currentMultiplier / Constants.TEXT_SIZE_FACTOR);
		int largerSize = (int) (baseSize * currentMultiplier * Constants.TEXT_SIZE_FACTOR);
		smallerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, smallerSize);
		largerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, largerSize);
		actualText.setTextSize(TypedValue.COMPLEX_UNIT_SP, actualSize);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.settingsSmallerText: {
				int currentMultiplierFactor = prefs.getInt(Constants.TEXT_SIZE_KEY, 0);
				prefs.edit().putInt(Constants.TEXT_SIZE_KEY, currentMultiplierFactor - 1).commit();
				refreshUI();
				break;
			}
			case R.id.settingsLargerText: {
				int currentMultiplierFactor = prefs.getInt(Constants.TEXT_SIZE_KEY, 0);
				prefs.edit().putInt(Constants.TEXT_SIZE_KEY, currentMultiplierFactor + 1).commit();
				refreshUI();
				break;
			}
		}
	}
}
