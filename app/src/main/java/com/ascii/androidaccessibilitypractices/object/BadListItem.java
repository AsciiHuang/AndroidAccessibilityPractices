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
		items.add(new BadListItem("項目拾壹", "項目描述拾壹"));
		items.add(new BadListItem("Item 12", "Item Description 12"));
		items.add(new BadListItem("項目拾參", "項目描述拾參"));
		items.add(new BadListItem("Item 14", "Item Description 14"));
		items.add(new BadListItem("項目拾伍", "項目描述拾伍"));
		items.add(new BadListItem("Item 16", "Item Description 16"));
		items.add(new BadListItem("項目拾柒", "項目描述拾柒"));
		items.add(new BadListItem("Item 18", "Item Description 18"));
		items.add(new BadListItem("項目拾玖", "項目描述拾玖"));
		items.add(new BadListItem("Item 20", "Item Description 20"));

		return items;
	}

}
