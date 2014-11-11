package com.zale.activity;


import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.zale.R;
import com.zale.data.SharedData;
import com.zale.service.background_service;
import com.zale.sqlitedatabase.SQLiteOperator;
import com.zale.view.CommuniteSelectAdapter;
import com.zale.view.ProblemListAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity{

	public static ListView commListView;
	public static CommuniteSelectAdapter adapter;
	private Button communiteButton;
	private Button exitButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		//更新社区列表和已订阅社区列表
		initCommuniteList();
		
		commListView = (ListView)findViewById(R.id.communiteSelectedList);
		adapter = new CommuniteSelectAdapter(SharedData.communiteSelected, this);
		commListView.setAdapter(adapter);
	       
		commListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Intent problemIntent = new Intent(getApplicationContext(),ProblemListActivity.class);
				String name = adapter.getItem(arg2).toString();
				SharedData.communiteSelectName = name;
				problemIntent.putExtra("communite_name", name);
				startActivity(problemIntent);			
			}
		});
	       
		
		SlidingMenu menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
    	menu.setShadowDrawable(R.drawable.shadow);
    	menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
    	menu.setFadeDegree(0.35f);
    	menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
    	menu.setMenu(R.layout.mymenu);
    	
    	
    	communiteButton = (Button)findViewById(R.id.communitelistbutton);;
    	exitButton = (Button)findViewById(R.id.exit);
    	
		communiteButton.setOnClickListener(new OnClickListener() {
					
			@Override
			public void onClick(View arg0) {
				
				Intent commIntent = new Intent(getApplicationContext(), CommuniteActivity.class);
				startActivity(commIntent);
			}
		});
		
		exitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Intent service = new Intent(getApplicationContext(), background_service.class);
				stopService(service);
				
				int pid = android.os.Process.myPid();
				android.os.Process.killProcess(pid);   //杀死当前进程
			}
		});
		
       
	}
	
	
	public void initCommuniteList()
	{
		int size = SharedData.communite.size();
		String communite_name;
		SharedPreferences in = getSharedPreferences("communite", Context.MODE_PRIVATE);
		for (int i = 0; i < size; i++) {
			communite_name = SharedData.communite.get(i);
			if (in.getInt(communite_name,0)==0) {
				SharedData.communiteListHashMap.put(communite_name,0);
			}
			else {
				SharedData.communiteSelected.add(communite_name);
				SharedData.communiteListHashMap.put(communite_name,1);
			}
		}
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
