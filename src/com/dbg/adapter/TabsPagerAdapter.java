package com.dbg.adapter;

import com.dbg.gameplay.AboutFragment;
import com.dbg.gameplay.DonateNowFragment;
import com.dbg.gameplay.PlayFragmant;
import com.dbg.gameplay.ProfileFragment;
import com.dbg.gameplay.SettingFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	
	 private String[] tabsName = { "Play", "DashBoard", "Donate Now","Setting","About" };
	 
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            // Top Rated fragment activity
            return new PlayFragmant();
        case 1:
        	 return new ProfileFragment();
        case 2:
        	 return new DonateNowFragment();
        case 3:
       	 return new SettingFragment();
        case 4:
          	 return new AboutFragment();
        }
 
        return null;
    }
    
	@Override
	public CharSequence getPageTitle(int position) {
		return tabsName[position];
	}
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }
 
}
