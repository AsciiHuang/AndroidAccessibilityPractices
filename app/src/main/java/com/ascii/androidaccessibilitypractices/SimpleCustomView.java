package com.ascii.androidaccessibilitypractices;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class SimpleCustomView extends View {

	public SimpleCustomView(Context context) {
		super(context);
	}

	public SimpleCustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawARGB(255, 154, 205, 50);
		super.onDraw(canvas);
	}
}
