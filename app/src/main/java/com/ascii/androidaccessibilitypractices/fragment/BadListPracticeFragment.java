package com.ascii.androidaccessibilitypractices.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ascii.androidaccessibilitypractices.R;
import com.ascii.androidaccessibilitypractices.adapter.BadListAdapter;
import com.ascii.androidaccessibilitypractices.object.BadListItem;

public class BadListPracticeFragment extends Fragment {

	ListView listView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bad_list_practice, container, false);
		listView = (ListView) view.findViewById(R.id.listview);
		listView.setAdapter(new BadListAdapter(getActivity(), BadListItem.getMockData()));
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
}
