package com.zale.uti;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static String getNowDate()
	{
		 Date currentTime = new Date();
		 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 String dateString = formatter.format(currentTime);
		 return dateString;
	}
}
