package com.ascii.androidaccessibilitypractices;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class SimpleCoverViewA extends SimpleCoverViewBase {

	public SimpleCoverViewA(Context context) {
		super(context);
		mHScrollView.setAccessibilityDelegate(accessibilityDelegate);
	}

	public SimpleCoverViewA(Context context, AttributeSet attrs) {
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
			return super.dispatchPopulateAccessibilityEvent(host, event);
		}

		@Override
		public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
			Log.e("onPopulateAccessibilityEvent", event.toString());
			if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
				event.getText().add("向左或向右滑動可選擇不同圖片");
			}
			super.onPopulateAccessibilityEvent(host, event);
		}

		@Override
		public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
			event.setScrollable(true);
			event.setItemCount(10);
			event.setFromIndex(currentIndex);
			super.onInitializeAccessibilityEvent(host, event);
		}

		@Override
		public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
			info.setScrollable(true);
			super.onInitializeAccessibilityNodeInfo(host, info);
		}

		@Override
		public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
			return super.onRequestSendAccessibilityEvent(host, child, event);
		}
	};
}
