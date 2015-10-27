package net.blaklizt.streets.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.common.StreetsCommon;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/11
 * Time: 4:30 PM
 */
public class Startup extends AppCompatActivity implements
	View.OnClickListener, MediaPlayer.OnCompletionListener
{
	private Button btnLogin = null;
	private EditText edtPassword = null;
	private CheckBox chkAutoLogin = null;
	int counter = 5;

	private class LoginTask extends AsyncTask<Void, Void, Void>
	{
		ProgressDialog progressDialog;

		@Override
		protected Void doInBackground(Void... param)
		{
			Log.i(TAG, "Authenticating...");

//			ServerCommunication.sendServerRequest("action=Login&channel=" + StreetsCommon.CHANNEL + "&username=" + username.getText().toString() + "&edtPassword=" + edtPassword.getText().toString());
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
					runOnUiThread(new Runnable() {
						@Override public void run() {
							Toast.makeText(getInstance(), "Login successful", Toast.LENGTH_SHORT).show();
							if (chkAutoLogin.isChecked()) {
								getStreetsCommon().setUserPreference("auto_login", "1");
							}
						}
					});

					Intent mainActivity = new Intent(getInstance(), MenuActivity.class);
					startActivity(mainActivity);
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
					getStreetsCommon().setUserPreference("auto_login", "0"); //disable auto login to prevent running out of attempts
					runOnUiThread(new Runnable() { @Override public void run() { Toast.makeText(getInstance(), loginResponseStr, Toast.LENGTH_SHORT).show(); } });

					if (--counter <= 0)
					{
						runOnUiThread(new Runnable() {
							@Override public void run() {
								edtPassword.setEnabled(false);
								btnLogin.setEnabled(false);
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

	private static final String TAG = StreetsCommon.getTag(Startup.class);

	private VideoView videoView;

	private static Startup startup;

    private static StreetsCommon streetsCommon = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
        Log.i(TAG, "+++ ON CREATE +++");
		super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);
        startup = this;

        try
		{
			edtPassword = (EditText) findViewById(R.id.loginPin);
			btnLogin = (Button) findViewById(R.id.btnLogin);
			chkAutoLogin = (CheckBox) findViewById(R.id.chkAutoLogin);

			if (getStreetsCommon().getUserPreferenceValue("show_intro").equals("1")) {
                playIntroVideo();
			} else {
				onCompletion(null);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to start application: " + ex.getMessage(), ex);
			runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplication(), "Failed to start application! An error occurred.",
                            Toast.LENGTH_SHORT).show();
                }
            });
			finish();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.i(TAG, "+++ ON COMPLETION +++");
		if (getStreetsCommon().getUserPreferenceValue("auto_login").equals("1")) {
			new LoginTask().execute();
		}
		else {
			edtPassword.setVisibility(View.VISIBLE);
			btnLogin.setVisibility(View.VISIBLE);
			chkAutoLogin.setVisibility(View.VISIBLE);
		}
	}

    public static StreetsCommon getStreetsCommon() {
        if (streetsCommon == null) {
            streetsCommon = StreetsCommon.getInstance(getInstance(), 0);
        }
        return streetsCommon;
    }

    public void playIntroVideo() {
        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro_clip));
        videoView.setMediaController(null);
        videoView.setClickable(false);
        videoView.setSoundEffectsEnabled(false);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(0, 0);
                mp.setLooping(false);
                videoView.start();
            }
        });
        videoView.setOnCompletionListener(this);
    }

	@Override
	public void onClick(View view)
	{
		new LoginTask().execute();
	}

    @Override
    public void onDestroy() {
		Log.i(TAG, "+++ ON DESTROY +++");
		super.onDestroy();
        if (streetsCommon != null) { streetsCommon.endApplication(); }
        if (videoView != null) { videoView.stopPlayback(); }
    }

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON POST CREATE +++");
		super.onPostCreate(savedInstanceState);
		btnLogin.setOnClickListener(this);
	}

	public static Startup getInstance() { return startup; }

}
