package com.zale.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zale.R;
import com.zale.data.ProblemMessage;
import com.zale.data.SharedData;
import com.zale.sqlitedatabase.SQLiteOperator;
import com.zale.uti.PinyinUtil;

public class CommuniteSelectAdapter extends BaseAdapter{
	private ArrayList<String> list;//填充数据的list
	private Context context;//上下文
	private LayoutInflater inflater=null;//用来导入布局
	private TextView communite;
	private TextView problem_num;

	public CommuniteSelectAdapter(ArrayList<String> list,Context context)//构造器
	{
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context); 
		initDate(list);//初始化数据 
	}
	

	private void initDate(ArrayList<String> list)
	{
		this.list = list;
	}
	
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;

		convertView=inflater.inflate(R.layout.communite_selected,null);//导入布局并且赋给convertview
		//holder.tv =(TextView)convertView.findViewById();//故障信息
		communite = (TextView)convertView.findViewById(R.id.communite_select);
		problem_num = (TextView)convertView.findViewById(R.id.problem_num);

		String name = list.get(position);
		communite.setText(name);
		name = PinyinUtil.getPinyinHeadChar(name);
		int count = SharedData.mytab.getUnSolvedListCountbyName(name);
		Log.e(name+"小区未解决的问题有", count+"个");
		problem_num.setText(String.valueOf(count));
		

		//int state = SharedData.mytab.getstatebyID(list.get(position).getID());
//		switch(state)
//		{
//		case 1:holder.img2.setImageResource(R.drawable.icon_question);break;
//		case 2:holder.img2.setImageResource(R.drawable.icon_process);break;
//		case 3:holder.img2.setImageResource(R.drawable.icon_correct);break;
//		case 4:holder.img2.setImageResource(R.drawable.icon_process);
//		}
		
		
//		// 设置list中TextView的显示  
//        holder.tv.setText(list.get(position));  
//        // 根据isSelected来设置checkbox的选中状况  
//        holder.cb.setChecked(getIsSelected().get(position));  
        return convertView;  
	}
}
