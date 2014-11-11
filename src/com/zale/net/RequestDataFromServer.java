package com.zale.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.zale.data.ProblemMessage;
import com.zale.data.SettingsData;
import com.zale.data.SharedData;
import com.zale.exception.RequestDataException;
import com.zale.uti.PinyinUtil;


public class RequestDataFromServer {
	
	private static final int INITDATA=0;
	private static final int NEWDATA=1;
	private static final int OLDDATA=2;
	
	private static ProblemMessage tempProblem;
	private static LinkedList<ProblemMessage> problemList;
	private static Socket clientSocket;
	
	public RequestDataFromServer()
	{
		//problemList = new LinkedList<ProblemType>();
	}
	
	
	public static LinkedList<ProblemMessage> getServerData(int type,String name,String time) throws Exception
	{
		name = PinyinUtil.getPinyinHeadChar(name);
		problemList = new LinkedList<ProblemMessage>();
		clientSocket = new Socket();
    	SocketAddress address = new InetSocketAddress(SettingsData.SERVER_IP,SettingsData.SERVER_PORT);
    	try {
    		clientSocket.connect(address, 8000);
    		Log.e("建立socket连接", "建立成功,准备接受服务器数据");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    
		BufferedWriter bufferWriter = 
				new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
		
		switch (type) {
		case INITDATA:
			bufferWriter.write(SettingsData.INITDATA+","+name+",30,2014-09-25 00:00:00*"+"\n");
			break;

		case NEWDATA:
			bufferWriter.write(SettingsData.NEWDATA+","+name+",30,"+time+"*"+"\n");
			break;
			
		case OLDDATA:
			bufferWriter.write(SettingsData.OLDDATA+","+name+",30,"+time+"*"+"\n");
			break;
			
		default:
			Log.e("Socket", "向服务器发送数据有误");
			break;
		}
		
		bufferWriter.flush();
		BufferedReader bufferReader = 
				new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String response = bufferReader.readLine();
		Log.e("ProblemList", "服务器返回的信息为"+response);
		if(response.equals("null"))
		{
			Log.e("ProblemList", "没有新的数据产生");
			return problemList;			
		}
		
		try 
		{
			Log.e("RequestDataFromServer", "开始解析response");
			
			String[] problemLine = response.split("\\*");
			
			int problemListSize = problemLine.length;
			for (int i = 0; i < problemListSize; i++) {
				//Log.e("RequestDataFromServer", "解析到的数据为"+problemLine[i].toString());
			
				try {
					String[] problemData = problemLine[i].split(",");
					tempProblem = new ProblemMessage(
							Integer.valueOf(problemData[0].substring(1, problemData[0].length())),
							problemData[1], problemData[2], problemData[3],"null");
					Log.e("RequestDataFromServer", "解析到的数据为"+ tempProblem.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				
				problemList.add(tempProblem);
			}
			
		} catch (Exception e) 
		{
			e.printStackTrace();
			new RequestDataException().toString();
		}
		
		Log.e("ProblemList", "页面上的故障信息有"+problemList.size()+"个");
		return problemList;
		
	}
	
}
