package com.zale.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.zale.R;
import com.zale.data.ProblemMessage;
import com.zale.data.SharedData;
import com.zale.sqlitedatabase.SQLiteDatabaseInit;
import com.zale.sqlitedatabase.SQLiteOperator;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ProblemListAdapter extends BaseAdapter {

	public static LinkedList<ProblemMessage> list;//填充数据的list
	private static HashMap<Integer,Boolean> isSelected;//用来控制checkBox的选中情况
	private Context context;//上下文
	private LayoutInflater inflater=null;//用来导入布局
	private double DownX,UpX;
	
	public ProblemListAdapter(LinkedList<ProblemMessage> list,Context context)//构造器
	{
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context); 
		initDate(list);//初始化数据 
	}
	

	private void initDate(LinkedList<ProblemMessage> list)
	{
		this.list = list;
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		
		if(convertView==null)
		{
			
			holder = new ViewHolder(); 
			convertView=inflater.inflate(R.layout.layout_listitem,null);//导入布局并且赋给convertview
			

			
			//holder.tv =(TextView)convertView.findViewById();//故障信息
			holder.img1 =(ImageView) convertView.findViewById(R.id.imageView1);
			holder.img2 =(ImageView) convertView.findViewById(R.id.imageView2);
			holder.tv1 =(TextView) convertView.findViewById(R.id.content_ID);
			holder.tv2 =(TextView) convertView.findViewById(R.id.content_model);
			holder.tv4 =(TextView) convertView.findViewById(R.id.content_date);
			holder.tv5 =(TextView) convertView.findViewById(R.id.textView2);
			holder.tv6 =(TextView) convertView.findViewById(R.id.textView3);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag(); 
		}
		
		ProblemMessage tempProblem = list.get(position);
		holder.tv4.setText(tempProblem.getsTime());
		holder.tv2.setText(tempProblem.getSid());
		holder.tv1.setText(tempProblem.get_node_name());
		
		//判断故障解决图片的状态
		int state = SharedData.mytab.getstatebyID(list.get(position).getID());
		switch(state)
		{
		//1代表未解决，3和4代表已经解决，4代表还没有发送至服务器更新
		case 1:holder.img2.setImageResource(R.drawable.question);break;
		case 3:holder.img2.setImageResource(R.drawable.fix);break;
		case 4:holder.img2.setImageResource(R.drawable.fix);break;
		}
		
		
//		// 设置list中TextView的显示  
//        holder.tv.setText(list.get(position));  
//        // 根据isSelected来设置checkbox的选中状况  
//        holder.cb.setChecked(getIsSelected().get(position));  
        return convertView;  
	}

}
