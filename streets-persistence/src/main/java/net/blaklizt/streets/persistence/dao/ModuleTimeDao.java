package net.blaklizt.streets.persistence.dao;

import net.blaklizt.streets.persistence.ModuleTime;
import net.blaklizt.streets.persistence.ModuleTimePK;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 9/13/13
 * Time: 3:36 PM
 */
@Repository
public class ModuleTimeDao extends AbstractDao<ModuleTime, ModuleTimePK>
{
	protected ModuleTimeDao() { super(ModuleTime.class); }

	public List<ModuleTime> findModuleId(Long moduleId)
	{
		return findByCriterion(Restrictions.like("moduleId", moduleId));
	}

	public List findByModuleName(String moduleName)
	{
		return findByCriteria(Restrictions.like("moduleName", moduleName, MatchMode.EXACT));
	}
}