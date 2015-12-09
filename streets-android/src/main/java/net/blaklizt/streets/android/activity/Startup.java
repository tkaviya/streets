package net.blaklizt.streets.android.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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
import net.blaklizt.streets.android.common.StreetsCommon;

import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static net.blaklizt.streets.android.common.enumeration.USER_PREFERENCE.AUTO_LOGIN;
import static net.blaklizt.streets.android.common.enumeration.USER_PREFERENCE.SHOW_INTRO;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/11
 * Time: 4:30 PM
 */
public class Startup extends AppCompatActivity implements OnClickListener, MediaPlayer.OnCompletionListener
{
	public Button btnLogin = null;
	public EditText edtPassword = null;
	private CheckBox chkAutoLogin = null;

    private final String TAG = StreetsCommon.getTag(Startup.class);

	private VideoView videoView;

	private static Startup startup;

    @Override
	public void onCreate(Bundle savedInstanceState)
	{
        AppContext.getInstance(this.getApplicationContext()); startup = this;

        Log.i(TAG, "+++ ON CREATE +++");
		super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);

        findViewById(R.id.labelLoginHeader).setOnClickListener(view -> {
            Intent loginActivity = new Intent(getInstance(), RegisterService.class);
            startActivity(loginActivity);
        });
        findViewById(R.id.labelGoToRegistration).setOnClickListener(view -> {
            Intent loginActivity = new Intent(getInstance(), RegisterService.class);
            startActivity(loginActivity);
        });
        try
		{
			edtPassword = (EditText) findViewById(R.id.loginPin);
			btnLogin = (Button) findViewById(R.id.btnLogin);
			chkAutoLogin = (CheckBox) findViewById(R.id.chkAutoLogin);

			if (AppContext.getStreetsCommon().getUserPreferenceValue(SHOW_INTRO).equals("1")) {
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

		if (AppContext.getStreetsCommon().getUserPreferenceValue(AUTO_LOGIN).equals("1")) {
			SequentialTaskManager.runWhenAvailable(new LoginTask(this));
		}
		else {
			edtPassword.setVisibility(VISIBLE);
			btnLogin.setVisibility(VISIBLE);
			chkAutoLogin.setVisibility(VISIBLE);
		}
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
    @SuppressWarnings("unchecked")
	public void onClick(View view)
	{
        Log.i(TAG, "+++ ON CLICK +++");
		new LoginTask(this).execute();
	}

    @Override
    public void onDestroy() {

        Log.i(TAG, "+++ ON DESTROY +++");
        super.onDestroy();

        AppContext.shutdown();
    }

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
        Log.i(TAG, "+++ ON POST CREATE +++");
		super.onPostCreate(savedInstanceState);
		btnLogin.setOnClickListener(this);
	}

	public static Startup getInstance() { return startup; }
}
