package net.blaklizt.streets.persistence.dao;

import net.blaklizt.streets.persistence.UserAttribute;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/10/13
 * Time: 11:25 AM
 */

@Repository
public class UserAttributeDao extends AbstractDao<UserAttribute, Long>
{
	protected UserAttributeDao() { super(UserAttribute.class); }

	public List<UserAttribute> findByGangName(String gangName)
	{
		return findByCriterion(Restrictions.like("gangName", gangName, MatchMode.EXACT));
	}
}
