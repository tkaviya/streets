package net.blaklizt.streets.common.utilities;

import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/12/13
 * Time: 10:05 PM
 */
public class StreetsUtilities
{
	public static String getConfiguration(String propertyKey)
	{
		ResourceBundle rb = ResourceBundle.getBundle("properties/streets_configuration");
		if(rb.containsKey(propertyKey))
			return rb.getString(propertyKey);
		return null;
	}
}
