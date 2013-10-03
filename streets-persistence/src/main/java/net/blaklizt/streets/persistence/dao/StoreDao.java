package net.blaklizt.streets.persistence.dao;

import net.blaklizt.streets.persistence.Store;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 */

@Repository
public class StoreDao extends AbstractDao<Store, Long>
{
	protected StoreDao() { super(Store.class); }

	public Store findByLocationID(Long locationID)
	{
		List result = findByCriteria(Restrictions.like("locationID", locationID));
		if (result == null || result.size() != 1) return null;
		return (Store)result.get(0);
	}
}
