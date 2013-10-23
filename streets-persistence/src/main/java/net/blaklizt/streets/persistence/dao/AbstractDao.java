package net.blaklizt.streets.persistence.dao;

import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/11/13
 * Time: 8:30 PM
 */

@Transactional
public abstract class AbstractDao<E, I extends Serializable> {

	@Autowired(required = true)
	private SessionFactory sessionFactory;

	private Class<E> entityClass;

	private String className;

	protected AbstractDao(Class<E> entityClass) {
		this.entityClass = entityClass;
		this.className = entityClass.getSimpleName();
	}

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	public E findById(I id)
	{
		return (E) getCurrentSession().get(entityClass, id);
	}

	public List findAll()
	{
		Query queryResult = getCurrentSession().createQuery("from " + className);
		return queryResult.list();
	}

	@Transactional
	public void saveOrUpdate(E e) {
		getCurrentSession().saveOrUpdate(e);
		getCurrentSession().flush();
	}

	@Transactional
	public void delete(E e) {
		getCurrentSession().delete(e);
		getCurrentSession().flush();
	}

	public List findByCriteria(Criterion criterion) {
		Criteria criteria = getCurrentSession().createCriteria(entityClass);
		criteria.add(criterion);
		return criteria.list();
	}
}