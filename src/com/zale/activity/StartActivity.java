package com.zale.activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import com.zale.R;
import com.zale.data.SettingsData;
import com.zale.data.SharedData;
import com.zale.service.background_service;
import com.zale.sqlitedatabase.SQLiteDatabaseInit;
import com.zale.sqlitedatabase.SQLiteOperator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class StartActivity extends Activity {

	public  static final int NET_ERROR = 1;
	public  static final int ALL_OK = 2;
	public  static final int NO_ACCESS = 3;
	public static int The_Net_State=1;
	
	private String response=null;
	private Socket clientSocket;
	private SQLiteOpenHelper helper =null;
	private Intent startActivityIntent;
	private Intent settingsActivityIntent;
	private Intent startServiceIntent;
	
	@SuppressLint("HandlerLeak")
	private Handler startHandler = new Handler(){
		@Override
		public void handleMessage(Message _message){
			switch (_message.what) {
				case ALL_OK:{
					The_Net_State = ALL_OK;
					startActivity(startActivityIntent);
					startService(startServiceIntent);
					finish();
					break;
				}
				case NET_ERROR:{
					The_Net_State = NET_ERROR;
					Toast.makeText(getApplicationContext(), 
							"你的手机出现了网络问题,未能连上服务器!请检查服务器设置.",
							Toast.LENGTH_LONG).show();
					startActivity(startActivityIntent);				
					startService(startServiceIntent);
					finish();
					break;
				}
				case NO_ACCESS:{
					The_Net_State = NO_ACCESS;
//					Toast.makeText(getApplicationContext(), 
//							"你的手机没有获得查看授权!!!", 
//							Toast.LENGTH_LONG).show();
					
					
					dialog();

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					finish();
					break;
				}
			}
		}
	}; 
	
	
	private Thread getReadyThread = new Thread(new Runnable() {
		@Override
		public void run() {
			try{
				Log.e("读取的数据为:", "IP:" + 
								SettingsData.SERVER_IP);

				String Imei = ((TelephonyManager) 
						getSystemService(TELEPHONY_SERVICE)).getDeviceId();
		        Log.e(Imei, Imei);
		    	   Log.e("正在建立socket连接", ".......");
		    	//增加超时机制
				//Socket clientSocket = new Socket(SettingsData.Server_IP, 9600);
		    	clientSocket = new Socket();
		    	SocketAddress address = new InetSocketAddress(SettingsData.SERVER_IP,SettingsData.SERVER_PORT);
		    	clientSocket.connect(address, 6000);
				Log.e("建立socket连接", "建立成功");
		    
				BufferedWriter bufferWriter = 
						new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
				bufferWriter.write(SettingsData.AUTHENTICATION+","+Imei+"*"+"\n");
				bufferWriter.flush();
				Log.e("建立socket连接", "身份验证信息发送成功");
				
				BufferedReader bufferReader = 
						new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				response = bufferReader.readLine();
				Log.e(Imei, "服务器返回的信息为"+response);
			
				if(response.charAt(1)=='0'){
					Message message = new Message();
					message.what = NO_ACCESS;
					startHandler.sendMessage(message);
				}else if(response.charAt(1)=='1'){
					Message message = new Message();
					message.what = ALL_OK;
					startHandler.sendMessage(message);
				}
				else {
					Message message = new Message();
					message.what = NET_ERROR;
					startHandler.sendMessage(message);
				}
		       
			}catch (Exception e) {
				e.printStackTrace(); 
				Message message = new Message();
				message.what = NET_ERROR;
				startHandler.sendMessage(message);
			}
		}
	});
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);	
		
		//初始化共享数据
		//给定一个helper
		helper=new SQLiteDatabaseInit(getApplicationContext());
		SharedData.helper = helper;
		new SharedData();
		
		startActivityIntent = new Intent(getApplicationContext(),MainActivity.class);
		startServiceIntent = new Intent(getApplicationContext(),background_service.class);

		getReadyThread.start();
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (SQLiteOperator.db!=null) {
			SQLiteOperator.closeDatabase();
		}
	}
	
	protected  void  dialog() 
	{
		AlertDialog.Builder builder = new Builder(StartActivity.this);
		builder.setMessage("你的手机没有获得授权，即将退出");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				StartActivity.this.finish();
				}
		});
		builder.create().show();
	}
	
}
