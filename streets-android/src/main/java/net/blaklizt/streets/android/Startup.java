package net.blaklizt.streets.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/11
 * Time: 4:30 PM
 */
public class Startup extends Activity
{
	private static final String TAG = Streets.TAG + "_" + Startup.class.getSimpleName();

//	private MediaPlayer mediaPlayer;
	private VideoView videoView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_layout);
		Log.i(TAG, "Playing intro clip...");

//		mediaPlayer = MediaPlayer.create(getApplication(), R.raw.intro_clip);
//		mediaPlayer.start();

		videoView = (VideoView)findViewById(R.id.videoView);
		videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro_clip));
		videoView.setMediaController(new MediaController(this));
		videoView.requestFocus();
		videoView.start();

		Log.i(TAG, "Going to login...");
		Intent loginActivity = new Intent(this, Login.class);
		startActivity(loginActivity);
	}

	@Override
	public void onDestroy()
	{
//		if (mediaPlayer != null) mediaPlayer.release();
	}
}
