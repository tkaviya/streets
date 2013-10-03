package net.blaklizt.streets.common.utilities;

import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/12/13
 * Time: 10:05 PM
 */
public class CommonUtilities
{
//	public static boolean isValidEmail(String emailAddress)
//	{
//		return emailAddress.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
//	}

	public static void RefreshBundles()
	{
		ResourceBundle.clearCache();
	}

	public static Properties resourceBundleToProperties(ResourceBundle bundle)
	{
		Properties props = new Properties();
		Enumeration<String> keys = bundle.getKeys();
		while(keys.hasMoreElements())
		{
			String key = keys.nextElement();
			props.put(key, bundle.getObject(key));
		}
		return props;
	}

	public static String alignStringToLength(String text, int length)
	{
		if (text == null) text = "";

		while (text.length() < length)
			text += " ";
		return text;
	}

	public static String getCurrencySymbol()
	{
		return getConfiguration("properties/streets_configuration", "currencySymbol");
	}

	public static String getCountryCodePrefix() {
		/*
		 * We have to use locales to get the right prefix
		 *
		 */
		return getConfiguration("properties/streets_configuration", "countryCode");
	}

	public static String[] getConfigurations(String bundle,String property)
	{
		try
		{
			ResourceBundle rb = ResourceBundle.getBundle(bundle);
			if(rb.containsKey(property))
				return rb.getString(property).split("\\,");
			return null;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static String getConfiguration(String propertyKey)
	{
		ResourceBundle rb = ResourceBundle.getBundle("properties/streets_configuration");
		if(rb.containsKey(propertyKey))
			return rb.getString(propertyKey);
		return null;
	}

	public static String getConfiguration(String bundle, String propertyKey)
	{
		ResourceBundle rb = ResourceBundle.getBundle(bundle);
		if(rb.containsKey(propertyKey))
			return rb.getString(propertyKey);
		return null;
	}

	public static String getConfiguration(String bundle, String propertyKey, String defaultProperty)
	{
		try
		{
			String property = getConfiguration(bundle,propertyKey);
			if (property == null)
				return defaultProperty;
			else
				return property;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return defaultProperty;
		}
	}

	public static String formatDoubleToMoney(double value, boolean includeCurrencySymbol)
	{
		DecimalFormat df = new DecimalFormat("###,##0.00");
		return (includeCurrencySymbol ? getCurrencySymbol() : "") + df.format(value);
	}

	public static int round(double d)
	{
		double dAbs = Math.abs(d);
		int i = (int) dAbs;

		if((dAbs - (double)i) < 0.5)	return d < 0 ? -i		: i;		//negative value
		else							return d < 0 ? -(i+1)	: i + 1;	//positive value
	}

}
