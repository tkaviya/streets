package net.blaklizt.streets.android;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * User: tkaviya
 * Date: 9/23/14
 * Time: 10:53 PM
 */
public class ServerCommunication
{
	private static final String TAG = Streets.TAG + "_" + ServerCommunication.class.getSimpleName();

//	private static final String SERVER_ADDRESS = "http://streets.blaklizt.net/streets_controller.php";
	private static final String SERVER_ADDRESS = "http://192.168.43.8/streets_controller.php";

	private static HttpURLConnection connection = null;

	private static String communicationResult = null;

	private static HttpURLConnection getConnection()
	{
		Log.i(TAG, "Initializing socket communication to server.");

		try
		{
			URL url = new URL(SERVER_ADDRESS);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setUseCaches (false);
		}
		catch (Exception ex)
		{
			Log.e(TAG, "Failed to connect to server: " + ex.getMessage(), ex);
			ex.printStackTrace();
			return null;
		}
		return connection;
	}

	public static String sendServerRequest(String postParams)
	{
		try
		{
			connection = getConnection();

			Log.i(TAG, "Sending request of " + postParams.length() + " bytes: " + postParams);

			connection.setRequestProperty("Content-Length", String.valueOf(postParams.length()));
			DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
			writer.writeBytes(postParams);
			writer.flush();

			Log.i(TAG, "Request sent");

			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			communicationResult = "";
			while ((line = reader.readLine()) != null) { communicationResult += line; }

			Log.i(TAG, "Got response of " + communicationResult.length() + " bytes:");
			Log.i(TAG, communicationResult);

			writer.close();
			reader.close();
			return communicationResult;
		}
		catch (Exception ex)
		{
			Log.e(TAG, "Failed to get server response: " + ex.getMessage(), ex);
			ex.printStackTrace();
			return null;
		}
	}
}
