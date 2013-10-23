package net.blaklizt.streets.persistence.dao;

import net.blaklizt.streets.persistence.Gang;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/10/13
 * Time: 11:25 AM
 */

@Repository
public class GangDao extends AbstractDao<Gang, Long>
{
	protected GangDao() { super(Gang.class); }
}
