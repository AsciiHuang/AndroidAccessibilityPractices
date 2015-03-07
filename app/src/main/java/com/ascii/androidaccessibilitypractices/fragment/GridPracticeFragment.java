package com.ascii.androidaccessibilitypractices.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ascii.androidaccessibilitypractices.DrawerToggleHandler;
import com.ascii.androidaccessibilitypractices.R;
import com.ascii.androidaccessibilitypractices.adapter.GridAdapter;

public class GridPracticeFragment extends Fragment {

	GridView gridView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_grid_practice, container, false);
		gridView = (GridView) view.findViewById(R.id.gridview);
		gridView.setAdapter(new GridAdapter(getActivity()));
		gridView.setOnItemClickListener(onGridItemClicked);
		return view;
	}

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
		((DrawerToggleHandler) activity).setActionBarTitle("Grid And Navigation");
		super.onAttach(activity);
	}

	private AdapterView.OnItemClickListener onGridItemClicked = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.container, PlaceholderFragment.newInstance(position + 1));
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		}
	};
}
