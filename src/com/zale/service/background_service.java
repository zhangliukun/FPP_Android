package com.zale.service;


import java.util.LinkedList;

import com.zale.R;
import com.zale.activity.MainActivity;
import com.zale.data.ProblemMessage;
import com.zale.data.SharedData;
import com.zale.thread.UpdateDatabaseThread;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class background_service extends Service{
	private static final int NET_ERROR = 1;
	private static final int NEW_PROBLEM = 2;
	private static final int NOTHING = 3;
	
	private Handler serviceHandler = new Handler(){
		@Override
		public void handleMessage(Message _message){
			switch (_message.what) {
				case NET_ERROR:{
					Toast.makeText(getApplicationContext(), 
							"你的网络出现问题,防攀爬系统无法进行更新!", 
							Toast.LENGTH_LONG).show();
					_message = null;
					System.gc();
//					Intent startServiceIntent = new Intent(getApplicationContext(), background_service.class);
//					stopService(startServiceIntent);
					break;
				}
				case NEW_PROBLEM:{
					Toast.makeText(getApplicationContext(), 
							"设备出现新的故障!请打开程序进行了解详细内容.", 
							Toast.LENGTH_LONG).show();
					getNotification();
					if(SharedData.isGetDataThreadFinished)
					{
						
						Thread task = new Thread(new UpdateDatabaseThread());
						task.start();			
						SharedData.isGetDataThreadFinished = false;
					}
					else
					{
						Toast.makeText(getApplicationContext(), 
								"正在更新数据库，请等待....",Toast.LENGTH_LONG).show();
					}
					
					_message = null;
					System.gc();
					break;
				}
				case NOTHING:{
					//那就Nothing吧
					Log.v("Service", "do nothing..."); 
					_message = null;
					System.gc();
					break;
				}
			}
		}
	};
	
	private Runnable askForFreshRunnable = new Runnable() {
		@Override
		public void run() {
			
			while(true){
				
				ProblemListManager.sendOfflineProblem();
				
				Message message = new Message();
				try {
					if((ProblemListManager.isRefresh())){
						message.what = NEW_PROBLEM;
						serviceHandler.sendMessage(message);
						
					}else{
						message.what = NOTHING;
						Log.e("..", "nothing");
						serviceHandler.sendMessage(message);
					}
					Thread.sleep(1000*60*10);
				} catch (Exception e) {
					e.printStackTrace();
					message.what = NET_ERROR;
					serviceHandler.sendMessage(message);
					try {
						Thread.sleep(1000*60*10);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
	};
	private Thread askForFreshThread;
	@Override
	public IBinder onBind(Intent _intent) {
		return null;
	}
	
	@Override
	public void onCreate(){
		Log.v("Service", "onCreate");
		askForFreshThread = new Thread(askForFreshRunnable);
		askForFreshThread.start();
	}
	
	@Override
	public void onDestroy(){
		Log.v("Service", "onDestroy");
		askForFreshThread.interrupt();
		askForFreshThread = null;
		System.gc();
		Intent startServiceIntent = new Intent(getApplicationContext(), background_service.class);
		stopService(startServiceIntent);
	}
	
	private void getNotification(){
		NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);              
		Notification n = new Notification(R.drawable.warning, "出现新的故障!!!", System.currentTimeMillis());            
		n.flags = Notification.FLAG_AUTO_CANCEL;               
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);          
		//PendingIntent
		PendingIntent contentIntent = PendingIntent.getActivity(
		        getApplicationContext(),
		        R.string.app_name,
		        intent,
		        PendingIntent.FLAG_UPDATE_CURRENT);
		                 
		n.setLatestEventInfo(
				getApplicationContext(),
		        "出现新的故障!!!",
		        "进行查看.",
		        contentIntent);
		nm.notify(R.string.app_name, n);
	}
}
