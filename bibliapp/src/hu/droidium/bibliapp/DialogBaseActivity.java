package hu.droidium.bibliapp;

import java.util.ArrayList;
import java.util.List;

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
	private List<DialogDescriptor> enqueuedDialogs = new ArrayList<DialogBaseActivity.DialogDescriptor>();

	/* ************************ DIALOG FUNCTIONS ************************ */
	
	protected void showDialog(int titleId, int messageId,
			int firstButtonId, OnClickListener firstButtonListener) {
		String title = getString(titleId);
		String message = getString(messageId);
		String firstButtonText = getString(firstButtonId);
		showDialog(-1, title, message, firstButtonText, firstButtonListener, null, null, null, null, Orientation.HORIZONTAL);
	}
	
	protected void showDialog(int titleId, int messageId,
			int firstButtonId, OnClickListener firstButtonListener,
			int secondButtonId, OnClickListener secondButtonListener, Orientation orientation) {
		String title = getString(titleId);
		String message = getString(messageId);
		String firstButtonText = getString(firstButtonId);
		String secondButtonText = getString(secondButtonId);
		showDialog(-1, title, message, firstButtonText, firstButtonListener, secondButtonText, secondButtonListener, null, null, orientation);
	}
	
	protected void showDialog(int titleId, int messageId,
			int firstButtonId, OnClickListener firstButtonListener,
			int secondButtonId, OnClickListener secondButtonListener,
			int thirdButtonId, OnClickListener thirdButtonListener, Orientation orientation) {
		String title = getString(titleId);
		String message = getString(messageId);
		String firstButtonText = getString(firstButtonId);
		String secondButtonText = getString(secondButtonId);
		String thirdButtonText = getString(thirdButtonId);
		showDialog(-1, title, message, firstButtonText, firstButtonListener, secondButtonText, secondButtonListener, thirdButtonText, thirdButtonListener, orientation);
	}

	protected void showDialog(int imageId, int titleId, int messageId,
			int firstButtonId, OnClickListener firstButtonListener) {
		String title = getString(titleId);
		String message = getString(messageId);
		String firstButtonText = getString(firstButtonId);
		showDialog(imageId, title, message, firstButtonText, firstButtonListener, null, null, null, null, Orientation.HORIZONTAL);
	}

	protected void showDialog(int imageId, int titleId, int messageId,
			int firstButtonId, OnClickListener firstButtonListener,
			int secondButtonId, OnClickListener secondButtonListener, Orientation orientation) {
		String title = getString(titleId);
		String message = getString(messageId);
		String firstButtonText = getString(firstButtonId);
		String secondButtonText = getString(secondButtonId);
		showDialog(imageId, title, message, firstButtonText, firstButtonListener, secondButtonText, secondButtonListener, null, null, orientation);
	}

	protected void showDialog(int imageId, int titleId, int messageId,
			int firstButtonId, OnClickListener firstButtonListener,
			int secondButtonId, OnClickListener secondButtonListener,
			int thirdButtonId, OnClickListener thirdButtonListener, Orientation orientation) {
		String title = getString(titleId);
		String message = getString(messageId);
		String firstButtonText = getString(firstButtonId);
		String secondButtonText = getString(secondButtonId);
		String thirdButtonText = getString(thirdButtonId);
		showDialog(imageId, title, message, firstButtonText, firstButtonListener, secondButtonText, secondButtonListener, thirdButtonText, thirdButtonListener, orientation);
	}

	protected void showDialog(String title, String message,
			String firstButtonText, OnClickListener firstButtonListener) {
		showDialog(-1, title, message, firstButtonText, firstButtonListener, null, null, null, null, Orientation.HORIZONTAL);
	}
	
	protected void showDialog(String title, String message,
			String firstButtonText, OnClickListener firstButtonListener,
			String secondButtonText, OnClickListener secondButtonListener, Orientation orientation) {
		showDialog(-1, title, message, firstButtonText, firstButtonListener, secondButtonText, secondButtonListener, null, null, orientation);
	}

	protected void showDialog(String title, String message,
			String firstButtonText, OnClickListener firstButtonListener,
			String secondButtonText, OnClickListener secondButtonListener,
			String thirdButtonText, OnClickListener thirdButtonListener, Orientation orientation) {
		showDialog(-1, title, message, firstButtonText, firstButtonListener, secondButtonText, secondButtonListener, thirdButtonText, thirdButtonListener, orientation);
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
	protected void showDialog(int imageId, String title, String message,
			String firstButtonText, final OnClickListener firstButtonListener,
			String secondButtonText, final OnClickListener secondButtonListener,
			String thirdButtonText, final OnClickListener thirdButtonListener, Orientation orientation) {
		if (dialog != null) {
			enqueuedDialogs.add(new DialogDescriptor(imageId, title, message, firstButtonText, firstButtonListener, secondButtonText, secondButtonListener, thirdButtonText, thirdButtonListener, orientation));
			return;
		}
		if (progressDialog != null) {
			enqueuedDialogs.add(new DialogDescriptor(imageId, title, message, firstButtonText, firstButtonListener, secondButtonText, secondButtonListener, thirdButtonText, thirdButtonListener, orientation));
			return;
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
					if (firstButtonListener != null) {
						firstButtonListener.onClick(null);
					}
					dialog = null;
					checkEnqueud();
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
					if (secondButtonListener != null) {
						secondButtonListener.onClick(null);
					}
					dialog = null;
					checkEnqueud();
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
					if (thirdButtonListener != null) {
						thirdButtonListener.onClick(null);
					}
					dialog = null;
					checkEnqueud();
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
	}

	private void checkEnqueud() {
		if(enqueuedDialogs.size() > 0) {
			DialogDescriptor descriptor = enqueuedDialogs.remove(0);
			showDialog(descriptor.imageId, descriptor.title, descriptor.message,
					descriptor.firstButtonText, descriptor.firstButtonListener,
					descriptor.secondButtonText, descriptor.secondButtonListener,
					descriptor.thirdButtonText, descriptor.thirdButtonListener, descriptor.orientation);
		}
	}

	@Override
	protected String getFlurryKey() {
		return getString(R.string.flurryKey);
	}
	
	private static class DialogDescriptor {
		int imageId;
		String title;
		String message;
		String firstButtonText;
		OnClickListener firstButtonListener;
		String secondButtonText;
		OnClickListener secondButtonListener;
		String thirdButtonText;
		OnClickListener thirdButtonListener;
		Orientation orientation;		

		public DialogDescriptor(int imageId, String title, String message,
				String firstButtonText, OnClickListener firstButtonListener,
				String secondButtonText,
				OnClickListener secondButtonListener, String thirdButtonText,
				OnClickListener thirdButtonListener, Orientation orientation) {
			this.imageId = imageId;
			this.title = title;
			this.message = message;
			this.firstButtonText = firstButtonText;
			this.firstButtonListener = firstButtonListener;
			this.secondButtonText = secondButtonText;
			this.secondButtonListener = secondButtonListener;
			this.thirdButtonText = thirdButtonText;
			this.thirdButtonListener = thirdButtonListener;
			this.orientation = orientation;
		}
	}
}
