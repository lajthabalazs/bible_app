package hu.droidium.bibliapp;

import hu.droidium.flurry_base.FlurryBaseActivity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.view.View;
import android.view.ViewStub;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogBaseActivity extends FlurryBaseActivity {
	
	private ProgressDialog progressDialog;
	private Dialog dialog; 

	/* ************************ DIALOG FUNCTIONS ************************ */
	
	protected boolean showDialog(int titleId, int messageId,
			int firstButtonId, OnClickListener firstButtonListener) {
		String title = getString(titleId);
		String message = getString(messageId);
		String firstButtonText = getString(firstButtonId);
		return showDialog(-1, title, message, firstButtonText, firstButtonListener, null, null, null, null, Orientation.HORIZONTAL);
	}
	
	protected boolean showDialog(int titleId, int messageId,
			int firstButtonId, OnClickListener firstButtonListener,
			int secondButtonId, OnClickListener secondButtonListener, Orientation orientation) {
		String title = getString(titleId);
		String message = getString(messageId);
		String firstButtonText = getString(firstButtonId);
		String secondButtonText = getString(secondButtonId);
		return showDialog(-1, title, message, firstButtonText, firstButtonListener, secondButtonText, secondButtonListener, null, null, orientation);
	}
	
	protected boolean showDialog(int titleId, int messageId,
			int firstButtonId, OnClickListener firstButtonListener,
			int secondButtonId, OnClickListener secondButtonListener,
			int thirdButtonId, OnClickListener thirdButtonListener, Orientation orientation) {
		String title = getString(titleId);
		String message = getString(messageId);
		String firstButtonText = getString(firstButtonId);
		String secondButtonText = getString(secondButtonId);
		String thirdButtonText = getString(thirdButtonId);
		return showDialog(-1, title, message, firstButtonText, firstButtonListener, secondButtonText, secondButtonListener, thirdButtonText, thirdButtonListener, orientation);
	}

	protected boolean showDialog(int imageId, int titleId, int messageId,
			int firstButtonId, OnClickListener firstButtonListener) {
		String title = getString(titleId);
		String message = getString(messageId);
		String firstButtonText = getString(firstButtonId);
		return showDialog(imageId, title, message, firstButtonText, firstButtonListener, null, null, null, null, Orientation.HORIZONTAL);
	}

	protected boolean showDialog(int imageId, int titleId, int messageId,
			int firstButtonId, OnClickListener firstButtonListener,
			int secondButtonId, OnClickListener secondButtonListener, Orientation orientation) {
		String title = getString(titleId);
		String message = getString(messageId);
		String firstButtonText = getString(firstButtonId);
		String secondButtonText = getString(secondButtonId);
		return showDialog(imageId, title, message, firstButtonText, firstButtonListener, secondButtonText, secondButtonListener, null, null, orientation);
	}

	protected boolean showDialog(int imageId, int titleId, int messageId,
			int firstButtonId, OnClickListener firstButtonListener,
			int secondButtonId, OnClickListener secondButtonListener,
			int thirdButtonId, OnClickListener thirdButtonListener, Orientation orientation) {
		String title = getString(titleId);
		String message = getString(messageId);
		String firstButtonText = getString(firstButtonId);
		String secondButtonText = getString(secondButtonId);
		String thirdButtonText = getString(thirdButtonId);
		return showDialog(imageId, title, message, firstButtonText, firstButtonListener, secondButtonText, secondButtonListener, thirdButtonText, thirdButtonListener, orientation);
	}

	protected boolean showDialog(String title, String message,
			String firstButtonText, OnClickListener firstButtonListener) {
		return showDialog(-1, title, message, firstButtonText, firstButtonListener, null, null, null, null, Orientation.HORIZONTAL);
	}
	
	protected boolean showDialog(String title, String message,
			String firstButtonText, OnClickListener firstButtonListener,
			String secondButtonText, OnClickListener secondButtonListener, Orientation orientation) {
		return showDialog(-1, title, message, firstButtonText, firstButtonListener, secondButtonText, secondButtonListener, null, null, orientation);
	}

	protected boolean showDialog(String title, String message,
			String firstButtonText, OnClickListener firstButtonListener,
			String secondButtonText, OnClickListener secondButtonListener,
			String thirdButtonText, OnClickListener thirdButtonListener, Orientation orientation) {
		return showDialog(-1, title, message, firstButtonText, firstButtonListener, secondButtonText, secondButtonListener, thirdButtonText, thirdButtonListener, orientation);
	}

	/**
	 * Dialog with three buttons and an image
	 * @param imageId
	 * @param titleId
	 * @param messageId
	 * @param firstButtonId
	 * @param firstButtonListener
	 * @param secondButtonId
	 * @param secondButtonListener
	 * @param thirdButtonId
	 * @param thirdButtonListener
	 * @return
	 */
	protected boolean showDialog(int imageId, String title, String message,
			String firstButtonText, final OnClickListener firstButtonListener,
			String secondButtonText, final OnClickListener secondButtonListener,
			String thirdButtonText, final OnClickListener thirdButtonListener, Orientation orientation) {
		if (dialog != null) {
			return false;
		}
		if (progressDialog != null) {
			return false;
		}
		dialog = new Dialog(this);
		dialog.setTitle(title);
		View dialogView = getLayoutInflater().inflate(R.layout.popart_dialog, null);
		ViewStub buttonStub = (ViewStub)dialogView.findViewById(R.id.dialogButtonStub);
		if(orientation == Orientation.HORIZONTAL) {
			buttonStub.setLayoutResource(R.layout.dialog_horizontal_buttons);
		} else {
			buttonStub.setLayoutResource(R.layout.dialog_vertical_buttons);
		}
		buttonStub.inflate();

		dialog.setContentView(dialogView);
		TextView dialogMessage = (TextView) dialogView.findViewById(R.id.dialogMessage);
		if (message != null) {
			dialogMessage.setText(message);
		} else {
			dialogMessage.setVisibility(View.GONE);
		}
		Button firstButton = (Button) dialogView.findViewById(R.id.dialogFirstButton);
		if (firstButtonText != null) {
			firstButton.setText(firstButtonText);
			firstButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					dialog = null;
					if (firstButtonListener != null) {
						firstButtonListener.onClick(null);
					}
				}
				
			});
		} else {
			firstButton.setVisibility(View.GONE);
		}

		Button secondButton = (Button) dialogView.findViewById(R.id.dialogSecondButton);
		if (secondButtonText != null) {
			secondButton.setText(secondButtonText);
			secondButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					dialog = null;
					if (secondButtonListener != null) {
						secondButtonListener.onClick(null);
					}
				}
			});
		} else {
			secondButton.setVisibility(View.GONE);
		}

		Button thirdButton = (Button) dialogView.findViewById(R.id.dialogThirdButton);
		if (thirdButtonText != null) {
			thirdButton.setText(thirdButtonText);
			thirdButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					dialog = null;
					if (thirdButtonListener != null) {
						thirdButtonListener.onClick(null);
					}
				}
			});
		} else {
			thirdButton.setVisibility(View.GONE);
		}
		if (imageId != -1) {
			ImageView imageView = (ImageView) dialogView.findViewById(R.id.dialogImage);
			imageView.setImageResource(imageId);
		} else {
			View imageHolder = dialogView.findViewById(R.id.dialogImageHolder);
			imageHolder.setVisibility(View.GONE);
		}
		dialog.setCancelable(false);
		dialog.show();
		return true;
	}

	@Override
	protected String getFlurryKey() {
		return getString(R.string.flurryKey);
	}
}
