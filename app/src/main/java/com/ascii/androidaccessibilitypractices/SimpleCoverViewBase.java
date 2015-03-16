package com.ascii.androidaccessibilitypractices;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SimpleCoverViewBase extends LinearLayout {

	protected HorizontalScrollView mHScrollView;
	protected LinearLayout mSubLayout;
	protected int currentIndex = 0;

	int color[] = {
			0xFF00AED8,
			0xFF9BCD9B,
			0xFFEEE685,
			0xFFCDCD00,
			0xFFFA8072,
			0xFFC1CDCD,
			0xFFEE799F,
			0xFFCDC9A5,
			0xFFEED5B7,
			0xFFEE8262};

	public SimpleCoverViewBase(Context context) {
		super(context);
		final float scale = getResources().getDisplayMetrics().density;
		initComponent((int)(300 * scale));
	}

	public SimpleCoverViewBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		final float scale = getResources().getDisplayMetrics().density;
		initComponent((int)(300 * scale));
	}

	private void initComponent(int size) {
		setLayoutParams(new LayoutParams(size, size));
		setBackgroundColor(0xFF000000);

		mHScrollView = new HorizontalScrollView(this.getContext());
		mHScrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mHScrollView.setHorizontalFadingEdgeEnabled(false);
		mHScrollView.setHorizontalScrollBarEnabled(false);

		mSubLayout = new LinearLayout(mHScrollView.getContext());
		mSubLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mSubLayout.setBackgroundColor(0xFF000000);
		mSubLayout.setOrientation(HORIZONTAL);

		for (int i = 0; i < color.length; ++i) {
			View view = new View(mSubLayout.getContext());
			view.setLayoutParams(new LayoutParams(size, size));
			view.setBackgroundColor(color[i]);
			mSubLayout.addView(view);
		}

		mHScrollView.addView(mSubLayout);

		addView(mHScrollView);
	}
}