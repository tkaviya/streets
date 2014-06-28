package net.blaklizt.streets.persistence.dao;

import net.blaklizt.streets.persistence.User;
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
public class UserDao extends AbstractDao<User, Long>
{
	protected UserDao() { super(User.class); }

	public User findByUsername(String username)
	{
		List result = findByCriterion(Restrictions.like("username", username, MatchMode.EXACT));
		if (result == null || result.size() != 1) return null;
		return (User)result.get(0);
	}

	public User findByEmail(String email)
	{
		List result = findByCriterion(Restrictions.like("email", email, MatchMode.EXACT));
		if (result == null || result.size() != 1) return null;
		return (User)result.get(0);
	}
}
