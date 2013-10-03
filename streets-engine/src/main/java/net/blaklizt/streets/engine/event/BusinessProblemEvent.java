package net.blaklizt.streets.engine.event;

import net.blaklizt.streets.persistence.BusinessProblem;
import net.blaklizt.streets.persistence.Location;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 10/3/13
 * Time: 9:17 PM
 */
public class BusinessProblemEvent extends Event
{
	protected Location location;
	protected BusinessProblem businessProblem;

	public BusinessProblemEvent(String name, String description, BusinessProblem businessProblem, Location location)
	{
		super(name, description);
		this.location = location;
		this.businessProblem = businessProblem;
	}

	public BusinessProblem getBusinessProblem() {
		return businessProblem;
	}

	public void setBusinessProblem(BusinessProblem businessProblem) {
		this.businessProblem = businessProblem;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
