package com.ascii.androidaccessibilitypractices;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SimpleAccessibilityCustomView extends View {

	private class FocusPosType {
		public static final int LIFT_TOP = 0;
		public static final int LIFT_BOTTOM = 1;
		public static final int RIGHT_TOP = 2;
		public static final int RIGHT_BOTTOM = 3;
	}

	public SimpleAccessibilityCustomView(Context context) {
		super(context);
	}

	public SimpleAccessibilityCustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private int prePos = -1;

	private int getBlock(double x, double y) {
		int res = -1;
		if (x < 300) {
			if (y < 300) {
				res = 0;
			} else {
				res = 1;
			}
		} else {
			if (y < 300) {
				res = 2;
			} else {
				res = 3;
			}
		}
		return res;
	}

	@Override
	public boolean onHoverEvent(MotionEvent event) {
		int currentPos = getBlock(event.getX(), event.getY());
		if (currentPos != prePos) {
			prePos = currentPos;
			if (currentPos == 0) {
				this.announceForAccessibility("左上方");
			} else if (currentPos == 1) {
				this.announceForAccessibility("左下方");
			} else if (currentPos == 2) {
				this.announceForAccessibility("右上方");
			} else if (currentPos == 3) {
				this.announceForAccessibility("右下方");
			}
		}
		return super.onHoverEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawARGB(255, 0, 174, 216);
		super.onDraw(canvas);
	}

}
