package net.blaklizt.streets.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;
import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.common.StreetsCommon;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/11
 * Time: 4:30 PM
 */
public class Startup extends Activity
{
	private static final String TAG = StreetsCommon.getTag(Startup.class);

	private VideoView videoView;

	private static Startup startup;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		try
		{
			startup = this;
			super.onCreate(savedInstanceState);

			if (StreetsCommon.getInstance(this, 0).getUserPreference("ShowIntro").equals("0")) {
				//Go directly to Login, do not pass video, do not disrupt music
				if (Login.getInstance() != null)        //coming backwards, so we are exiting
				{
					Log.i(TAG, "Existing Streetz classes still running. Terminating.");
					StreetsCommon.endApplication();
					return;
				}
				else                                //going forward, let's get this work!
				{
					Log.i(TAG, "Initiating Tha Streetz.");

					Intent loginActivity = new Intent(this, Login.class);
					startActivity(loginActivity);
					StreetsCommon.registerStreetsActivity(Login.getInstance());
					return;
				}
			}

			setContentView(R.layout.video_layout);
			Log.i(TAG, "Playing intro clip...");

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
			videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					if (Login.getInstance() != null)                        //coming backwards, so we are exiting
					{
						Log.i(TAG, "Existing Streetz classes still running. Terminating.");
						StreetsCommon.endApplication();
					} else                                                //going forward, let's get this work!
					{
						Log.i(TAG, "Initiating Tha Streetz.");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Log.i(TAG, "Going to login...");
						Intent loginActivity = new Intent(getApplication(), Login.class);
						startActivity(loginActivity);
						StreetsCommon.registerStreetsActivity(Login.getInstance());
					}
				}
			});
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to start application: " + ex.getMessage(), ex);
			runOnUiThread(new Runnable() {
				@Override public void run() {
					Toast.makeText(getApplication(), "Failed to start application! An error occurred.",
					Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	public static Startup getStartup() { return startup; }

}
