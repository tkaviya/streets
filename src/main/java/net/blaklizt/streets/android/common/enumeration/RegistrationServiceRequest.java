package net.blaklizt.streets.android.common.enumeration;

/**
 * Created by tsungai.kaviya on 2015-11-22.
 */
public class RegistrationServiceRequest {

    public enum SERVICE_TYPE { ESTABLISHED_BUSINESS, SMALL_BUSINESS, OG_HUSTLE }

    public String serivceName;
    public SERVICE_TYPE serviceType;
    public String serviceDescription;
}
