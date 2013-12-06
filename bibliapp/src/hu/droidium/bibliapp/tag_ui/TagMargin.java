package hu.droidium.bibliapp.tag_ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TagMargin extends View {

	private int width;
	private int height;
	private int x;
	private int y;

	private List<Paint> paints = new ArrayList<Paint>();

	public TagMargin(Context context) {
		super(context);
	}

	public TagMargin(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TagMargin(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setColors(ArrayList<String> colors) {
		paints.clear();
		for (String color : colors) {
			Paint paint = new Paint();
			paint.setColor(Color.parseColor(color));
			paints.add(paint);
		}
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (paints.size() > 0) {
			float offset = 0;
			float increment = ((float)height) / paints.size();
			for (Paint paint : paints) {
				canvas.drawRect(x, y + offset, x + width, y + offset + increment, paint);
				offset += increment;
			}
		}
	}
	

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// Account for padding
		this.width = w - (getPaddingLeft() + getPaddingRight());
		this.height = h - (getPaddingTop() + getPaddingBottom());
		this.x = getPaddingLeft();
		this.y = getPaddingTop();
	}
}
