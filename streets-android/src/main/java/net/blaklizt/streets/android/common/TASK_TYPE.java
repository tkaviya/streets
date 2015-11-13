package net.blaklizt.streets.android.common;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/15
 * Time: 3:29 AM
 */
public enum TASK_TYPE
{
    SYS_DB              ("1", "system_db_access",   "system access of the database"),
    SYS_TASK            ("2", "system_task",        "system task execution"),
    SYS_SECURITY        ("3", "system_security",    "system security validations"),

    USER_AUTH           ("10", "user_auth",         "user authentication"),
    USER_INPUT          ("11", "user_data",         "user input data"),
    USER_PREF_READ      ("12", "user_read",         "read user preference"),
    USER_PREF_UPDATE    ("13", "user_customization","user preference customization"),

    NET_ACCESS          ("30", "net_access",        "network access"),

    BG_GOOGLE_MAP_TASK  ("51", "google_map_task",   "load google map"),
    BG_LOCATION_TASK    ("52", "location_task",     "update current user location"),
    BG_PLACES_TASK      ("53", "places_task",       "update nearby places"),

    FG_LOGIN_TASK       ("101", "login_task",       "login to the app"),
    FG_REGISTER_TASK    ("102", "register_task",    "register onto the streets"),
    FG_INTRO_VIDEO_TASK ("103", "intro_video",      "introductory video");

    public final String task_type_id;
    public final String task_type_name;
    public final String description;

    TASK_TYPE(String tt_id, String task_type_name, String description) {
        this.task_type_id = tt_id; this.task_type_name = task_type_name; this.description = description;
    }
}
