package net.blaklizt.streets.android.common.enumeration;

/**
 * Created by tsungai.kaviya on 2015-11-21.
 */
public enum SHARE_PROVIDER {

    GOOGLE("Google", "com.google"),
    WHATSAPP("Whatsapp", "com.whatsapp"),
    TWITTER("Twitter", "com.twitter.android.auth.login");

    public final String app_name;
    public final String app_package;

    SHARE_PROVIDER(String app_name, String app_package) {
        this.app_name = app_name;
        this.app_package = app_package;
    }
}
