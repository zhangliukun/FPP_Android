package com.zale.view;

import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


public class ViewHolder {  
	public ImageView img1 = null;
	public ImageView img2 = null;
	public TextView tv1 = null;
	public TextView tv2 = null;
	public TextView tv3 = null;
	public TextView tv4 = null;
	public TextView tv5 = null;
	public TextView tv6 = null;
	public TextView tv = null;  
	public CheckBox cb = null;  
	
	@SuppressWarnings("unchecked")
	public static <T extends View> T getView(View convertView, int id) {
		
		SparseArray<View> holder = (SparseArray<View>) convertView.getTag();
		if( holder == null ) {
			holder = new SparseArray<View>();
			convertView.setTag(holder);
		}
		
		View view = holder.get(id);
		if( view == null ) {
			view = convertView.findViewById(id);
			holder.put(id, view);
		}
		return (T)view;
	}
	
}  
