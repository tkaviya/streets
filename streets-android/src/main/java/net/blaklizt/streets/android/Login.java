package net.blaklizt.streets.android;

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
import net.blaklizt.streets.common.ResponseCode;
import org.json.JSONObject;

/**
 * User: tkaviya
 * Date: 9/23/14
 * Time: 7:50 AM
 */
public class Login extends Activity implements View.OnClickListener
{
	private static final String TAG = Streets.TAG + "_" + Login.class.getSimpleName();

	private static Login login;

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

			String loginResponse = ServerCommunication.sendServerRequest("action=Login&channel=SMARTPHONE&username=" + username.getText().toString() + "&password=" + password.getText().toString());

			if (loginResponse == null)
			{
				Toast.makeText(getLogin(), "Login Failed. Check Internet Connection.", Toast.LENGTH_SHORT).show();
				return null;
			}

			try
			{
				JSONObject responseJSON = new JSONObject(loginResponse);

				if (responseJSON.getInt("response_code") == ResponseCode.SUCCESS.getValue())
				{
					Log.i(TAG, "Login successful");
					runOnUiThread(new Runnable() { @Override public void run() { Toast.makeText(getLogin(), "Login successful", Toast.LENGTH_SHORT).show(); } });
					Intent mainActivity = new Intent(getLogin(), Streets.class);
					startActivity(mainActivity);
				}
				else if (responseJSON.getInt("response_code") < 0)
				{
					Log.i(TAG, "Login failed with internal error: " + responseJSON.getString("response_message"));
					runOnUiThread(new Runnable() { @Override public void run() { Toast.makeText(getLogin(), "Login Failed. An unknown error occurred on the server.", Toast.LENGTH_SHORT).show(); } });
				}
				else
				{
					final String loginResponseStr = responseJSON.getString("response_message");
					Log.i(TAG, "Login failed: " + responseJSON.getString("response_message"));
					runOnUiThread(new Runnable() { @Override public void run() { Toast.makeText(getLogin(), loginResponseStr, Toast.LENGTH_SHORT).show(); } });

					if (--counter <= 0)
					{
						runOnUiThread(new Runnable() {
							@Override public void run() {
								username.setEnabled(false);
								password.setEnabled(false);
								loginBtn.setEnabled(false);
								Toast.makeText(getLogin(), "Maximum login attempts. Please contact support", Toast.LENGTH_LONG).show();
							}
						});
					}
				}
			}
			catch (Exception e)
			{
				Log.e(TAG, "Login failed: " + e.getMessage(), e);
				e.printStackTrace();
				runOnUiThread(new Runnable() { @Override public void run() { Toast.makeText(getLogin(), "Login Failed. An unknown error occurred on the server.", Toast.LENGTH_SHORT).show(); } });
			}
			return null;
		}


		@Override
		protected void onPreExecute()
		{
			progressDialog = ProgressDialog.show(getLogin(), "Authenticating", "Authenticating...", true, false);
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
		super.onCreate(savedInstanceState);
		login = this;
		setContentView(R.layout.login_layout);
		username = (EditText)findViewById(R.id.loginEmail);
		password = (EditText)findViewById(R.id.loginPassword);
		loginBtn = (Button)findViewById(R.id.btnLogin);
		loginBtn.setOnClickListener(this);

		findViewById(R.id.btnLinkToRegisterScreen).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent registerActivity = new Intent(getLogin(), Register.class);
				startActivity(registerActivity);
			}
		});
	}

	private static Login getLogin() { return login; }

	@Override
	public void onClick(View view)
	{
		new LoginTask().execute();
	}
}