package net.blaklizt.streets.persistence.dao;

import net.blaklizt.streets.persistence.Location;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/10/13
 * Time: 11:25 AM
 */

@Repository
public class LocationDao extends AbstractDao<Location, Long>
{
	protected LocationDao() { super(Location.class); }
}
