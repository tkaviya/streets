package net.blaklizt.streets.persistence.dao;

import net.blaklizt.streets.persistence.StoreItem;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 */

@Repository
public class StoreItemDao extends AbstractDao<StoreItem, Long>
{
	protected StoreItemDao() { super(StoreItem.class); }

	public List findByStoreID(Long storeID)
	{
		return findByCriteria(Restrictions.like("storeID", storeID));
	}
}
