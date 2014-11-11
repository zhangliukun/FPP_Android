package com.zale.activity;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.SocketHandler;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zale.R;
import com.zale.data.News;
import com.zale.data.SharedData;
import com.zale.sqlitedatabase.SQLiteOperator;
import com.zale.view.AlphaAdapter;
import com.zale.view.LetterBarView;
import com.zale.view.LetterBarView.OnLetterSelectListener;
import com.zale.view.ViewHolder;


public class CommuniteActivity extends Activity{
	
	private AlphaAdapter<News> adapter;
	private ListView listView;
	final String[] CONTENTS = {"小区"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.communite);
		
		listView = (ListView) findViewById(R.id.listview);
		
		//更新community
		SharedData.communite.clear();
		LinkedList<String> tempCommunity = new LinkedList<String>();
		tempCommunity = SharedData.mytab.getCommunityName();
		int community_size = tempCommunity.size();
		for (int i = 0; i < community_size; i++) {
			SharedData.communite.add(tempCommunity.get(i));
		}
		
		//更新小区列表的是否已经订阅的信息，不然在getview时会出错。
		initCommuniteList();
		
		ArrayList<News> data = new ArrayList<News>();
		for( int i=0; i<SharedData.communite.size(); i++ ) {
			try {
				
			} catch (Exception e) {
				
			}
			data.add(new News(SharedData.communite.get(i)));
		}
		
		adapter = new AlphaAdapter<News>(this, data) {

			@Override
			public void bindOriginData(int position, View convertView, News itemData) {
				TextView titleText = ViewHolder.getView(convertView, R.id.news_title);
				titleText.setText(itemData.getTitle());
			}
		};
		
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AlphaAdapter.OnItemClickWrapperListener<News>() {

			@Override
			public void onItemClick(News itemData) {
				
				String communite_name = itemData.getTitle(); 
				Toast.makeText(CommuniteActivity.this, communite_name, Toast.LENGTH_SHORT).show();
				//持久化保存勾选信息
				SharedPreferences in = getSharedPreferences("communite", Context.MODE_PRIVATE);
				if (in.getInt(communite_name, 0)==0) {
					SharedPreferences.Editor editor = in.edit();
					editor.putInt(communite_name, 1);
					editor.commit();
					SharedData.communiteListHashMap.put(communite_name, 1);
				}
				else {
					SharedPreferences.Editor editor = in.edit();
					editor.putInt(communite_name, 0);
					editor.commit();
					SharedData.communiteListHashMap.put(communite_name, 0);
				}
				
				updateCommuniteSecected();
				listView.setAdapter(adapter);
				MainActivity.adapter.notifyDataSetChanged();
			}
			
		});
		
		LetterBarView letterBar = (LetterBarView) findViewById(R.id.letter_bar);
		letterBar.setOnLetterSelectListener(new OnLetterSelectListener() {
			
			@Override
			public void onLetterSelect(String s) {
				if(s.equalsIgnoreCase("#")) {
					listView.setSelection(0);
				} else {
					if( adapter.containsAlpha(s) ) {
						listView.setSelection( adapter.getAlphaPosition(s) );
					}
				}
			}
		});
		
	}
	
	
	//从数据库查找出新的小区后进行初始化操作
	public void initCommuniteList()
	{
		SharedData.communiteSelected.clear();
		
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
	
	
	public void updateCommuniteSecected()
	{
		SharedData.communiteSelected.clear();
		int size = SharedData.communite.size();
		for (int i = 0; i < size; i++) {
			String nameString = SharedData.communite.get(i);
			if (SharedData.communiteListHashMap.get(nameString)==1) {
				SharedData.communiteSelected.add(nameString);
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
