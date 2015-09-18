package com.dbg.adapter;

import java.util.ArrayList;

import com.dbg.samplegame.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<DummyModel> {

	Context context;

	

	public ListAdapter(Context context, int resource,
			ArrayList<DummyModel> objects) {
		super(context, resource, objects);

		this.context = context;

	}

	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		final DummyModel rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_view, null);
			holder = new ViewHolder();

			


			holder.textViewPrice = (TextView) convertView
					.findViewById(R.id.textViewPrice);

			holder.textViewDesc = (TextView) convertView
					.findViewById(R.id.textViewDesc);
			
			
			

			convertView.setTag(holder);
		} else

			holder = (ViewHolder) convertView.getTag();

			
			holder.textViewPrice.setText(rowItem.getPrice());
			

			holder.textViewDesc.setText(rowItem.getDescription());
			
		return convertView;
	}

	/* private view holder class */
	

	


	
	private class ViewHolder {


		TextView textViewPrice;
		TextView textViewDesc;
	}
}
