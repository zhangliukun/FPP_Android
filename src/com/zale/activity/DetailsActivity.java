package com.zale.activity;


import com.zale.R;
import com.zale.data.ProblemMessage;
import com.zale.data.SettingsData;
import com.zale.data.SharedData;
import com.zale.net.SocketToServer;
import com.zale.sqlitedatabase.SQLiteDatabaseInit;
import com.zale.sqlitedatabase.SQLiteOperator;
import com.zale.thread.ProblemSolvedThread;
import com.zale.uti.DateUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

@SuppressLint("NewApi")
public class DetailsActivity extends Activity{
	
	private int id;
	private ProblemMessage problem;
	private Button infoButton;
	private Button queryButton;
	private PopupMenu pop = null; //一个pop的菜单
	
	public  static final int NETERROR = 1;
	
	public  Handler myHandler = new Handler(){
		
		public void handleMessage (Message msg)
		{
			switch (msg.what) {
			case NETERROR:
				Toast.makeText(getApplicationContext(),
						"你的网络出现问题，无法连接服务器，请稍后操作",Toast.LENGTH_SHORT).show();
				//网络出现错误，将故障信息的状态置为4表示未发送至服务器
				if (SharedData.mytab.getstatebyID(id) == 3) {
					SharedData.mytab.updateStateById(id, 4);
				}
				
				break;

			default:
				break;
			}
		}
	};
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_details);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebtn);
		
		final TextView title_communite = (TextView)findViewById(R.id.title_communite);
		//final Button title_btnButton = (Button)findViewById(R.id.titlebtn);
		title_communite.setText("");
		
		
//		setContentView(R.layout.activity_details);
		TextView detailsTextView = (TextView)findViewById(R.id.details_TV);
		Button backButton = (Button)findViewById(R.id.back_BTN);
		queryButton=(Button)findViewById(R.id.query_BTN);
		
		//其他的线程要用到这个Handler
		SharedData.tempHandler = myHandler;
		
		id = SharedData.problemSelectId;
		problem = SharedData.mytab.getProblemById(id);
		
		//判断确定操作可不可见以及能否点击
		int state = SharedData.mytab.getstatebyID(id);
		Log.e("the state", "test" + state +"故障的ID为"+id);
		switch(state)
		{
		case 1:{
				queryButton.setText("未解决");break;}
		case 3:{
				queryButton.setText("已解决");break;}
		case 4:{
				queryButton.setText("已解决");break;}

		default:{queryButton.setText("状态错误");break;}
		
		}
		
		
		//将故障信息等显示
		detailsTextView.setText(problem.toShowString());
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View _v) {
				finish();
			}
		});
	}

//	protected void onActivityResult(int requestCodef,int resultCode,Intent data)
//	{
//		
//		if(data==null)
//		{
//			Log.e("onActivityResult", "the data is null and the code is "+ String.valueOf(requestCodef)+" and "
//					+ String.valueOf(resultCode));
//			return;
//		}
//		
//		Bundle b=data.getBundleExtra("newbd");
//		int value = b.getInt("flag");
//		Log.e("onActivityResult", "return the data and the data is " + String.valueOf(value));
//		
//		//得到返回值后再进行按钮的设置,
//		switch(value)
//		{
//		case 0:queryButton.setClickable(false);break;
//		case 1:queryButton.setClickable(true);queryButton.setVisibility(View.VISIBLE);break;
//		default:queryButton.setClickable(false);
//		}
//		Log.e("the button value", String.valueOf(value));
//		
//	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.details, menu);
		return true;
	}
	
	public void onpopupmenu(View button)
	{
		pop = new PopupMenu(this,button);
		pop.getMenuInflater().inflate(R.menu.popmenu,pop.getMenu());
		
		
		pop.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			@Override
			public boolean onMenuItemClick(MenuItem item) {
//				Toast.makeText(getApplicationContext(), 
//						"你点击了菜单",
//						Toast.LENGTH_LONG).show();
				
				switch(item.getItemId())
				{
				case R.id.solved:
					{
						//测试数据库,将点击确定后的数据的状态 yi改为3，表示图片完成
						Log.e("database", "the id is "+ id);
						//获得当前的时间，并且插入数据库
						String currentDate = DateUtil.getNowDate();
						SharedData.mytab.updateEtimeById(id, currentDate);
						//发送给服务器解决的时间
						String message = SettingsData.PROBLEM_SOLVED+","+id+","+currentDate+"*";
						
						
						//这里如果断线服务器没有收到的话图标不要改为勾而改为警告标识，下一次网络正常的话再将有警告标识
						//的数据发送给服务器
						
						if(SharedData.mytab.getstatebyID(id)!=3&&SharedData.mytab.getstatebyID(id)!=4)
						{
							SharedData.mytab.updateStateById(id,3);
							ProblemListActivity.actualListView.setAdapter(ProblemListActivity.mAdapter);
						}
						
						new Thread(new ProblemSolvedThread(id,message,myHandler)).start();
						
						finish();
						break;
					}
				case R.id.unsolved:
					{
						Log.e("database", "the id is "+ id);
						//获得当前的时间，并且插入数据库
						String currentDate = null;
						SharedData.mytab.updateEtimeById(id, currentDate);
						//发送给服务器解决的时间
						String message = SettingsData.PROBLEM_SOLVED+","+id+","+currentDate+"*";
						
						
						if(SharedData.mytab.getstatebyID(id)!=1)
						{
							SharedData.mytab.updateStateById(id,1);
							ProblemListActivity.actualListView.setAdapter(ProblemListActivity.mAdapter);
						}
						
						new Thread(new ProblemSolvedThread(id,message,myHandler)).start();
						//点击确定后将ID传回mainactivity
						finish();
						break;
					}
				}
				Log.e("button", "the popMenu button");
				return false;
			}
			
		});
		pop.show();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (SQLiteOperator.db!=null) {
			SQLiteOperator.closeDatabase();
		}
	}
}
