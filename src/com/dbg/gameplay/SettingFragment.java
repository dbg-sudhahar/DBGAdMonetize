package com.dbg.gameplay;

import java.util.ArrayList;
import java.util.List;

import com.dbg.manager.AdManager;
import com.dbg.manager.AppPreferenceManager;
import com.dbg.samplegame.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingFragment extends Fragment {
	
	
	

	View view;
	
	
	Activity activity;
	AdManager adManager;
	Spinner spinnerAdSetting;
	Button buttonShowAd;
	private LinearLayout linContainer;
	RelativeLayout rl;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		activity=getActivity();
		view = inflater.inflate(R.layout.activity_setting, container, false);
		
		

		adManager = new AdManager(activity);

		
	
		buttonShowAd=(Button)view.findViewById(R.id.buttonShowAd);
		
		spinnerAdSetting=(Spinner)view.findViewById(R.id.spinnerAdSetting);
		
		 List<String> categories = new ArrayList<String>();
	      categories.add("Automobile");
	      categories.add("Business Services");
	      ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, categories);
	        
	      
	      spinnerAdSetting.setAdapter(dataAdapter);
	      
	      spinnerAdSetting.setSelection(AppPreferenceManager.getAdSettingType(activity));
		spinnerAdSetting.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				AppPreferenceManager.setAdSettingType(position, activity);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		buttonShowAd.setText("Show Ad  :-"+AppPreferenceManager.getAdCount(activity));
		buttonShowAd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				adManager.ShowAd(true);
			}
		});
		return view;
	}
	


    @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	int count =AppPreferenceManager.getAdCount(activity);
    	buttonShowAd.setText("Show Ad  :-"+AppPreferenceManager.getAdCount(activity));
    	if(count>0){
    		buttonShowAd.setEnabled(true);
    		
    	}
    	else{
    		buttonShowAd.setEnabled(false);
    	}
    }
	
	
	

}
