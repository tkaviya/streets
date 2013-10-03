package net.blaklizt.streets.common.utilities;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/12/13
 * Time: 10:11 PM
 */
public class ReferenceGenerator extends Thread {
	public void run()
	{
		System.out.println(Gen());
	}

	public static synchronized String GenTrails()
	{
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Calendar cl2=Calendar.getInstance();
		return String.valueOf(cl2.getTimeInMillis());
	}

	public static synchronized long GenMills()
	{
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Calendar cl2=Calendar.getInstance();
		return cl2.getTimeInMillis();
	}

	public static synchronized String Gen()
	{
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Calendar cl2=Calendar.getInstance();
		String time=String.valueOf(cl2.getTimeInMillis());
		String resultString="";
		for(int i=0;i<time.length()-3;i++)
		{
			int tmp=Integer.parseInt(time.substring(i, i+3));
			int firstletter=(tmp/32)+65;
			int secondletter=(tmp%32)+65;
			resultString=resultString+((firstletter>90)?(char)(firstletter-42):(char)firstletter)+((secondletter>90)?(char)(secondletter-42):(char)secondletter);
			i=i+2;
		}
		return resultString+time.substring(time.length()-1);
	}

}
