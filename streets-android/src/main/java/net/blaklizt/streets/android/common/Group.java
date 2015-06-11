package net.blaklizt.streets.android.common;

/**
 * User: tkaviya
 * Date: 9/13/14
 * Time: 2:05 PM
 */
import java.util.ArrayList;
import java.util.List;

public class Group {

	public String string;
	public final List<String> children = new ArrayList<String>();

	public Group(String string) {
		this.string = string;
	}

}

