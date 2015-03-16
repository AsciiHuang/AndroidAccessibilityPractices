package com.ascii.androidaccessibilitypractices.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

import com.ascii.androidaccessibilitypractices.DrawerToggleHandler;
import com.ascii.androidaccessibilitypractices.R;

public class CustomViewPracticeFragment extends Fragment {

	Button btnChangeSampleA;
	Button btnChangeSampleB;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_customview_practice, container, false);

		btnChangeSampleA = (Button) view.findViewById(R.id.btn_change_content_a);
		btnChangeSampleA.setOnClickListener(onChangeContentAClicked);
		btnChangeSampleB = (Button) view.findViewById(R.id.btn_change_content_b);
		btnChangeSampleB.setOnClickListener(onChangeContentBClicked);
		btnChangeSampleB.setAccessibilityDelegate(buttonAccessibilityDelegate);

		return view;
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
