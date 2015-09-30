package com.dbg.gameplay;

import com.dbg.samplegame.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AboutFragment extends Fragment {
	
	
	

	View view;
	
	
	Activity activity;

	String URL="http://fongchang.wix.com/discoverapptribute";
	RelativeLayout relContainer;
	TextView textViewVersion;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		activity=getActivity();
		view = inflater.inflate(R.layout.activity_about, container, false);
	
		relContainer=(RelativeLayout)view.findViewById(R.id.relContainer);
		textViewVersion=(TextView)view.findViewById(R.id.textViewVersion);
		try {
			String versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
			
			textViewVersion.setText("Ver:- "+versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		relContainer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
			    startActivity(myIntent);
				
			}
		});
		return view;
	}
	


    
	
	
	

}
