package com.ascii.androidaccessibilitypractices.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ascii.androidaccessibilitypractices.DrawerToggleHandler;
import com.ascii.androidaccessibilitypractices.R;
import com.ascii.androidaccessibilitypractices.adapter.BadListAdapter;
import com.ascii.androidaccessibilitypractices.object.BadListItem;

public class BadListPracticeFragment extends Fragment {

	ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bad_list_practice, container, false);
		listView = (ListView) view.findViewById(R.id.listview);
		listView.setAdapter(new BadListAdapter(getActivity(), BadListItem.getMockData()));
		return view;
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
		((DrawerToggleHandler) activity).setActionBarTitle("Bad List");
		super.onAttach(activity);
	}
}
