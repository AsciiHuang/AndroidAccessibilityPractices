package com.ascii.androidaccessibilitypractices.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ListView;

import com.ascii.androidaccessibilitypractices.DrawerToggleHandler;
import com.ascii.androidaccessibilitypractices.R;
import com.ascii.androidaccessibilitypractices.adapter.BadListAdapter;
import com.ascii.androidaccessibilitypractices.adapter.DemoPagerAdapter;
import com.ascii.androidaccessibilitypractices.object.BadListItem;

import me.relex.circleindicator.CircleIndicator;

public class BadListPracticeFragment extends Fragment {

	ViewPager viewpager;
	CircleIndicator circleIndicator;
	View header;
	ListView listView;
	Handler uiHandler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bad_list_practice, container, false);
		listView = (ListView) view.findViewById(R.id.listview);
		listView.setAdapter(new BadListAdapter(getActivity(), BadListItem.getMockData()));
		initHeader(inflater);
		listView.addHeaderView(header);
		uiHandler.postDelayed(runnable, 3000);
		return view;
	}

	@Override
	public void onDestroyView() {
		uiHandler.removeCallbacks(runnable);
		super.onDestroyView();
	}

	private void initHeader(LayoutInflater inflater) {
		header = inflater.inflate(R.layout.layout_runway, null, false);
		viewpager = (ViewPager) header.findViewById(R.id.viewpager);
		circleIndicator = (CircleIndicator) header.findViewById(R.id.circle_indicator);
		DemoPagerAdapter defaultDemoPagerAdapter = new DemoPagerAdapter(getActivity().getSupportFragmentManager());
		viewpager.setAdapter(defaultDemoPagerAdapter);
		circleIndicator.setViewPager(viewpager);
		circleIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override public void onPageScrolled(int i, float v, int i2) {

			}

			@Override public void onPageSelected(int i) {

			}

			@Override public void onPageScrollStateChanged(int i) {

			}
		});
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			int current = viewpager.getCurrentItem();
			if (current > 3) {
				viewpager.setCurrentItem(0);
			} else {
				viewpager.setCurrentItem(current + 1);
			}
			uiHandler.postDelayed(this, 3000);
		}
	};

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
		((DrawerToggleHandler) activity).setActionBarTitle("Bad List");
		super.onAttach(activity);
	}
}
