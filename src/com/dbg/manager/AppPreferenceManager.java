/**
 * 
 */
package com.dbg.manager;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * @author Kishore
 *
 */
public class AppPreferenceManager {
	
	
	private static String PREFERENCE_VALUE="DBG";
	
	
	
	
	
	public static void increaseAdCount(Context context) 
	{
		SharedPreferences preferences;
		preferences = context.getSharedPreferences(PREFERENCE_VALUE,Context.MODE_PRIVATE);
		int  count =getAdCount(context)+1;
		preferences.edit().putInt("AdCount", count).commit();
	}
	
	public static void decreaseAdCount(Context context) 
	{
		SharedPreferences preferences;
		preferences = context.getSharedPreferences(PREFERENCE_VALUE,Context.MODE_PRIVATE);
		int  count =getAdCount(context)-1;
		
		if(count>=0)
		preferences.edit().putInt("AdCount", count).commit();
		
	}
	
	public static int getAdCount(Context context) 
	{
		SharedPreferences preferences;
		preferences = context.getSharedPreferences(PREFERENCE_VALUE,Context.MODE_PRIVATE);
		int  count = preferences.getInt("AdCount", 0);
		return count;
	}
	
	
	
	
	public static int getAdSettingType(Context context) 
	{
		SharedPreferences preferences;
		preferences = context.getSharedPreferences(PREFERENCE_VALUE,Context.MODE_PRIVATE);
		int  count = preferences.getInt("AdSettingType", 0);
		return count;
	}
	
	public static void setAdSettingType(int adSettingType,Context context) 
	{
		SharedPreferences preferences;
		preferences = context.getSharedPreferences(PREFERENCE_VALUE,Context.MODE_PRIVATE);
	
		preferences.edit().putInt("AdSettingType", adSettingType).commit();
	}
	
	
	
	
}
