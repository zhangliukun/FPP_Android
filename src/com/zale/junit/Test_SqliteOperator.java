//package com.zale.junit;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//
//import com.zale.data.SharedData;
//import com.zale.sqlitedatabase.SQLiteDatabaseInit;
//import com.zale.sqlitedatabase.SQLiteOperator;
//
//import junit.framework.Assert;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.test.AndroidTestCase;
//
//public class Test_SqliteOperator extends AndroidTestCase {
//	
//	private SQLiteOperator operator=null;
//	private SQLiteOpenHelper helper =null;
//	
//	public void test_insert_and_getstatebyID() throws Exception
//	{
//		this.helper = new SQLiteDatabaseInit(getContext());
//		operator = new SQLiteOperator(helper.getWritableDatabase());
//		operator.insert(1000, 0);
//		
//		operator = new SQLiteOperator(helper.getWritableDatabase());
//		Assert.assertEquals(0, operator.getstatebyID(1000));
//		
//	}
//	
//	public void test_update() throws Exception
//	{
//		this.helper = new SQLiteDatabaseInit(getContext());
//		operator = new SQLiteOperator(helper.getWritableDatabase());
//		operator.insert(1001, 0);
//		
//		operator = new SQLiteOperator(helper.getWritableDatabase());
//		operator.update(1001, 1);
//		
//		operator = new SQLiteOperator(helper.getWritableDatabase());
//		Assert.assertEquals(1, operator.getstatebyID(1001));
//		
//	}
//	
//	public void test_check_same() throws Exception
//	{
//		this.helper = new SQLiteDatabaseInit(getContext());
//		operator = new SQLiteOperator(helper.getWritableDatabase());
//		operator.insert(1003, 0);
//		
//		operator = new SQLiteOperator(helper.getWritableDatabase());
//		Assert.assertEquals(true, operator.check_same(1003));
//	}
//	
//	public void test_find_and_insert() throws Exception
//	{
//		this.helper = new SQLiteDatabaseInit(getContext());
//		operator = new SQLiteOperator(helper.getWritableDatabase());
//		operator.insert(1, "2", "3", 4, "5","6","7","8",1);
//		
//		operator = new SQLiteOperator(helper.getWritableDatabase());
//		LinkedList<String> list = (LinkedList<String>) operator.find();
//		assertEquals("1,2,3,4,5,6,7,8,1", list.get(list.size()-1).toString());
//	}
//	
//	
//	
//}
