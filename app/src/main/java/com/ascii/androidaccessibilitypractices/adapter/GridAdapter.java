package com.ascii.androidaccessibilitypractices.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.ascii.androidaccessibilitypractices.Constant;
import com.squareup.picasso.Picasso;

public class GridAdapter extends BaseAdapter {

	private Context context;

	public GridAdapter(Context context) {
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;

		if (convertView == null) {
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
		} else {
			imageView = (ImageView) convertView;
		}
		Picasso.with(context).load(Constant.ImageURLs[position]).into(imageView);

		return imageView;
	}

	@Override
	public int getCount() {
		return Constant.ImageURLs.length;
	}

	@Override
	public Object getItem(int position) {
		return Constant.ImageURLs[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}