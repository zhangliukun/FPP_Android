package com.zale.sqlitedatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class SQLiteDatabaseInit extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME="fpp.db";
	private static final int  DATABASE_VERSION=2;//更改版本后数据库将重新创建
	private static final String TABLE_NAME="fpp";
	private static final String TABLE_NAME_COMMUNITE = "community";
	
	/**
     * 在SQLiteOpenHelper的子类当中，必须有这个构造函数
     * @param context     当前的Activity
     * @param name        表的名字（而不是数据库的名字，这个类是用来操作数据库的）
     * @param factory      用来在查询数据库的时候返回Cursor的子类，传空值
     * @param version      当前的数据库的版本，整数且为递增的数
     */
    public SQLiteDatabaseInit(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);//继承父类
        // TODO Auto-generated constructor stub
    }
/**
     * 该函数是在第一次创建数据库时执行，只有当其调用getreadabledatebase()
     * 或者getwrittleabledatebase()而且是第一创建数据库是才会执行该函数
     */

    public void onCreate(SQLiteDatabase db)
    {
    	//创建故障信息表
        String sql = "CREATE TABLE " + TABLE_NAME + "("
        		+ "id INTEGER,"
        		+ "node_name VARCHAR(100),"
        		+ "sid CHAR(1),"
        		+ "stime DATETIME,"
        		+ "etime DATETIME,"
        		+ "state INTEGER"
        		+ ")";
        db.execSQL(sql);
        Log.d("Create database","故障信息表创建成功");
        
        //创建社区名字表
        String sql2 = "CREATE TABLE " + TABLE_NAME_COMMUNITE + "("
        		+ "community VARCHAR(30)"
        		+ ")";
        db.execSQL(sql2);
        Log.d("Create database","故障信息表创建成功");
    }
    
/**
*数据库更新函数，当数据库更新时会执行此函数
*/

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    	String sql = "DROP TABLE IF EXISTS " + TABLE_NAME; 
    	db.execSQL(sql);
    	
    	String sql2 = "DROP TABLE IF EXISTS "+ TABLE_NAME_COMMUNITE; 
    	db.execSQL(sql2);
    	
    	this.onCreate(db);
        System.out.println("数据库已经更新");
        /**
         * 在此添加更新数据库是要执行的操作
         */
    }
  
}

