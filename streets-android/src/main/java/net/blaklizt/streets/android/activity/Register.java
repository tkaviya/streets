//package net.blaklizt.streets.android.activity;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.telephony.TelephonyManager;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//import net.blaklizt.streets.android.R;
//import net.blaklizt.streets.android.common.ServerCommunication;
//import net.blaklizt.streets.android.common.StreetsCommon;
//import org.json.JSONObject;
//
///**
// * User: tkaviya
// * Date: 9/23/14
// * Time: 7:50 AM
// */
//public class Register extends Activity implements View.OnClickListener
//{
//	private static final String TAG = StreetsCommon.getTag(Register.class);
//
//	private static Register register;
//
//	private EditText firstName = null;
//	private EditText lastName = null;
//	private EditText username = null;
//	private EditText msisdn = null;
//	private EditText email = null;
//	private EditText confirmPassword = null;
//	private EditText password = null;
//	private TextView registerError;
//	private Button registerBtn;
//
//	private class RegisterTask extends AsyncTask<Void, Void, Void>
//	{
//		ProgressDialog progressDialog;
//
//		@Override
//		protected Void doInBackground(Void... param)
//		{
//			Log.i(TAG, "Registering " + username.getText().toString() + " with password " + password.getText().toString());
//
//			String registerResponse = ServerCommunication.sendServerRequest(
//					"action=Register&channel=" + StreetsCommon.CHANNEL +
//							"&firstName=" + firstName.getText().toString() +
//							"&lastName=" + lastName.getText().toString() +
//							"&username=" + username.getText().toString() +
//							"&msisdn=" + msisdn.getText().toString() +
//							"&email=" + email.getText().toString() +
//							"&password=" + password.getText().toString() +
//							"&imei=" + getIMEI() + "&imsi=" + getIMSI());
//
//			if (registerResponse == null)
//			{
//				showToast(TAG, "Registration Failed. Check Internet Connection.", Toast.LENGTH_SHORT).show();
//				return null;
//			}
//
//			try
//			{
//				JSONObject responseJSON = new JSONObject(registerResponse);
//
//				if (responseJSON.getInt("response_code") == 1)//ResponseCode.SUCCESS.getValue())
//				{
//					Log.i(TAG, "Registration successful");
//					runOnUiThread(new Runnable() { @Override public void run() { showToast(TAG, "Registration successful", Toast.LENGTH_SHORT).show(); } });
//					Intent mainActivity = new Intent(getInstance(), Streets.class);
//					startActivity(mainActivity);
//					StreetsCommon.registerStreetsActivity(Streets.getInstance());
//				}
//				else if (responseJSON.getInt("response_code") < 0)
//				{
//					Log.i(TAG, "Registration failed with internal error: " + responseJSON.getString("response_message"));
//					runOnUiThread(new Runnable() { @Override public void run() { showToast(TAG, "Registration Failed. An unknown error occurred on the server.", Toast.LENGTH_SHORT).show(); } });
//				}
//				else
//				{
//					final String registerResponseStr = responseJSON.getString("response_message");
//					Log.i(TAG, "Registration failed: " + responseJSON.getString("response_message"));
//					runOnUiThread(new Runnable() { @Override public void run() { showToast(registerResponseStr, Toast.LENGTH_SHORT).show(); } });
//				}
//			}
//			catch (Exception e)
//			{
//				Log.e(TAG, "Registration failed: " + e.getMessage(), e);
//				e.printStackTrace();
//				runOnUiThread(new Runnable() { @Override public void run() { showToast(TAG, "Registration Failed. An unknown error occurred on the server.", Toast.LENGTH_SHORT).show(); } });
//			}
//			return null;
//		}
//
//
//		@Override
//		protected void onPreExecute()
//		{
//			progressDialog = ProgressDialog.show(getInstance(), "Registering", "Registering...", true, false);
//			progressDialog.show();
//		}
//
//		@Override
//		protected void onProgressUpdate(Void... progress) { }
//
//		@Override
//		protected void onPostExecute(Void result)
//		{
//			progressDialog.hide();
//		}
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		try {
//			super.onCreate(savedInstanceState);
//			register = this;
//			setContentView(R.layout.register_layout);
//			firstName = (EditText) findViewById(R.id.registerFirstname);
//			lastName = (EditText) findViewById(R.id.registerLastname);
//			username = (EditText) findViewById(R.id.registerUsername);
//			msisdn = (EditText) findViewById(R.id.registerMsisdn);
//			email = (EditText) findViewById(R.id.registerEmail);
//			password = (EditText) findViewById(R.id.registerPassword);
//			confirmPassword = (EditText) findViewById(R.id.registerConfirmPassword);
//			registerError = (TextView) findViewById(R.id.register_error);
//			registerBtn = (Button) findViewById(R.id.btnRegister);
//			registerBtn.setOnClickListener(this);
//
//			findViewById(R.id.btnLinkToLoginScreen).setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					Intent loginActivity = new Intent(getInstance(), Login.class);
//					startActivity(loginActivity);
//				}
//			});
//		}
//		catch (Exception ex)
//		{
//			ex.printStackTrace();
//			Log.e(TAG, "Failed to create register dialogue: " + ex.getMessage(), ex);
//			runOnUiThread(new Runnable() {
//				@Override public void run() {
//					Toast.makeText(getApplication(), "Failed to create register dialogue! An error occurred.",
//					Toast.LENGTH_SHORT).show();
//				}
//			});
//		}
//	}
//
//	public static Register getInstance() { return register; }
//
//	public void onClick(View view)
//	{
//		if (!password.getText().toString().equals(confirmPassword.getText().toString()))
//		{
//			Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
//			password.requestFocus();
//			return;
//		}
//		new RegisterTask().execute();
//	}
//
//	public String getIMEI()
//	{
//		try
//		{
//			TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//			String imei = mTelephonyMgr.getDeviceId();
//			int retries = 0;
//			while (retries++ != 3 && (imei == null || imei.equals("")))
//			{
//				Log.i(TAG, "Retries = " + retries);
//				mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//				imei = mTelephonyMgr.getDeviceId();
//			}
//			Log.i(TAG, "Got IMEI = " + imei);
//			return imei;
//		}
//		catch (Exception ex)
//		{
//			Log.e(TAG, "Faield to get IMEI: " + ex.getMessage(), ex);
//			return null;
//		}
//	}
//
//	public String getIMSI()
//	{
//		try
//		{
//			TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//			String imsi = mTelephonyMgr.getSubscriberId();
//			int retries = 0;
//			while (retries++ != 3 && (imsi == null || imsi.equals("")))
//			{
//				Log.i(TAG, "Retries = " + retries);
//				mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//				imsi = mTelephonyMgr.getDeviceId();
//			}
//			Log.i(TAG, "Got IMSI = " + imsi);
//			return imsi;
//		}
//		catch (Exception ex)
//		{
//			Log.e(TAG, "Faield to get IMSI: " + ex.getMessage(), ex);
//			return null;
//		}
//	}
//}