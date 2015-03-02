package com.ascii.androidaccessibilitypractices.object;

import java.util.ArrayList;

public class BadListItem {

	public String title;
	public String subTitle;

	public BadListItem(String title, String subTitle) {
		this.title = title;
		this.subTitle = subTitle;
	}

	public static ArrayList<BadListItem> getMockData() {
		ArrayList<BadListItem> items = new ArrayList<BadListItem>();

		items.add(new BadListItem("Item 0", "Item Description 0"));
		items.add(new BadListItem("項目壹", "項目描述壹"));
		items.add(new BadListItem("Item 2", "Item Description 2"));
		items.add(new BadListItem("項目參", "項目描述參"));
		items.add(new BadListItem("Item 4", "Item Description 4"));
		items.add(new BadListItem("項目伍", "項目描述伍"));
		items.add(new BadListItem("Item 6", "Item Description 6"));
		items.add(new BadListItem("項目柒", "項目描述柒"));
		items.add(new BadListItem("Item 8", "Item Description 8"));
		items.add(new BadListItem("項目玖", "項目描述玖"));
		items.add(new BadListItem("Item 10", "Item Description 10"));

		return items;
	}

}
