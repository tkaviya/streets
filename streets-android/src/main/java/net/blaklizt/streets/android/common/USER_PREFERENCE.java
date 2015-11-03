package net.blaklizt.streets.android.common;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/15
 * Time: 3:29 AM
 */
public enum USER_PREFERENCE
{
    SHOW_INTRO("1", "show_intro", "1", "Enable intro video", "boolean"),
    AUTO_LOGIN("2", "auto_login", "0", "Login automatically", "boolean"),
    SUGGEST_GPS("3", "suggest_gps", "1", "Ask to enable GPS", "boolean"),
    AUTO_ENABLE_GPS("4", "auto_enable_gps", "1", "Auto enable GPS", "boolean"),
    SHOW_LOCATION("5", "show_location", "1", "Share location", "boolean"),
    SHOW_DISTANCE("6", "show_distance", "1", "Share distance", "boolean"),
    RECEIVE_REQUESTS("7", "receive_requests", "1", "Allow interaction", "boolean"),
    SHOW_HISTORY("8", "show_history", "1", "Show history", "boolean"),
    ASK_ON_EXIT("9", "ask_on_exit", "1", "Check before exiting","boolean"),
    ENABLE_TTS("10", "enable_tts", "1", "Enable TextToSpeech", "boolean"),
    REQUEST_GPS_PERMS("11", "request_gps_perms", "1", "Ask GPS permissions", "boolean");

    public final String pref_id;
    public final String pref_name;
    public String pref_value;
    public final String pref_description;
    public final String pref_data_type;

    @Override public String toString() { return pref_name; }

    USER_PREFERENCE(String pref_id, String pref_name, String default_value, String pref_description, String pref_data_type) {
        this.pref_id = pref_id; this.pref_name = pref_name; this.pref_value = default_value;
        this.pref_description = pref_description; this.pref_data_type = pref_data_type;
    }
}
