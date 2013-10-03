package net.blaklizt.streets.persistence.dao;

import net.blaklizt.streets.persistence.BusinessProblem;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 */

@Repository
public class BusinessProblemDao extends AbstractDao<BusinessProblem, Long>
{
	protected BusinessProblemDao() { super(BusinessProblem.class); }

	public List findByBusinessType(String businessType)
	{
		return findByCriteria(Restrictions.like("businessType", businessType, MatchMode.EXACT));
	}

//	@Override
//	protected String getEntityClassName() {
//		return BusinessProblem.class.getSimpleName();
//	}
}
