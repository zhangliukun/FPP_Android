package com.zale.thread;

import java.io.IOException;
import java.util.LinkedList;

import com.zale.activity.MainActivity;
import com.zale.data.ProblemMessage;
import com.zale.data.SharedData;
import com.zale.net.RequestDataFromServer;
import com.zale.sqlitedatabase.SQLiteOperator;
import com.zale.view.ProblemListAdapter;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class UpdateDatabaseThread implements Runnable{

	private LinkedList<ProblemMessage> problemlist;
	private ProblemMessage problem;
	private Context tempContext ;
	
	public UpdateDatabaseThread()
	{
		
	}
	
	

	@Override
	public void run() {
		
		try {
			getData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.e("insertDatabase", "开始将数据插入数据库");
		insertDatabase(problemlist);
		
		
	}
	
	public void  getData() throws IOException {
		problemlist = new LinkedList<ProblemMessage>();
		int tempSize = SharedData.communiteSelected.size();
		for (int i = 0; i < tempSize; i++) {
			String name = SharedData.communiteSelected.get(i);
			String time = SharedData.mytab.getDateByName(name);
			Log.e("相应的小区的最新的时间为", time);
			try {
				problemlist.addAll(RequestDataFromServer.getServerData(1,name,time));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Log.e("UpdateDatabase", "更新故障列表成功");
	}
	
	public void insertDatabase(LinkedList<ProblemMessage> list)
	{
		Log.e("problemlistsize", "共获得"+list.size()+"个故障");
		for(int i=0;i<list.size();i++)
		{
			problem = list.get(i);			
			//先检查所得数据是否已经插入过了
			boolean isSame = SharedData.mytab.check_same(problem.getID());
			//将故障信息插入数据库
			if(!isSame)
			{
				SharedData.mytab.insert(problem.getID(), problem.get_node_name(), 
						problem.getSid(),  problem.getsTime(), 1);	
			}
			else
			{
				Log.e("database", "the id of "+problem.getID()+" already exists in the database ");
			}
		}
	}

	
}


	