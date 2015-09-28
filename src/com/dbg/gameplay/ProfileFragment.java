package com.dbg.gameplay;

import java.util.ArrayList;

import com.dbg.adapter.DummyModel;
import com.dbg.adapter.ListAdapter;
import com.dbg.manager.RoundedImageView;
import com.dbg.samplegame.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProfileFragment extends Fragment implements OnClickListener{
	
	
	RelativeLayout rel1;

	RelativeLayout rel2;

	RelativeLayout rel3;
	TextView textViewCount1;
	TextView textViewCount2;
	TextView textViewCount3;
	TextView textView1;
	TextView textView2;
	TextView textView3;
	
	ListView listView;
	ListAdapter listAdapter;
	Activity activity;
	
	RoundedImageView roundedImageView;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_profile);
//		initializeUI();
//		setListener();
//		setData();
//	}

	View view;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		activity=getActivity();
		view = inflater.inflate(R.layout.activity_profile, container, false);
         initializeUI(view);
         setListener();
         setData();
		return view;
	}
	


	private void setData() {
		ArrayList<DummyModel> dummyModels=new ArrayList<DummyModel>();
		dummyModels.add(new DummyModel("$25", "Played the 2046 game and donated $25 for cancer patients"));

		dummyModels.add(new DummyModel("$23", "Played the 2046 game and donated $23 for cancer patients"));

		dummyModels.add(new DummyModel("$28", "Played the 2046 game and donated $28 for cancer patients"));

		dummyModels.add(new DummyModel("$32", "Played the 2046 game and donated $32 for cancer patients"));

		dummyModels.add(new DummyModel("$5", "Played the 2046 game and donated $5 for cancer patients"));

		dummyModels.add(new DummyModel("$8", "Played the 2046 game and donated $8 for cancer patients"));

		dummyModels.add(new DummyModel("$52", "Played the 2046 game and donated $52 for cancer patients"));

		dummyModels.add(new DummyModel("$69", "Played the 2046 game and donated $69 for cancer patients"));

		dummyModels.add(new DummyModel("$24", "Played the 2046 game and donated $24 for cancer patients"));

		dummyModels.add(new DummyModel("$5", "Played the 2046 game and donated $5 for cancer patients"));

		dummyModels.add(new DummyModel("$3", "Played the 2046 game and donated $3 for cancer patients"));

		dummyModels.add(new DummyModel("$1", "Played the 2046 game and donated $1 for cancer patients"));

		dummyModels.add(new DummyModel("$2", "Played the 2046 game and donated $2 for cancer patients"));

		dummyModels.add(new DummyModel("$19", "Played the 2046 game and donated $19 for cancer patients"));

		dummyModels.add(new DummyModel("$15", "Played the 2046 game and donated $15 for cancer patients"));

		dummyModels.add(new DummyModel("$17", "Played the 2046 game and donated $17 for cancer patients"));

		dummyModels.add(new DummyModel("$13", "Played the 2046 game and donated $13 for cancer patients"));

		dummyModels.add(new DummyModel("$15", "Played the 2046 game and donated $15 for cancer patients"));

		dummyModels.add(new DummyModel("$14", "Played the 2046 game and donated $14 for cancer patients"));

		dummyModels.add(new DummyModel("$12", "Played the 2046 game and donated $12 for cancer patients"));
		
		
		listAdapter=new ListAdapter(activity, 0, dummyModels);
		
		listView.setAdapter(listAdapter);
		
		
	}





	private void initializeUI(View view) {
		rel1=(RelativeLayout)view.findViewById(R.id.rel1);

		rel2=(RelativeLayout)view.findViewById(R.id.rel2);
		rel3=(RelativeLayout)view.findViewById(R.id.rel3);
		textView1=(TextView)view.findViewById(R.id.textView1);
		textView2=(TextView)view.findViewById(R.id.textView2);
		textView3=(TextView)view.findViewById(R.id.textView3);

		
		textViewCount1=(TextView)view.findViewById(R.id.textViewCount1);
		textViewCount2=(TextView)view.findViewById(R.id.textViewCount2);
		textViewCount3=(TextView)view.findViewById(R.id.textViewCount3);

		roundedImageView=(RoundedImageView)view.findViewById(R.id.roundedImageView);
		roundedImageView.setOval(true);
		roundedImageView.setImageResource(R.drawable.profile);
		roundedImageView.setBorderColor(Color.BLACK);
		roundedImageView.setBorderWidth(15f);
		
		listView=(ListView)view.findViewById(R.id.listView);
	}
	
	private void setListener() {
		
		rel1.setOnClickListener(this);

		rel2.setOnClickListener(this);

		rel3.setOnClickListener(this);
	}

	
	private void setButtonEffect(RelativeLayout layout,TextView textViewCount,TextView textView){
		
		
		textViewCount1.setTextColor(getResources().getColor(R.color.text_black));
		textView1.setTextColor(getResources().getColor(R.color.text_black));
		textViewCount2.setTextColor(getResources().getColor(R.color.text_black));
		textView2.setTextColor(getResources().getColor(R.color.text_black));
		textViewCount3.setTextColor(getResources().getColor(R.color.text_black));
		textView3.setTextColor(getResources().getColor(R.color.text_black));
		
		
		
		
		textViewCount.setTextColor(getResources().getColor(R.color.text_white));
		textView.setTextColor(getResources().getColor(R.color.text_white));
		
		if(layout==rel1){
			rel1.setBackgroundResource(R.drawable.buttonleftpress);
			
			rel2.setBackgroundResource(R.drawable.buttoncenter);
			rel3.setBackgroundResource(R.drawable.buttonright);
			
			
		}
		else if(layout==rel2){

			rel1.setBackgroundResource(R.drawable.buttonleft);
			
			rel2.setBackgroundResource(R.drawable.buttoncenterpress);
			
			rel3.setBackgroundResource(R.drawable.buttonright);
		}
		else if(layout==rel3){

			rel1.setBackgroundResource(R.drawable.buttonleft);
			
			rel2.setBackgroundResource(R.drawable.buttoncenter);
			
			rel3.setBackgroundResource(R.drawable.buttonrightpress);
		}
		
		
	}
	
	

	@Override
	public void onClick(View v) {
		
		
		if(rel1==v){
			setButtonEffect(rel1, textViewCount1, textView1);
		}
		else if(rel2==v){
			setButtonEffect(rel2, textViewCount2, textView2);
		}
		else if(rel3==v){
			setButtonEffect(rel3, textViewCount3, textView3);
		}
	}

}
