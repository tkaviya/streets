package net.blaklizt.streets.android.common.utils;

/**
 * User: tkaviya
 * Date: 3/27/2015
 * Time: 6:44 AM
 */
public class Validator
{
    private static final Integer MIN_PASSW_LEN = 6;
    private static final Integer MAX_PASSW_LEN = 50;
    private static final Integer PIN_LEN = 4;

    private static final Integer MIN_NAME_LEN = 2;
    private static final Integer MAX_NAME_LEN = 50;

    private static final Integer MIN_UNAME_LEN = 2;
    private static final Integer MAX_UNAME_LEN = 50;

    public static boolean isNumeric(Object testObject) {
        if (testObject == null) return false;
        try { Float.parseFloat(String.valueOf(testObject)); return true; }
        catch (Exception e) { return false; }
    }

    public static boolean isValidEmail(String emailAddress)
    {
        return !isNullOrEmpty(emailAddress) && emailAddress.matches("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$");
    }


    public static boolean isValidPassword(String password)
    {
        return password.matches("[a-zA-Z0-9<>.!@();:#$%&*+/=?^_{|}~-]{" + MIN_PASSW_LEN + "," + MAX_PASSW_LEN +"}");
    }

    public static boolean isValidPin(String pin)
    {
        return pin.matches("[0-9]{" + PIN_LEN +"}");
    }

    public static boolean isValidMsisdn(String msisdn, String countryCodePrefix) {
        return isValidMsisdn(msisdn) || msisdn.matches("^(" + countryCodePrefix + ")[0-9]{9}");
    }

    public static boolean isNullOrEmpty(String string)
    {
        return string == null || string.trim().equals("");
    }

    public static boolean isValidMsisdn(String msisdn)      {   return msisdn.matches("^(0)[0-9]{9}");          }

    public static boolean isValidName(String name) {
        return name.matches("[a-zA-Z]{" + MIN_NAME_LEN + "," + MAX_NAME_LEN + "}");
    }

    public static boolean isValidUsername(String username) {
        return username.matches("[a-zA-Z0-9-_.]{" + MIN_UNAME_LEN + "," + MAX_UNAME_LEN + "}");
    }

}