package net.blaklizt.streets.android;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/10
 * Time: 10:53 PM
 */

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.app.ActionBarActivity;
import net.blaklizt.streets.android.persistence.StreetsDBHelper;

import java.util.Locale;

public class Streets extends ActionBarActivity
{
    public static final String TAG = "Streets";
	protected StreetsDBHelper streetsDBHelper = null;

	protected static TextToSpeech ttsEngine = null;
    protected static Streets streets = null;

	public StreetsDBHelper getStreetsDBHelper() { return streetsDBHelper; }

	public TextToSpeech getTTSEngine() { return ttsEngine; }

    public static Streets getInstance() { return streets; }

	// Declaring Your View and Variables

	Toolbar toolbar;
	ViewPager pager;
	ViewPagerAdapter adapter;
	SlidingTabLayout tabs;
	CharSequence Titles[] = { "MAP", "NAV" };
	int Numboftabs =2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "+++ ON CREATE +++");

		setContentView(R.layout.activity_main);

		initializeStreetsData();

		initializeTabs();

		Log.i(TAG, "Displayed Main Layout");

	}

	@Override
	public void onStart() {
		Log.i(TAG, "+++ ON START +++");
		super.onStart();
	}

	@Override
	public void onDestroy()
	{
		if (ttsEngine != null) ttsEngine.shutdown();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		int id = item.getItemId();

		switch (id)
		{
            case R.id.action_search:
                return true;
            case R.id.action_settings:
                return true;
			case R.id.action_get_location:
//				getActionBar().selectTab(mapTab);
//				MapLayout.getInstance().refreshLocation();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void initializeStreetsData()
	{
		try
		{
			Log.i(TAG, "Initializing streets core data");
			streets = this;
			streetsDBHelper = new StreetsDBHelper(getApplicationContext());
			ttsEngine = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener()
			{
				@Override
				public void onInit(int i)
				{
					Log.i(TAG, "Initialized text to speech engine");
					ttsEngine.setLanguage(Locale.US);
				}
			});
//			status_text_view = (TextView)findViewById(R.id.status_text_view);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to initialize streets core data", ex);
		}
	}

	private void initializeTabs()
	{
		try
		{
			Log.i(TAG, "Initializing tabs");
			// Creating The Toolbar and setting it as the Toolbar for the activity

			toolbar = (Toolbar) findViewById(R.id.tool_bar);
			setSupportActionBar(toolbar);


			// Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
			adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

			// Assigning ViewPager View and setting the adapter
			pager = (ViewPager) findViewById(R.id.pager);
			pager.setAdapter(adapter);

			// Assiging the Sliding Tab Layout View
			tabs = (SlidingTabLayout) findViewById(R.id.tabs);
			tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

			// Setting Custom Color for the Scroll bar indicator of the Tab View
			tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
				@Override
				public int getIndicatorColor(int position) {
					return getResources().getColor(R.color.tabsScrollColor);
				}
			});

			// Setting the ViewPager For the SlidingTabsLayout
			tabs.setViewPager(pager);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			Log.e(TAG, "Failed to initialize app tabs", ex);
		}
	}
}
