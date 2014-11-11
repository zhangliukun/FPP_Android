package com.zale.service;

import java.io.IOException;
import java.util.LinkedList;

import android.util.Log;

import com.zale.data.ProblemMessage;
import com.zale.data.SettingsData;
import com.zale.data.SharedData;
import com.zale.net.RequestDataFromServer;
import com.zale.thread.ProblemSolvedThread;
import com.zale.thread.UpdateDatabaseThread;

public class ProblemListManager {
	
	private static String message;
	private static int id;
	private static String currentDate;
	private static ProblemMessage tempProblem;
	private static LinkedList<ProblemMessage> new_problems_list;
	private static LinkedList<ProblemMessage> offline_problems_list;
	
	public static boolean isRefresh() throws IOException {
		new_problems_list = new LinkedList<ProblemMessage>();
		Log.e("isRefresh", "检测是否刷新！");
		int communiteSelectedSize = SharedData.communiteSelected.size();
		for (int i = 0; i < communiteSelectedSize; i++) {
			String name = SharedData.communiteSelected.get(i);
			String time = SharedData.mytab.getDateByName(name);
			Log.e("相应的小区的最新的时间为", time);
			try {
				new_problems_list.addAll(RequestDataFromServer.
						getServerData(1,name,time));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (new_problems_list.size()>0) {
			new UpdateDatabaseThread().insertDatabase(new_problems_list);
			Log.e("isRefresh", "后台服务检测到有新故障产生！");
			return true;
		}
		else {
			Log.e("isRefresh", "后台服务没有检测到新故障产生！");
			return false;
		}
	}
	
	public static void sendOfflineProblem()
	{
		offline_problems_list = new LinkedList<ProblemMessage>();
		offline_problems_list = SharedData.mytab.getProblemListByState(SettingsData.UNSENDTOSERVER);
		
		int problem_size = offline_problems_list.size();
		if (problem_size != 0) {
			for (int i = 0; i < problem_size; i++) 
			{
				tempProblem = offline_problems_list.get(i);
				id = tempProblem.getID();
				currentDate = tempProblem.getEtime();
				message = SettingsData.PROBLEM_SOLVED+","+id+","+currentDate+"*";
				new Thread(new ProblemSolvedThread(id,message,SharedData.tempHandler)).start();
			}
		}
	}
}
