package ee.cs.ut.locdoc.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import ee.cs.ut.locdoc.FileAddFragment;
import ee.cs.ut.locdoc.FileBrowserFragment;
import ee.cs.ut.locdoc.GMapFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	GMapFragment map;

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		switch (index) {
		case 0:
			return new FileBrowserFragment(); 
		case 1:
			if (map == null)
				map = new GMapFragment();
			return map;
		case 2:
			return new FileAddFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		return 3;
	}

}
