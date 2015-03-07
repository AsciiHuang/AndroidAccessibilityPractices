package com.ascii.androidaccessibilitypractices.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ascii.androidaccessibilitypractices.DrawerToggleHandler;
import com.ascii.androidaccessibilitypractices.MainActivity;
import com.ascii.androidaccessibilitypractices.R;

public class PlaceholderFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static PlaceholderFragment newInstance(int sectionNumber) {
		PlaceholderFragment fragment = new PlaceholderFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public PlaceholderFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		ActionBarActivity activity = (ActionBarActivity) getActivity();
		activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		activity.getSupportActionBar().setIcon(R.drawable.icon_shape_transparent);
		return rootView;
	}

	@Override
	public void onResume() {
		DrawerToggleHandler handler = (DrawerToggleHandler) getActivity();
		handler.setDrawerEnabled(false);
		super.onResume();
	}

	@Override
	public void onAttach(Activity activity) {
		((DrawerToggleHandler) activity).setActionBarTitle("Place");
		super.onAttach(activity);
	}
}
