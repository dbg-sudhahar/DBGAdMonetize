package com.dbg.manager;

import com.dbg.gameplay.ProfileActivity;
import com.dbg.samplegame.R;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class MenuManager{
	
	
	
	 private Activity mActivity;
	 private Intent i;
	 
	 int PROFILE_ID=0;
	
	
	 //private boolean isTransfer=false;
	    public MenuManager(Activity activity) {
	        mActivity = activity;
	        
	      
	      
	       
	    }

	  
	    
	    public boolean onPrepareOptionsMenuConfig(Menu menu) {
	    	menu.add(Menu.NONE, PROFILE_ID, Menu.NONE, "Profile").setIcon(SelectorManager.getButtonDrawableByScreenCathegory(mActivity, R.drawable.ic_people, R.drawable.ic_photos)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	    
	   
	        return true;
	    }
	    
	    

	    public boolean onOptionsItemSelected(MenuItem item) {

	    	
	    	if(item.getItemId()==PROFILE_ID){
	    		i = new Intent(mActivity, ProfileActivity.class);
	    		mActivity.startActivity(i);
	    		return true;
	    	}
	    	
	    	return false;
	    	
	    }
	    	
	    	

		
	    
	    
	    
	    
	    

}
	    
	    

