//package com.zale.view;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import zale.fpp_android.R;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.CheckBox;
//import android.widget.TextView;
//
//public class Adapter_De_listView extends BaseAdapter{
//	private ArrayList<String> list;//填充数据的list
//	private static HashMap<Integer,Boolean> isSelected;//用来控制checkBox的选中情况
//	private Context context;//上下文
//	private LayoutInflater inflater=null;//用来导入布局
//	
//	public Adapter_De_listView(ArrayList<String> list,Context context)//构造器
//	{
//		this.context = context;
//		this.list = list;
//		inflater = LayoutInflater.from(context); 
//		isSelected = new HashMap<Integer, Boolean>(); 
//		initDate();//初始化数据 
//	}
//	
//	//初始化选择判断为false
//	private void initDate()
//	{
//		for(int i=0;i<list.size();i++)
//		{
//			isSelected.put(i, false);
//		}
//	}
//	
//	@Override
//	public int getCount() {
//		// TODO Auto-generated method stub
//		return list.size();
//	}
//
//	@Override
//	public Object getItem(int position) {
//		// TODO Auto-generated method stub
//		return list.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		// TODO Auto-generated method stub
//		return position;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		// TODO Auto-generated method stub
//		ViewHolder holder = null;
//		if(convertView==null)
//		{
//			holder = new ViewHolder(); 
//			convertView=inflater.inflate(R.layout.problem_item,null);//导入布局并且赋给convertview
//			holder.tv =(TextView)convertView.findViewById(R.id.item_tv);//故障信息
//			holder.cb =(CheckBox)convertView.findViewById(R.id.item_cb);//勾选框
//			convertView.setTag(holder);
//		}
//		else
//		{
//			holder = (ViewHolder) convertView.getTag(); 
//		}
//		// 设置list中TextView的显示  
//        holder.tv.setText(list.get(position));  
//        // 根据isSelected来设置checkbox的选中状况  
//        holder.cb.setChecked(getIsSelected().get(position));  
//        return convertView;  
//	}
//	
//	public static HashMap<Integer,Boolean> getIsSelected() {  
//        return isSelected;  
//    }  
//	
//	public static void setIsSelected(HashMap<Integer,Boolean> isSelected) {  
//		Adapter_De_listView.isSelected = isSelected;  
//    }  
//}
