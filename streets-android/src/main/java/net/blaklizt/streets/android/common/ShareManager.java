package net.blaklizt.streets.android.common;

import android.app.Activity;
import android.content.Intent;
import net.blaklizt.streets.android.common.enumeration.SHARE_PROVIDER;

import static net.blaklizt.streets.android.common.StreetsCommon.getTag;

/**
 * Created by tsungai.kaviya on 2015-11-21.
 */
public class ShareManager {

    private static final String TAG = getTag(ShareManager.class);

    public static boolean share(Activity activity, SHARE_PROVIDER shareProvider, String shareData) {
        Intent waIntent = new Intent(Intent.ACTION_SEND);
        waIntent.setType("text/plain");
        waIntent.setPackage(shareProvider.app_package);
        waIntent.putExtra(Intent.EXTRA_TEXT, shareData);//
        activity.startActivity(Intent.createChooser(waIntent, "Share with"));
        return true;
    }
}
