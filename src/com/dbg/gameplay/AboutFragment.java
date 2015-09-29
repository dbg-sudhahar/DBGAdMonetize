package com.dbg.gameplay;

import com.dbg.samplegame.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutFragment extends Fragment {
	
	
	

	View view;
	
	
	Activity activity;

	String URL="http://fongchang.wix.com/discoverapptribute";
	ImageView imageView1;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		activity=getActivity();
		view = inflater.inflate(R.layout.activity_about, container, false);
	
		imageView1=(ImageView)view.findViewById(R.id.imageView1);
		
		
		imageView1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
			    startActivity(myIntent);
				
			}
		});
		return view;
	}
	


    
	
	
	

}
