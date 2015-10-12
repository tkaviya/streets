package net.blaklizt.streets.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.common.ServerCommunication;
import net.blaklizt.streets.android.common.StreetsCommon;
import org.json.JSONObject;

/**
 * User: tkaviya
 * Date: 9/23/14
 * Time: 7:50 AM
 */
public class Login extends Activity implements View.OnClickListener
{
	private static final String TAG = StreetsCommon.getTag(Login.class);

	private static Login login = null;

	private EditText username = null;
	private EditText password = null;

	private Button loginBtn;
	int counter = 5;

	private class LoginTask extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog progressDialog;

		@Override
		protected Void doInBackground(Void... param)
		{
			Log.i(TAG, "Authenticating " + username.getText().toString() + " with password " + password.getText().toString());

//			ServerCommunication.sendServerRequest("action=Login&channel=" + StreetsCommon.CHANNEL + "&username=" + username.getText().toString() + "&password=" + password.getText().toString());
			String loginResponse = "{response_code:1, response_message:\"success\"}";

			if (loginResponse == null)
			{
				runOnUiThread(new Runnable() {
					@Override public void run() {
						Toast.makeText(getInstance(), "Login Failed. Check Internet Connection.", Toast.LENGTH_SHORT).show();
					}
				});
				return null;
			}

			try
			{
				JSONObject responseJSON = new JSONObject(loginResponse);

				if (responseJSON.getInt("response_code") == 1)//ResponseCode.SUCCESS.getValue())
				{
					Log.i(TAG, "Login successful");
					runOnUiThread(new Runnable() { @Override public void run() { Toast.makeText(getInstance(), "Login successful", Toast.LENGTH_SHORT).show(); } });
					Intent mainActivity = new Intent(getInstance(), Streets.class);
					startActivity(mainActivity);
					StreetsCommon.registerStreetsActivity(Streets.getInstance());
				}
				else if (responseJSON.getInt("response_code") < 0)
				{
					Log.i(TAG, "Login failed with internal error: " + responseJSON.getString("response_message"));
					runOnUiThread(new Runnable() { @Override public void run() { Toast.makeText(getInstance(), "Login Failed. An unknown error occurred on the server.", Toast.LENGTH_SHORT).show(); } });
				}
				else
				{
					final String loginResponseStr = responseJSON.getString("response_message");
					Log.i(TAG, "Login failed: " + responseJSON.getString("response_message"));
					runOnUiThread(new Runnable() { @Override public void run() { Toast.makeText(getInstance(), loginResponseStr, Toast.LENGTH_SHORT).show(); } });

					if (--counter <= 0)
					{
						runOnUiThread(new Runnable() {
							@Override public void run() {
								username.setEnabled(false);
								password.setEnabled(false);
								loginBtn.setEnabled(false);
								Toast.makeText(getInstance(), "Maximum login attempts. Please contact support", Toast.LENGTH_LONG).show();
							}
						});
					}
				}
			}
			catch (Exception e)
			{
				Log.e(TAG, "Login failed: " + e.getMessage(), e);
				e.printStackTrace();
				runOnUiThread(new Runnable() { @Override public void run() { Toast.makeText(getInstance(), "Login Failed. An unknown error occurred on the server.", Toast.LENGTH_SHORT).show(); } });
			}
			return null;
		}


		@Override
		protected void onPreExecute()
		{
			progressDialog = ProgressDialog.show(getInstance(), "Authenticating", "Authenticating...", true, false);
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Void... progress) { }

		@Override
		protected void onPostExecute(Void result)
		{
			progressDialog.hide();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try
		{
			super.onCreate(savedInstanceState);
			login = this;
			setContentView(R.layout.login_layout);
			username = (EditText) findViewById(R.id.loginEmail);
			password = (EditText) findViewById(R.id.loginPassword);
			loginBtn = (Button) findViewById(R.id.btnLogin);
			loginBtn.setOnClickListener(this);

			findViewById(R.id.btnLinkToRegisterScreen).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent registerActivity = new Intent(Startup.getStartup(), Register.class);
					startActivity(registerActivity);
					StreetsCommon.registerStreetsActivity(Register.getInstance());
				}
			});
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to create login dialogue: " + ex.getMessage(), ex);
			runOnUiThread(new Runnable() {
				@Override public void run() {
					Toast.makeText(getApplication(), "Failed to create login dialogue! An error occurred.",
					Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	public static Login getInstance() { return login; }

	@Override
	public void onClick(View view)
	{
		new LoginTask().execute();
	}
}