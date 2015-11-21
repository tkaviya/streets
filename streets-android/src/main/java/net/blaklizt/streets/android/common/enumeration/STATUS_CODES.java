package net.blaklizt.streets.android.common.enumeration;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/15
 * Time: 3:29 AM
 */
public enum STATUS_CODES
{
    SUCCESS         ("0", "success",        "success"),
    GENERAL_ERROR   ("-1", "general_error", "general_error"),
    CANCELLED       ("10", "cancelled",     "cancelled"),
    TIMEOUT         ("11", "timeout",       "timeout");

    public final String status_code;
    public final String status_name;
    public final String status_description;

    STATUS_CODES(String status_code, String status_name, String status_description) {
        this.status_code = status_code; this.status_name = status_name;
        this.status_description = status_description;
    }
}
