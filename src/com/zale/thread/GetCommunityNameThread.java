package com.zale.thread;

import android.os.Handler;
import android.util.Log;

import com.zale.activity.StartActivity;
import com.zale.data.SettingsData;
import com.zale.data.SharedData;
import com.zale.net.SocketToServer;
import com.zale.uti.PinyinUtil;

public class GetCommunityNameThread implements Runnable{
	
	private static String dataRcecive;
	private static String[] community_name; 


	
	@Override
	public void run() {
		String message = SettingsData.COMMUNITY + "*\n";
		
		try {
			dataRcecive = SocketToServer.getDataByProtocol(message);
			Log.e("#3CommunityName", dataRcecive);
			
			dataRcecive = dataRcecive.substring(1, dataRcecive.length()-2);
			//dataRcecive ="百度小区,测试小区,哈哈小区";
			Log.e("CommunityName", dataRcecive);
			community_name = dataRcecive.split(",");
			for (int i = 0; i < community_name.length; i++) {
				Log.e("CommunityName", community_name[i]);
				try {
					PinyinUtil.getPinyin(community_name[i]);
					SharedData.mytab.insert_community_name(community_name[i]);
				} catch (Exception e) {
					Log.e("CommunityName", e.getMessage());
				}
				
				
			}
		} catch (Exception e) {             
			e.printStackTrace();
		}
		
		
	}

}
