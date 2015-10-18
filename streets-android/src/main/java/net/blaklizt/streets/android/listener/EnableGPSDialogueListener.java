package net.blaklizt.streets.android.listener;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import net.blaklizt.streets.android.activity.Startup;
import net.blaklizt.streets.android.common.StreetsCommon;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/15
 * Time: 3:37 AM
 */
public class EnableGPSDialogueListener implements DialogInterface.OnClickListener
{
	private static final String TAG = StreetsCommon.getTag(EnableGPSDialogueListener.class);

	private static final CharSequence[] questionItems = new CharSequence[]{"Never ask again"};

	private static final boolean[] checkedItems = new boolean[] { true };

	public static boolean[] getCheckedItems() { return checkedItems; }

	public static CharSequence[] getQuestionItems() { return questionItems; }

	static boolean askAgain = true;

	public static class EnableGPSOptionListener implements DialogInterface.OnMultiChoiceClickListener
	{
		private static EnableGPSOptionListener enableGPSOptionListener = new EnableGPSOptionListener();

		public static EnableGPSOptionListener getInstance() { return enableGPSOptionListener; }

		@Override
		public void onClick(DialogInterface dialogInterface, int option, boolean checked) {
			Log.i(TAG, "Multiple choice click listener invoked.");
			Log.i(TAG, "option = " + option);
			Log.i(TAG, "checked = " + checked);
			askAgain = !(option == 0 && checked);
		}
	}

	private Context context;

	public EnableGPSDialogueListener(Context context) { this.context = context; }

	@Override
	public void onClick(DialogInterface dialog, int selection) {
		Log.i(TAG, "Ask again GPS dialogue clicked.");
		Log.i(TAG, "selection = " + selection);
		switch (selection){
			case DialogInterface.BUTTON_POSITIVE:
				//Yes button clicked
				Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(myIntent);
                Startup.getStreetsCommon().setUserPreference("suggest_gps", askAgain ? "1" : "0");
				if (!askAgain) {
					Startup.getStreetsCommon().setUserPreference("auto_enable_gps", "1"); //auto_enable without asking
					Startup.getStreetsCommon().setUserPreference("request_gps_perms", "1"); //reset preferences if permissions were updated
				}
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				//No button clicked
                Startup.getStreetsCommon().setUserPreference("suggest_gps", askAgain ? "1" : "0");
				if (!askAgain) {
					Startup.getStreetsCommon().setUserPreference("auto_enable_gps", "0"); //never enable and don't ask
					Startup.getStreetsCommon().setUserPreference("request_gps_perms", "0"); //don't request GPS perms
				}
				break;
		}
	}
}
