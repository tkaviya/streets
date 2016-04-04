package net.blaklizt.streets.core.event;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * SymbiosisUser: tkaviya
 * Date: 10/3/13
 * Time: 8:53 PM
 */
public class Event implements Serializable {
	protected String name;
	protected String description;

	public Event(String name, String description)
	{
		this.name = name;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}
}
