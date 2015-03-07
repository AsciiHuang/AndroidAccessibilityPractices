package com.ascii.androidaccessibilitypractices;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.ascii.androidaccessibilitypractices.fragment.BadListPracticeFragment;
import com.ascii.androidaccessibilitypractices.fragment.CustomViewPracticeFragment;
import com.ascii.androidaccessibilitypractices.fragment.GridPracticeFragment;
import com.ascii.androidaccessibilitypractices.fragment.PlaceholderFragment;

public class MainActivity extends ActionBarActivity
		implements NavigationDrawerFragment.NavigationDrawerCallbacks, DrawerToggleHandler {

	private NavigationDrawerFragment mNavigationDrawerFragment;
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();

		if (position == 0) {
			fragmentManager.beginTransaction()
					.replace(R.id.container, new BadListPracticeFragment())
					.commit();
		} else if (position == 1) {
			fragmentManager.beginTransaction()
					.replace(R.id.container, new GridPracticeFragment())
					.commit();
		} else if (position == 2) {
			fragmentManager.beginTransaction()
					.replace(R.id.container, new CustomViewPracticeFragment())
					.commit();
		} else {
			fragmentManager.beginTransaction()
					.replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
					.commit();
		}
	}

	public void setDrawerEnabled(boolean enabled) {
		mNavigationDrawerFragment.mDrawerToggle.setDrawerIndicatorEnabled(enabled);
	}

	public void setActionBarTitle(String title) {
		mTitle = title;
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
//		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public void onBackPressed() {
		int count = getFragmentManager().getBackStackEntryCount();
		if (count > 0) {
			getFragmentManager().popBackStack();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			return true;
		} else if (item.getItemId() == android.R.id.home &&
				mNavigationDrawerFragment.mDrawerToggle != null &&
				!mNavigationDrawerFragment.mDrawerToggle.isDrawerIndicatorEnabled()) {
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
