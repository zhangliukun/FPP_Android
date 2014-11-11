package com.zale.thread;

import android.os.Handler;
import android.util.Log;

import com.zale.activity.DetailsActivity;
import com.zale.data.SharedData;
import com.zale.net.SocketToServer;

public class ProblemSolvedThread implements Runnable {

	private String message;
	private Handler handler;
	private int id;
	
	public ProblemSolvedThread(int id,String message,Handler mHandler) {
		this.message = message;
		this.handler = mHandler;
		this.id = id;
	}
	
	@Override
	public void run() {
		String response = "";
		try {
			response = SocketToServer.getDataByProtocol(this.message);
			Log.e("SocketToServerThread","服务器返回的结果为"+response);
			if (response.equals("#1*") ) {
				Log.e("SocketToServerThread", "服务器操作成功");
				SharedData.mytab.updateStateById(id, 3);
			}
			else if (response.equals("#0*") ) {
				Log.e("SocketToServerThread", "服务器操作失败");
				SharedData.mytab.updateStateById(id, 4);
			}
			else {
				Log.e("SocketToServerThread", "发送数据失败");
				SharedData.mytab.updateStateById(id, 4);
			}
		} catch (Exception e) {
			handler.sendEmptyMessage(DetailsActivity.NETERROR);
			
			e.printStackTrace();
		}
		
	}

}
