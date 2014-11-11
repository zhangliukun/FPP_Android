/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.zale.activity;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import junit.framework.Test;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.zale.R;
import com.zale.data.ProblemMessage;
import com.zale.data.SharedData;
import com.zale.net.RequestDataFromServer;
import com.zale.sqlitedatabase.SQLiteOperator;
import com.zale.thread.UpdateDatabaseThread;
import com.zale.uti.PinyinUtil;
import com.zale.view.ProblemListAdapter;

public final class ProblemListActivity extends ListActivity {

	static final int MENU_MANUAL_REFRESH = 0;
	static final int MENU_DISABLE_SCROLL = 1;
	static final int MENU_SET_MODE = 2;
	static final int MENU_DEMO = 3;

	private ProblemMessage oldProblem;
	private LinkedList<ProblemMessage> problemList;
	private LinkedList<ProblemMessage> tempProblem;
	private PullToRefreshListView mPullRefreshListView;
	public static ProblemListAdapter mAdapter;
	public static ListView actualListView;
	private String name;
	private static int chosed_Id;//选择删除的Id
	private static int position;//选择删除的故障在list中的位置

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//自定义的titlebar的固定格式
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_problem_list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebtn);
		
		Log.e("Problemlist", "开始创建Problemlist");
		
		Intent nameData = getIntent();
		name = nameData.getStringExtra("communite_name");
		
		final TextView title_communite = (TextView)findViewById(R.id.title_communite);
		//final Button title_btnButton = (Button)findViewById(R.id.titlebtn);
		title_communite.setText(name);
//		title_btnButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				Toast.makeText(getApplicationContext(), "筛选信息", Toast.LENGTH_SHORT).show();
//			}
//		});
		
		name = PinyinUtil.getPinyinHeadChar(name);    
		
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				new GetNewDataTask().execute();
			}
		});

		// Add an end-of-list listener
		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				Toast.makeText(ProblemListActivity.this, "从服务器拉取旧的数据...!", Toast.LENGTH_SHORT).show();
				new GetOldDataTask().execute();
			}
		});

		actualListView = mPullRefreshListView.getRefreshableView();

		// Need to use the Actual ListView when registering for Context Menu
		registerForContextMenu(actualListView);

//		mListItems = new LinkedList<String>();
//		mListItems.addAll(Arrays.asList(mStrings));
		
		problemList = new LinkedList<ProblemMessage>();
		tempProblem = new LinkedList<ProblemMessage>();
		//SharedData.mytab.insert(1, "szdx001", "F", "2014-09-25 00:00:00",1);
		
		new InitDataTask().execute();

		
		//problemList.add(new ProblemMessage(1, "百度小区", "F", "2014-09-25 00:00:00",null));
		mAdapter = new ProblemListAdapter(problemList, this);

		
		
		/**
		 * Add Sound Event Listener
		 */
//		SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(this);
//		soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
//		soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
//		soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
//		mPullRefreshListView.setOnPullEventListener(soundListener);

		// You can also just use setListAdapter(mAdapter) or
		// mPullRefreshListView.setAdapter(mAdapter)
		actualListView.setAdapter(mAdapter);
		
		actualListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.e("你点击了problem中的第",String.valueOf(arg2)+"个" );
				SharedData.problemSelectId = ((ProblemMessage)(mAdapter.getItem(arg2-1))).getID();
				
				Intent detailIntent = new Intent(getApplicationContext(), DetailsActivity.class);
				startActivity(detailIntent);
			}
		});
			
		
		actualListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				return false;
			}
		});
	}

	
	public void popup(View v)
	{
		PopupMenu popupMenu = new PopupMenu(this, v);
		MenuInflater inflater = popupMenu.getMenuInflater();
		inflater.inflate(R.menu.popmenu, popupMenu.getMenu());
		popupMenu.show();
	}
	
	private class InitDataTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// Simulates a background job.
			if (SharedData.isDataBaseOpen == false) 
			{
				
				SharedData.isDataBaseOpen = true;
				try 
				{
					try 
					{
						Log.e("ProblemListActivity", "开始执行初始化操作");
						tempProblem = RequestDataFromServer.getServerData(0,name,"2014-09-25 00:00:00");
					} catch (Exception e) {					
						e.printStackTrace();
					}
					
					if(tempProblem.size()!=0)
					{
						new UpdateDatabaseThread().insertDatabase(tempProblem);
					}
					
					
					Log.e("没有拉到最新数据", "开始拉取本地数据");
					tempProblem = SharedData.mytab.getLocalNewestDataByName(name);
					Log.e("本地数据大小", String.valueOf(tempProblem.size()));
					
					
					
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			return "success";
		}

		@Override
		protected void onPostExecute(String result) {
			//mListItems.addFirst("Added after refresh...");
			//adapter里面的数据更新操作要和notify方法放在一起，否则会报数据不一致错误
			problemList.addAll(tempProblem);
			Log.e("ProblemListActivity", "接受到的数据有"+String.valueOf(tempProblem.size()));
			
			mAdapter.notifyDataSetChanged();
			SharedData.isDataBaseOpen = false;
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}
	
	private class GetNewDataTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			
			String result = "more";
			// Simulates a background job.
			if(SharedData.isDataBaseOpen == false)
			{
				SharedData.isDataBaseOpen = true;
				try {
					try {
						String time = SharedData.mytab.getDateByName(name);
						Log.e("相应的小区的最新的时间为", time);
						tempProblem = RequestDataFromServer.getServerData(1,name,time);
						if(tempProblem.size() == 0 )
						{
							result = "none";
						}
						else {
							new UpdateDatabaseThread().insertDatabase(tempProblem);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			//mListItems.addFirst("Added after refresh...");
			
			if (result.equals("none")) {
				Toast.makeText(getApplicationContext(), "没有新的数据了", Toast.LENGTH_SHORT).show();;
			}
			else if (result.equals("more")) {
				Toast.makeText(getApplicationContext(), "拉取完成", Toast.LENGTH_SHORT).show();;
			}
			
			problemList.addAll(0,tempProblem);
			Log.e("页面的数据大小为", problemList.size() + "个");
			mAdapter.notifyDataSetChanged();
			SharedData.isDataBaseOpen = false;
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}

	
	private class GetOldDataTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// Simulates a background job.
			
			String result = "more";
			
			if(SharedData.isDataBaseOpen == false)
			{
				SharedData.isDataBaseOpen = true;
				try {
					try {
						//String time = problemList.getLast().getsTime();
						
						Thread.sleep(2000);
						
						oldProblem = problemList.getLast();
						String oldTime = oldProblem.getsTime();
						int id = oldProblem.getID();
						Log.e("相应的小区的显示的最旧的时间为", oldTime);
						tempProblem = SharedData.mytab.getLocalOldDataByTime(id, oldTime);
						if (tempProblem.size() == 0) {
							result = "none";
							tempProblem = RequestDataFromServer.getServerData(2,name,oldTime);
							if (tempProblem.size() != 0) {
								new UpdateDatabaseThread().insertDatabase(tempProblem);
								tempProblem = SharedData.mytab.getLocalOldDataByTime(id, oldTime);
							}
							
						}
										
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			//mListItems.addFirst("Added after refresh...");
			if (result.equals("none")) {
				Toast.makeText(getApplicationContext(), "没有更多的数据了", Toast.LENGTH_SHORT).show();;
			}
			else if (result.equals("more")) {
				Toast.makeText(getApplicationContext(), "拉取完成", Toast.LENGTH_SHORT).show();;
			}
			
			problemList.addAll(problemList.size(), tempProblem);	
			mAdapter.notifyDataSetChanged();
			SharedData.isDataBaseOpen = false;
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}

	
	
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(0, MENU_MANUAL_REFRESH, 0, "Manual Refresh");
//		menu.add(0, MENU_DISABLE_SCROLL, 1,
//				mPullRefreshListView.isScrollingWhileRefreshingEnabled() ? "Disable Scrolling while Refreshing"
//						: "Enable Scrolling while Refreshing");
//		menu.add(0, MENU_SET_MODE, 0, mPullRefreshListView.getMode() == Mode.BOTH ? "Change to MODE_PULL_DOWN"
//				: "Change to MODE_PULL_BOTH");
//		menu.add(0, MENU_DEMO, 0, "Demo");
//		return super.onCreateOptionsMenu(menu);
//	}

	/* 每次长按ContextMenu被绑定的View的子控件，都会调用此方法*/  
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

//		menu.setHeaderTitle("Item: " + getListView().getItemAtPosition(info.position));
//		menu.add("删除");
		position = info.position;
		chosed_Id = ((ProblemMessage)(getListView().getItemAtPosition(info.position))).getID();
		getMenuInflater().inflate(R.menu.listviewcontextmenu, menu);  

		super.onCreateContextMenu(menu, v, menuInfo);
	}
	//在弹出的菜单中进行操作
	public boolean onContextItemSelected(MenuItem item)  
    {  
		switch (item.getItemId()) {
		case R.id.delete_problem:
			//当按下删除键以后更新数据库并且将主页中的小区的故障数更新
			int id = SharedData.mytab.getstatebyID(chosed_Id);
			if (id == 3) {
				SharedData.mytab.deleteById(chosed_Id);
				problemList.remove(position-1);
				mAdapter.notifyDataSetChanged();
				MainActivity.commListView.setAdapter(MainActivity.adapter);
			}
			else if (id == 1) {
				Toast.makeText(getApplicationContext(), "故障尚未解决，无法删除", Toast.LENGTH_SHORT).show();;
			}
			else if (id == 4) {
				Toast.makeText(getApplicationContext(), "等待服务器更新状态，请稍后删除", Toast.LENGTH_SHORT).show();;
			}
			
			break;

		default:
			break;
		}
		
        Log.e("onCreateContextMenu", "选中的列表数为"+position);  
        return true;          
    }  

//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		MenuItem disableItem = menu.findItem(MENU_DISABLE_SCROLL);
//		disableItem
//				.setTitle(mPullRefreshListView.isScrollingWhileRefreshingEnabled() ? "Disable Scrolling while Refreshing"
//						: "Enable Scrolling while Refreshing");
//
//		MenuItem setModeItem = menu.findItem(MENU_SET_MODE);
//		setModeItem.setTitle(mPullRefreshListView.getMode() == Mode.BOTH ? "Change to MODE_FROM_START"
//				: "Change to MODE_PULL_BOTH");
//
//		return super.onPrepareOptionsMenu(menu);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//
//		switch (item.getItemId()) {
//			case MENU_MANUAL_REFRESH:
//				new GetNewDataTask().execute();
//				mPullRefreshListView.setRefreshing(false);
//				break;
//			case MENU_DISABLE_SCROLL:
//				mPullRefreshListView.setScrollingWhileRefreshingEnabled(!mPullRefreshListView
//						.isScrollingWhileRefreshingEnabled());
//				break;
//			case MENU_SET_MODE:
//				mPullRefreshListView.setMode(mPullRefreshListView.getMode() == Mode.BOTH ? Mode.PULL_FROM_START
//						: Mode.BOTH);
//				break;
//			case MENU_DEMO:
//				mPullRefreshListView.demo();
//				break;
//		}
//
//		return super.onOptionsItemSelected(item);
//	}


//	@Override
//	public boolean onMenuItemClick(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.menu.delete:
//			
//			break;
//
//		default:
//			break;
//		}
//		return false;
//	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (SQLiteOperator.db!=null) {
			SQLiteOperator.closeDatabase();
		}
	}
	
}
