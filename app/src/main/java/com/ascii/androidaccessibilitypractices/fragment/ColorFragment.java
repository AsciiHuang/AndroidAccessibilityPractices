package com.ascii.androidaccessibilitypractices.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.ascii.androidaccessibilitypractices.Constant;
import com.ascii.androidaccessibilitypractices.R;
import com.squareup.picasso.Picasso;

public class ColorFragment extends Fragment {

	private static final String ARG_COLOR = "color";

	private int mColor;

	public static ColorFragment newInstance(int param1) {
		ColorFragment fragment = new ColorFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_COLOR, param1);
		fragment.setArguments(args);
		return fragment;
	}

	public ColorFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mColor = getArguments().getInt(ARG_COLOR);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.color_fragment, container, false);
		v.setBackgroundColor(mColor);

		return v;
	}
}