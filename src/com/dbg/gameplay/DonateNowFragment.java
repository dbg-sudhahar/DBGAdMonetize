package com.dbg.gameplay;

import com.dbg.samplegame.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DonateNowFragment extends Fragment{
	

	Activity activity;
	

	View view;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		activity=getActivity();
		view = inflater.inflate(R.layout.activity_donate, container, false);
        
		return view;
	}
	


}
