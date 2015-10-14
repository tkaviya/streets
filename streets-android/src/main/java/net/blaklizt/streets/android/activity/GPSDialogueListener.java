package net.blaklizt.streets.android.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/15
 * Time: 3:37 AM
 */
public class GPSDialogueListener implements DialogInterface.OnClickListener
{
	static boolean askAgain = true;

	static class GPSDialogueOptionsListener implements DialogInterface.OnMultiChoiceClickListener
	{
		@Override
		public void onClick(DialogInterface dialogInterface, int option, boolean value) {
			switch (option){
				case 0:
					askAgain = !value;
					break;
			}
		}
	}

	private Context context;

	public GPSDialogueListener(Context context) { this.context = context; }

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which){
			case DialogInterface.BUTTON_POSITIVE:
				//Yes button clicked
				Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(myIntent);
				Streets.getStreetsCommon().setUserPreference("suggest_gps", askAgain ? "1" : "0");
				if (!askAgain) Streets.getStreetsCommon().setUserPreference("auto_enable_gps", "1"); //auto_enable without asking
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				//No button clicked
				Streets.getStreetsCommon().setUserPreference("suggest_gps", askAgain ? "1" : "0");
				if (!askAgain) Streets.getStreetsCommon().setUserPreference("auto_enable_gps", "0"); //never enable and don't ask
				break;
		}
	}
}
