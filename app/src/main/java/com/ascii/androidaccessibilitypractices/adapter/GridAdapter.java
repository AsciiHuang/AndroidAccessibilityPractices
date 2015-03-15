package com.ascii.androidaccessibilitypractices.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ascii.androidaccessibilitypractices.Constant;
import com.ascii.androidaccessibilitypractices.R;
import com.squareup.picasso.Picasso;

public class GridAdapter extends BaseAdapter {

	class ViewHolder {
		public ImageView image;
		public TextView title;
		public TextView subTitle;
	}

	private Context context;
	private LayoutInflater inflater;

	public GridAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.layout_grid_card, parent, false);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.subTitle = (TextView) convertView.findViewById(R.id.sub_title);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Picasso.with(context).load(Constant.ImageURLs[position]).into(viewHolder.image);
		viewHolder.title.setText(Constant.Titles[position]);
		viewHolder.subTitle.setText(Constant.SubTitles[position]);

		String strDescription = String.format("%s, %s, %s",
				Constant.Titles[position],
				context.getString(R.string.artist),
				Constant.SubTitles[position]);

		convertView.setContentDescription(strDescription);

		return convertView;
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