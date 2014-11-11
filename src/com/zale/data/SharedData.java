package com.zale.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.zale.net.SocketToServer;
import com.zale.sqlitedatabase.SQLiteOperator;
import com.zale.thread.GetCommunityNameThread;

import android.R.integer;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

public class SharedData {
	public static String communiteSelectName;
	public static int problemSelectId;
	public static int LocalDatabase_Problem_Size = 0;
	public static boolean isGetDataThreadFinished =true; 
	//数据库操作变量共享
	public static SQLiteOpenHelper helper =null;
	public static SQLiteOperator mytab=null;
	//小区数目list
	public static ArrayList<String> communite;
	//已经选择小区
	public static ArrayList<String> communiteSelected;
	//保存小区订阅状态的信息
	public static HashMap<String, Integer> communiteListHashMap;
	
	//这个handler只作参数用，不做具体传值作用
	public static Handler tempHandler = new Handler();
	
	//数据库打开或者关闭判断
	public static boolean isDataBaseOpen = false;
	public SharedData()
	{
		//每次操作数据库得打开操作
		mytab = new SQLiteOperator();
		
		communite = new ArrayList<String>();
		communiteSelected = new ArrayList<String>();
		communiteListHashMap = new HashMap<String, Integer>();
		
		communite.clear();
		communiteSelected.clear();
		
		new Thread(new GetCommunityNameThread()).start();
		
//		SharedData.mytab.insert_community_name("苏州大学");
//		SharedData.mytab.insert_community_name("百度小区");
		
		LinkedList<String> tempCommunity = new LinkedList<String>();
		tempCommunity = mytab.getCommunityName();
		int community_size = tempCommunity.size();
		for (int i = 0; i < community_size; i++) {
			communite.add(tempCommunity.get(i));
		}
		
	}
	
}
