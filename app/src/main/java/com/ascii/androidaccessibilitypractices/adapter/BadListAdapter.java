package com.ascii.androidaccessibilitypractices.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ascii.androidaccessibilitypractices.R;
import com.ascii.androidaccessibilitypractices.object.BadListItem;

import java.util.ArrayList;
import java.util.Random;

public class BadListAdapter extends BaseAdapter {

	class ViewHolder {
		public View icon;
		public TextView title;
		public TextView subTitle;
	}

	private Context context;
	private LayoutInflater inflater;
	private Random random;
	private ArrayList<BadListItem> items = new ArrayList<BadListItem>();

	public BadListAdapter(Context context, ArrayList<BadListItem> items) {
		random = new Random();
		this.context = context;
		this.items = items;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item_bad_practice, parent, false);
			viewHolder.icon	 = convertView.findViewById(R.id.icon);
			viewHolder.title = (TextView) convertView.findViewById(R.id.title);
			viewHolder.subTitle = (TextView) convertView.findViewById(R.id.sub_title);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		BadListItem model = items.get(position);
		viewHolder.icon.setBackgroundColor(0xff000000 | random.nextInt(0x00ffffff));
		viewHolder.title.setText(model.title);
		viewHolder.subTitle.setText(model.subTitle);

		return convertView;
	}

	@Override
	public int getCount() {
		return (items == null) ? 0 : items.size();
	}

	@Override
	public BadListItem getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}