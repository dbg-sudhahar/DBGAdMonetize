package com.dbg.gameplay;

import com.dbg.samplegame.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends Fragment {
	
	
	

	View view;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		
		view = inflater.inflate(R.layout.activity_about, container, false);
        
		return view;
	}
	


	
	
	
	

}
