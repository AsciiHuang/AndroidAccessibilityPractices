package com.ascii.androidaccessibilitypractices.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.ListView;

import com.ascii.androidaccessibilitypractices.DrawerToggleHandler;
import com.ascii.androidaccessibilitypractices.R;
import com.ascii.androidaccessibilitypractices.adapter.DemoPagerAdapter;

import me.relex.circleindicator.CircleIndicator;

public class CustomViewPracticeFragment extends Fragment {

	ViewPager viewpager;
	CircleIndicator circleIndicator;
	Button btnChangeSampleA;
	Button btnChangeSampleB;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_customview_practice, container, false);

		initPager(view);
		btnChangeSampleA = (Button) view.findViewById(R.id.btn_change_content_a);
		btnChangeSampleA.setOnClickListener(onChangeContentAClicked);
		btnChangeSampleB = (Button) view.findViewById(R.id.btn_change_content_b);
		btnChangeSampleB.setOnClickListener(onChangeContentBClicked);
		btnChangeSampleB.setAccessibilityDelegate(buttonAccessibilityDelegate);

		return view;
	}

	private void initPager(View view) {
		viewpager = (ViewPager) view.findViewById(R.id.viewpager);
		circleIndicator = (CircleIndicator) view.findViewById(R.id.circle_indicator);
		DemoPagerAdapter defaultDemoPagerAdapter = new DemoPagerAdapter(getActivity().getSupportFragmentManager());
		viewpager.setAdapter(defaultDemoPagerAdapter);
		circleIndicator.setViewPager(viewpager);
//		circleIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//			@Override public void onPageScrolled(int i, float v, int i2) {
//			}
//
//			@Override public void onPageSelected(int i) {
//			}
//
//			@Override public void onPageScrollStateChanged(int i) {
//			}
//		});
		viewpager.setAccessibilityDelegate(pagerAccessibilityDelegate);
	}

	private View.OnClickListener onChangeContentAClicked = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			((Button) v).setText(getString(R.string.changed_button_text));
		}
	};

	private boolean mChecked = false;
	private View.OnClickListener onChangeContentBClicked = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mChecked) {
				((Button) v).setText(getString(R.string.default_button_text));
			} else {
				((Button) v).setText(getString(R.string.changed_button_text));
			}
			mChecked = !mChecked;
		}
	};

	private View.AccessibilityDelegate buttonAccessibilityDelegate = new View.AccessibilityDelegate() {
		@Override
		public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
			super.onInitializeAccessibilityEvent(host, event);
			event.setChecked(mChecked);
		}

		@Override
		public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
			super.onInitializeAccessibilityNodeInfo(host, info);
			info.setCheckable(true);
			info.setChecked(mChecked);
		}
	};

	private View.AccessibilityDelegate pagerAccessibilityDelegate = new View.AccessibilityDelegate() {
		@Override
		public void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
			Log.e("sendAccessibilityEventUnchecked", "" + event.toString());
			int eventType = event.getEventType();
			if (eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
				event.getText().add("總共 " + 5 + " 項, 目前顯示第 " + (viewpager.getCurrentItem() + 1) + " 項.");
			} else if (eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
				event.setItemCount(5);
				event.setCurrentItemIndex(viewpager.getCurrentItem());
				event.getText().add("Ascii");
			} else if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
				event.setItemCount(5);
				event.setCurrentItemIndex(viewpager.getCurrentItem());
				event.getText().add("Yuggi");
			}
			super.sendAccessibilityEventUnchecked(host, event);
		}

		@Override /* must */
		public boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
			Log.e("dispatchPopulateAccessibilityEvent", "" + event.toString());
			boolean result = super.dispatchPopulateAccessibilityEvent(host, event);
			event.getText().add("大家好");
			return result;
		}

		@Override
		public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
			Log.e("onPopulateAccessibilityEvent", "" + event.toString());
			super.onPopulateAccessibilityEvent(host, event);
			event.getText().add("你好嗎");
//			int eventType = event.getEventType();
//
//			if (eventType == AccessibilityEvent.TYPE_VIEW_SELECTED ||
//					eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
//            event.getText().add("Mode selected: " + Integer.toString(viewpager.getCurrentItem() + 1) + ".");
//				event.setItemCount(5);
//				event.setCurrentItemIndex(0);
//			}
//
//			if (eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
//				event.getText().add("total " + 5 + ", select " + viewpager.getCurrentItem() + ".");
//			}
		}

		@Override
		public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
			Log.e("onInitializeAccessibilityEvent", "" + event.toString());
			super.onInitializeAccessibilityEvent(host, event);
//			event.getText().add("大家好 12345");
			if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
				event.setItemCount(5);
				event.setCurrentItemIndex(viewpager.getCurrentItem());
				event.getText().add("初始事件" + viewpager.getCurrentItem());
			}
		}

		@Override
		public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
			Log.e("onInitializeAccessibilityNodeInfo", "" + info.toString());
			super.onInitializeAccessibilityNodeInfo(host, info);
			final AccessibilityNodeInfo.CollectionInfo collectionInfo = AccessibilityNodeInfo.CollectionInfo.obtain(
					5, 1, false, AccessibilityNodeInfo.CollectionInfo.SELECTION_MODE_SINGLE);
			info.setCollectionInfo(collectionInfo);
			info.setScrollable(true);
			info.setSelected(true);
		}

		@Override
		public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
			Log.e("onRequestSendAccessibilityEvent", "" + event.toString());
			boolean result = super.onRequestSendAccessibilityEvent(host, child, event);
			return result;
		}
	};

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onResume() {
		DrawerToggleHandler handler = (DrawerToggleHandler) getActivity();
		handler.setDrawerEnabled(true);
		super.onResume();
	}

	@Override
	public void onPause() {
		DrawerToggleHandler handler = (DrawerToggleHandler) getActivity();
		handler.setDrawerEnabled(false);
		super.onPause();
	}

	@Override
	public void onAttach(Activity activity) {
		((DrawerToggleHandler) activity).setActionBarTitle("Custom View");
		super.onAttach(activity);
	}

}
