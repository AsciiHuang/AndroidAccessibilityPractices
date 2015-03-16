package com.ascii.androidaccessibilitypractices;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

public class SimpleCoverViewB extends SimpleCoverViewBase {

	public SimpleCoverViewB(Context context) {
		super(context);
		mHScrollView.setAccessibilityDelegate(accessibilityDelegate);
	}

	public SimpleCoverViewB(Context context, AttributeSet attrs) {
		super(context, attrs);
		mHScrollView.setAccessibilityDelegate(accessibilityDelegate);
	}

	private AccessibilityDelegate accessibilityDelegate = new AccessibilityDelegate() {
		@Override
		public void sendAccessibilityEvent(View host, int eventType) {
			if (eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
				currentIndex = mHScrollView.getScrollX() / 900;
			}
			super.sendAccessibilityEvent(host, eventType);
		}

		@Override
		public boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
			if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
				event.getText().add("總共 " + 10 + " 項, 目前顯示第 " + (currentIndex + 1) + " 項.");
			}
			return super.dispatchPopulateAccessibilityEvent(host, event);
		}
	};
}
