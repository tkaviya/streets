package net.blaklizt.streets.persistence.dao;

import net.blaklizt.streets.persistence.Module;
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
public class ModuleDao extends AbstractDao<Module, Long>
{
	protected ModuleDao() { super(Module.class); }

	public List<Module> findAllActive()
	{
		return findByCriterion(Restrictions.like("enabled", true));
	}

	public Module findByModuleName(String moduleName)
	{
		List result = findByCriterion(Restrictions.like("moduleName", moduleName, MatchMode.EXACT));
		if (result == null || result.size() != 1) return null;
		return (Module)result.get(0);
	}
}