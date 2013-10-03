package net.blaklizt.streets.persistence.dao;

import net.blaklizt.streets.persistence.UserItem;
import net.blaklizt.streets.persistence.UserItemPK;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 9/13/13
 * Time: 3:36 PM
 */
@Repository
public class UserItemDao extends AbstractDao<UserItem, UserItemPK>
{
	protected UserItemDao() { super(UserItem.class); }
}