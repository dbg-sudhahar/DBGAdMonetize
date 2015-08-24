package com.dbg.application;

import com.dbg.constants.IAppConstants;
import com.parse.Parse;

import android.app.Application;

public class GameApplication extends Application{
	
	public void onCreate() {
		 super.onCreate();
		 
		 Parse.initialize(this, IAppConstants.PARSE_APP_ID, IAppConstants.PARSE_CLIENT_KEY);
		
		
	};

}
