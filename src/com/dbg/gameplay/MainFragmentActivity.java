package com.dbg.gameplay;

import com.dbg.adapter.TabsPagerAdapter;
import com.dbg.manager.NonSwipeableViewPager;
import com.dbg.manager.PagerSlidingTabStrip;
import com.dbg.samplegame.R;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;

public class MainFragmentActivity extends FragmentActivity  {
	private NonSwipeableViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    
	private int currentColor = 0xFFd3571a;
	 private PagerSlidingTabStrip tabs;
    // Tab titles
    private String[] tabsName = { "Play", "DashBoard", "Donate Now","About" };
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainfragment);
 
        // Initilization
        viewPager = (NonSwipeableViewPager) findViewById(R.id.pager);
      
        tabs=(PagerSlidingTabStrip)findViewById(R.id.tabs);
       
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
 
        viewPager.setAdapter(mAdapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        viewPager.setPageMargin(pageMargin);
        
    	tabs.setViewPager(viewPager);

		tabs.setIndicatorColor(currentColor);
        


       
    }
 
}