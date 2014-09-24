package net.blaklizt.streets.android.groupon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * User: tkaviya
 * Date: 9/22/14
 * Time: 11:46 PM
 */
public class DealManager
{
	public static String getDeals()
	{
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		try {
			url = new URL("http://api.groupon.com/v2/deals/?channel_id=goods?" +
//				"client_id=c77a05aabd8b1651961fd5b48c26f4919ec4bed0&" +
				"client_id=b91d375e38147f3c1e0339a3588d0b791c190424&" +
				"show=tags,options");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
