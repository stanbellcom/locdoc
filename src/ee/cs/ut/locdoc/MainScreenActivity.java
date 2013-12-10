package ee.cs.ut.locdoc;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import ee.cs.ut.locdoc.adapters.TabsPagerAdapter;
import ee.cs.ut.locdoc.utils.DBUtils;

public class MainScreenActivity extends FragmentActivity implements ActionBar.TabListener {

	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;

	private DBUtils dbUtils;

	int prev;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainscreen_activity);

		// Initilization
		dbUtils = new DBUtils();
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// File browser tab
		Tab fileTab = actionBar.newTab();
		fileTab.setText(R.string.title_filebrowser);
		fileTab.setTabListener(this);
		fileTab.setIcon(R.drawable.icon_filestab);
		actionBar.addTab(fileTab);

		// Map tab
		Tab mapTab = actionBar.newTab();
		mapTab.setText(R.string.title_map);
		mapTab.setTabListener(this);
		mapTab.setIcon(R.drawable.icon_maptab);
		actionBar.addTab(mapTab);

		// Add file tab
		Tab addFileTab = actionBar.newTab();
		addFileTab.setText(R.string.title_fileadd);
		addFileTab.setTabListener(this);
		addFileTab.setIcon(R.drawable.icon_addfile);
		actionBar.addTab(addFileTab);

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_screen_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btn_logout_big:
			if (dbUtils.getAccManager(getApplicationContext()).hasLinkedAccount()) {
				dbUtils.getAccManager(getApplicationContext()).unlink();
				MainScreenActivity.this.finish();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// bug with screen becoming dark
		if (prev == 1) {
			if (tab.getPosition() == 0) {
				viewPager.setCurrentItem(2);
			} else if (tab.getPosition() == 2) {
				viewPager.setCurrentItem(0);
			}
		}

		// updating the map file list
		if (tab.getPosition() == 1) {
			GMapFragment gMapFragment = (GMapFragment) mAdapter.getItem(1);
			if (gMapFragment.googleMap != null)
				gMapFragment.populateMap();
		}

		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		prev = tab.getPosition();
	}
}