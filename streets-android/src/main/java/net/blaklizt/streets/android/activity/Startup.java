package net.blaklizt.streets.android.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.activity.helpers.LoginTask;
import net.blaklizt.streets.android.activity.helpers.SequentialTaskManager;
import net.blaklizt.streets.android.activity.helpers.StreetsProviderPattern;
import net.blaklizt.streets.android.common.STATUS_CODES;
import net.blaklizt.streets.android.common.StreetsCommon;
import net.blaklizt.streets.android.common.TASK_TYPE;
import net.blaklizt.streets.android.common.USER_PREFERENCE;
import net.blaklizt.streets.android.common.utils.SecurityContext;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/11
 * Time: 4:30 PM
 */
public class Startup extends AppCompatActivity implements
        View.OnClickListener, MediaPlayer.OnCompletionListener, StreetsProviderPattern
{
	public Button btnLogin = null;
	public EditText edtPassword = null;
	private CheckBox chkAutoLogin = null;

    private ArrayList<StreetsProviderPattern> shutdownCallbackQueue = new ArrayList<>();

    private final String TAG =  StreetsCommon.getTag(Startup.class);

	private VideoView videoView;

	private static Startup startup;

    private static StreetsCommon streetsCommon = null;

    private static SecurityContext securityContext = null;

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

			if (getStreetsCommon().getUserPreferenceValue(USER_PREFERENCE.SHOW_INTRO).equals("1")) {
                playIntroVideo();
			} else {
				onCompletion(null);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to start application: " + ex.getMessage(), ex);
			runOnUiThread(() ->
                Toast.makeText(getApplication(), "Failed to start application! An error occurred.",
                Toast.LENGTH_SHORT).show());
			finish();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.i(TAG, "+++ ON COMPLETION +++");
		if (getStreetsCommon().getUserPreferenceValue(USER_PREFERENCE.AUTO_LOGIN).equals("1")) {
			SequentialTaskManager.runImmediately(new LoginTask(this));
		}
		else {
			edtPassword.setVisibility(View.VISIBLE);
			btnLogin.setVisibility(View.VISIBLE);
			chkAutoLogin.setVisibility(View.VISIBLE);
		}
	}

    public static StreetsCommon getStreetsCommon() {
        if (streetsCommon == null) {
            streetsCommon = StreetsCommon.getInstance(getInstance());
        }
        return streetsCommon;
    }

    public static SecurityContext getSecurityContext() {

        if (securityContext == null) {
            securityContext = SecurityContext.getInstance();
        }
        return securityContext;
    }

    public void registerOnDestroyHandler(StreetsProviderPattern onDestroyHandler) {
        shutdownCallbackQueue.add(onDestroyHandler);
    }

    public void playIntroVideo() {
        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro_clip));
        videoView.setMediaController(null);
        videoView.setClickable(false);
        videoView.setSoundEffectsEnabled(false);
        videoView.requestFocus();
        videoView.setOnPreparedListener(mp -> {
            mp.setVolume(0, 0);
            mp.setLooping(false);
            videoView.start();
        });
        videoView.setOnCompletionListener(this);
    }

	@Override
	public void onClick(View view)
	{
        Log.i(TAG, "+++ ON CLICK +++");
		new LoginTask(this).execute();
	}

    @Override
    public void onResume() {
        Log.i(TAG, "+++ ON RESUME +++");
        super.onResume();
        if (MenuLayout.getInstance() != null) {
            onDestroy();
        }
    }

    @Override
    public void onDestroy() {

		StreetsCommon.showSnackBar(TAG, "[- Now leaving Tha Streetz -]\n ...Goodbye...", Snackbar.LENGTH_SHORT);

        Log.i(TAG, "+++ ON DESTROY +++");
		super.onDestroy();

        Log.i(TAG, "Terminating video...");
        if (videoView != null) { videoView.stopPlayback(); videoView = null; }

        for (StreetsProviderPattern shutdownHandler : shutdownCallbackQueue) {
            shutdownHandler.onTermination();
        }

        shutdownCallbackQueue.clear();
        shutdownCallbackQueue = null;

        if (streetsCommon != null) { streetsCommon.endApplication(); }

        getStreetsCommon().writeEventLog(TASK_TYPE.SYS_TASK, STATUS_CODES.SUCCESS, "Shutdown completed cleanly");
        finish();
    }

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON POST CREATE +++");
		super.onPostCreate(savedInstanceState);
		btnLogin.setOnClickListener(this);
	}

	public static Startup getInstance() { return startup; }

    @Override
    public void onTermination() {

    }

    @Override
    public String getClassName() {
        return this.getClass().getSimpleName();
    }
}
