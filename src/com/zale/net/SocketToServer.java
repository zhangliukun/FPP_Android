package com.zale.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.util.Log;

import com.zale.data.SettingsData;

public class SocketToServer {

	private static Socket clientSocket;
	private static String response = "";
	
	public static String getDataByProtocol(String message) throws Exception
	{
	
		//增加超时机制
		//Socket clientSocket = new Socket(SettingsData.Server_IP, 9600);
    	clientSocket = new Socket();
    	SocketAddress address = new InetSocketAddress(SettingsData.SERVER_IP,SettingsData.SERVER_PORT);
    	clientSocket.connect(address, 6000);
		Log.e("建立socket连接"+message, "建立成功");
    
		BufferedWriter bufferWriter = 
				new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
		bufferWriter.write(message);
		bufferWriter.flush();
		Log.e("getDataByProtocol"+message, "消息发送成功");
		
		BufferedReader bufferReader = 
				new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		response = bufferReader.readLine();
		Log.e("getDataByProtocol", "服务器返回的信息为"+response);
			
		return response;
	}

}
