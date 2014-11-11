package com.zale.sqlitedatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.zale.data.ProblemMessage;
import com.zale.data.SharedData;

import android.R.bool;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SQLiteOperator {
	private static final String TABLE_NAME = "fpp";//要操作的数据表的名称
	private static final String TABLE_NAME_COMMUNITE = "community";
	public  static SQLiteDatabase db=null;	//数据库操作
	
	//构造函数
	public SQLiteOperator()
	{
		
	}
	
	public static void openDatabase()
	{
		db =  SharedData.helper.getWritableDatabase();
	}
	
	public static void closeDatabase()
	{
		db.close();
	}
	
	//插入操作
	public  void insert(int id,String node_name,String sid,String stime,int state)
	{
		openDatabase();
		String sql = "INSERT INTO " + TABLE_NAME + " (id,node_name,sid,stime,state)"
				+ " VALUES(?,?,?,?,?)";
		Object args[]=new Object[]{id,node_name,sid,stime,state};
		this.db.execSQL(sql, args);
		//this.db.close(); //如果操作数据库不关闭连接，那么这次拿到的连接是上一次没有关闭的连接，（效率稍高，、
		//适用于单个用户操作数据库的情况下）
	}

	//更新状态操作
	public void updateStateById(int id,int state)
	{
		openDatabase();
		String sql = "UPDATE " + TABLE_NAME + " SET state=? WHERE id=?";
		Object args[]=new Object[]{state,id};
		this.db.execSQL(sql, args);
		//this.db.close();
	}
	
	//更新故障结束时间
	public void updateEtimeById(int id,String etime)
	{
		openDatabase();
		String sql = "UPDATE " + TABLE_NAME + " SET etime=? WHERE id=?";
		Object args[]=new Object[]{etime,id};
		this.db.execSQL(sql, args);
		//this.db.close();
	}
	
	//删除操作,删除,数据库未增加删除操作，因为项目中未用到，如果以后要用的话可以扩展
	public void deleteById(int id)
	{
		openDatabase();
		String sql = "DELETE FROM " + TABLE_NAME +" WHERE id=?";
		Object args[]=new Object[]{id};
		this.db.execSQL(sql, args);
		//this.db.close();
	}
	
	public ProblemMessage getProblemById(int id)
	{
		openDatabase();
		ProblemMessage problem = new ProblemMessage(-1, "没有这个小区", "N", "2014-09-25 00:00:00","2014-09-25 00:00:00");	
		String sql = "SELECT * FROM " + TABLE_NAME +" where id = ?";
		String[] args=new String[]{String.valueOf(id)};
		Cursor result = this.db.rawQuery(sql, args); 	//执行查询语句
		for(result.moveToFirst();!result.isAfterLast();result.moveToNext()	)	//采用循环的方式查询数据
		{
			
			problem = new ProblemMessage(result.getInt(0),result.getString(1),
					result.getString(2),result.getString(3),result.getString(4));
		} 
		result.close();
		//this.db.close();
		return problem;
	}
	
	//查询未解决数量问题的数量
	public int  getUnSolvedListCountbyName(String name)
	{
		openDatabase();
		int count = 0;
		String sql = "SELECT count(*) FROM " + TABLE_NAME + " WHERE (state= 1 and node_name like ?)";
		String[] args=new String[]{name+"%"};
		Cursor result = this.db.rawQuery(sql, args); 	//执行查询语句
		for(result.moveToFirst();!result.isAfterLast();result.moveToNext()	)	//采用循环的方式查询数据
		{
			count = result.getInt(0);
		} 
		result.close();
		//this.db.close();
		Log.e(name,"共有"+count+"个未处理的数据" );
		return count;
	}
	
	
	//查询操作,查询表中所有的记录返回列表
	public LinkedList<String> find()
	{
		openDatabase();
		LinkedList<String> all = new LinkedList<String>();	//此时只是String
		String sql = "SELECT * FROM " + TABLE_NAME +" ORDER by stime";
		Cursor result = this.db.rawQuery(sql, null); 	//执行查询语句
		for(result.moveToFirst();!result.isAfterLast();result.moveToNext()	)	//采用循环的方式查询数据
		{
			all.add(result.getInt(0)+","+result.getString(1)+","+result.getString(2)+","+result.getString(3)+","
					+result.getString(4)+","+result.getInt(5));
		} 
		result.close();
		//this.db.close();
		return all;
	}
	
	public LinkedList<ProblemMessage> getProblemlist()
	{
		openDatabase();
		LinkedList<ProblemMessage> all = new LinkedList<ProblemMessage>();	
		String sql = "SELECT * FROM " + TABLE_NAME +" ORDER by stime desc";
		Cursor result = this.db.rawQuery(sql, null); 	//执行查询语句
		for(result.moveToFirst();!result.isAfterLast();result.moveToNext()	)	//采用循环的方式查询数据
		{
			
			ProblemMessage tempProblem = new ProblemMessage(result.getInt(0),result.getString(1),
					result.getString(2),result.getString(3),result.getString(4));
			all.add(tempProblem);
		} 
		result.close();
		//this.db.close();
		Log.e("ListView","数据库共返回"+all.size()+"个数据给listview" );
		return all;
	}
	
	//查询未解决问题
	public LinkedList<ProblemMessage> getUnknowProblemlist()
	{
		openDatabase();
		LinkedList<ProblemMessage> all = new LinkedList<ProblemMessage>();	
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE state= 1 ORDER by stime desc";
		Cursor result = this.db.rawQuery(sql, null); 	//执行查询语句
		for(result.moveToFirst();!result.isAfterLast();result.moveToNext()	)	//采用循环的方式查询数据
		{
			
			ProblemMessage tempProblem = new ProblemMessage(result.getInt(0),result.getString(1),
					result.getString(2),result.getString(3),result.getString(4));
			all.add(tempProblem);
		} 
		result.close();
		//this.db.close();
		Log.e("ListView","数据库共返回"+all.size()+"个数据给listview" );
		return all;
	}
	
	//查询正在处理的问题
	public LinkedList<ProblemMessage> getProcessingProblemlist()
	{
		openDatabase();
		LinkedList<ProblemMessage> all = new LinkedList<ProblemMessage>();	
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE state= 2 or State =4 ORDER by stime desc";
		Cursor result = this.db.rawQuery(sql, null); 	//执行查询语句
		for(result.moveToFirst();!result.isAfterLast();result.moveToNext()	)	//采用循环的方式查询数据
		{
			
			ProblemMessage tempProblem = new ProblemMessage(result.getInt(0),result.getString(1),
					result.getString(2),result.getString(3),result.getString(4));
			all.add(tempProblem);
		} 
		result.close();
		//this.db.close();
		Log.e("ListView","数据库共返回"+all.size()+"个数据给listview" );
		return all;
	}
	
	//查询以及处理完成的数据
	public LinkedList<ProblemMessage> getResolvedProblemlist()
	{
		openDatabase();
		LinkedList<ProblemMessage> all = new LinkedList<ProblemMessage>();	
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE state= 3 ORDER by stime desc";
		Cursor result = this.db.rawQuery(sql, null); 	//执行查询语句
		for(result.moveToFirst();!result.isAfterLast();result.moveToNext()	)	//采用循环的方式查询数据
		{
			
			ProblemMessage tempProblem = new ProblemMessage(result.getInt(0),result.getString(1),
					result.getString(2),result.getString(3),result.getString(4));
			all.add(tempProblem);
		} 
		result.close();
		//this.db.close();
		Log.e("ListView","数据库共返回"+all.size()+"个数据给listview" );
		return all;
	}
	
	//更具状态查询故障数据
	public LinkedList<ProblemMessage> getProblemListByState(int state)
	{
		openDatabase();
		LinkedList<ProblemMessage> all = new LinkedList<ProblemMessage>();	
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE state= 4 ORDER by stime desc";
		Cursor result = this.db.rawQuery(sql, null); 	//执行查询语句
		for(result.moveToFirst();!result.isAfterLast();result.moveToNext()	)	//采用循环的方式查询数据
		{
			
			ProblemMessage tempProblem = new ProblemMessage(result.getInt(0),result.getString(1),
					result.getString(2),result.getString(3),result.getString(4));
			all.add(tempProblem);
		} 
		result.close();
		//this.db.close();
		Log.e("ListView","数据库共返回"+all.size()+"个未发送给服务器的数据给listview" );
		return all;
	}
	
	
	//查询操作虫重载函数，返回指定ID的列表
	public int getstatebyID(int id)
	{
		openDatabase();
		int num=-1;//错误状态-1
		List<String> all = new ArrayList<String>();	//此时只是String
		String sql = "SELECT state FROM " + TABLE_NAME + " where id=?" ;
		String args[] = new String[]{String.valueOf(id)};
		Cursor result = this.db.rawQuery(sql, args);
		for(result.moveToFirst();!result.isAfterLast();result.moveToNext()	)
		{
			num=result.getInt(0);
		}
		
		Log.d("database", "图片状态state"+ String.valueOf(num));
		result.close();
		//this.db.close();
		return num;
	}
	
	//查询对应小区的最新的故障的最新的事件
	public String getDateByName(String name)
	{
		openDatabase();
		String time = "";
		String sql="SELECT stime from " + TABLE_NAME + " where(node_name like ? and id =(select max(id) from " 
				+ TABLE_NAME + "))";
		String args[] = new String[]{name+"%"};
		Cursor result = this.db.rawQuery(sql, args);
		for(result.moveToFirst();!result.isAfterLast();result.moveToNext()	)
		{
			time=result.getString(0);
		}
		result.close();
		//this.db.close();
		return time;
	}
	
	//查询本地数据库中最新的20条数据
	public LinkedList<ProblemMessage> getLocalNewestDataByName(String name)
	{
		openDatabase();
		LinkedList<ProblemMessage> all = new LinkedList<ProblemMessage>();	
		String sql="SELECT * from " + TABLE_NAME + " where(node_name like ? and "
				+ "id <= (select max(id) from "+TABLE_NAME+")) order by stime desc limit 0,20";
		String args[] = new String[]{name+"%"};
		Cursor result = this.db.rawQuery(sql, args);
		for(result.moveToFirst();!result.isAfterLast();result.moveToNext()	)
		{
			ProblemMessage tempProblem = new ProblemMessage(result.getInt(0),result.getString(1),
					result.getString(2),result.getString(3),result.getString(4));
			all.add(tempProblem);
		}
		result.close();
		//this.db.close();
		return all;
	}
	
	//查询对应小区的相对于输入时间以前的20条数据
	public LinkedList<ProblemMessage> getLocalOldDataByTime(int id,String name)
	{
		openDatabase();
		LinkedList<ProblemMessage> all = new LinkedList<ProblemMessage>();	
		String sql="SELECT * from " + TABLE_NAME + " where(node_name like ? and id < ?)"
				+ " order by stime desc limit 0,20";
		String args[] = new String[]{name+"%",String.valueOf(id)};
		Cursor result = this.db.rawQuery(sql, args);
		for(result.moveToFirst();!result.isAfterLast();result.moveToNext()	)
		{
			ProblemMessage tempProblem = new ProblemMessage(result.getInt(0),result.getString(1),
					result.getString(2),result.getString(3),result.getString(4));
			all.add(tempProblem);
		}
		result.close();
		//this.db.close();
		return all;
	}
	
	//判断插入数据的ID是否已经存在数据库中。
	public boolean check_same(int id)
	{
		openDatabase();
		String sql="SELECT id from " + TABLE_NAME + " where id = ?";
		String args[] =new String[]{String.valueOf(id)};
		Cursor result=this.db.rawQuery(sql,args);
		Log.d("database", "the sql has been excuate");
		
		Log.d("database","the hang count" + String.valueOf(result.getCount()));
		
		if(result.getCount()==0)//判断得到的返回数据是否为空
		{
			Log.d("database", "return false and not exist the same result" + String.valueOf(result.getCount()));
			result.close();
			//this.db.close();
			return false;
		}
		else
		{
			Log.d("database", "return true and exist the same result"+ String.valueOf(result.getCount()));
			result.close();	
			//this.db.close();
			return true;
		}
	}
	
	
	/**
	 * 操作社区名字表
	 */
	
	public void insert_community_name(String name)
	{
		openDatabase();
		if (!check_same_name(name)) {
			String sql = "INSERT INTO " + TABLE_NAME_COMMUNITE + " (community)"
					+ " VALUES(?)";
			String args[]=new String[]{name};
			this.db.execSQL(sql, args);
		}
		
		
	}
	
	public LinkedList<String> getCommunityName()
	{
		openDatabase();
		LinkedList<String> all = new LinkedList<String>();	//此时只是String
		String sql = "SELECT * FROM " + TABLE_NAME_COMMUNITE;
		Cursor result = this.db.rawQuery(sql, null); 	//执行查询语句
		
		for(result.moveToFirst();!result.isAfterLast();result.moveToNext()	)	//采用循环的方式查询数据
		{
			all.add(result.getString(0));
		} 
		
		
		result.close();
		//this.db.close();
		return all;
	}
	
	//防止重复插入name
	public boolean check_same_name(String name)
	{
		openDatabase();
		String sql="SELECT community from " + TABLE_NAME_COMMUNITE + " where community = ?";
		String args[] =new String[]{name};
		Cursor result=this.db.rawQuery(sql,args);
		Log.d("database", "the sql has been excuate");
		
		Log.d("database","the hang count" + String.valueOf(result.getCount()));
		
		if(result.getCount()==0)//判断得到的返回数据是否为空
		{
			Log.d("database", "return false and not exist the same result" + String.valueOf(result.getCount()));
			result.close();
			//this.db.close();
			return false;
		}
		else
		{
			Log.d("database", "return true and exist the same result"+ String.valueOf(result.getCount()));
			result.close();	
			//this.db.close();
			return true;
		}
	}
}
