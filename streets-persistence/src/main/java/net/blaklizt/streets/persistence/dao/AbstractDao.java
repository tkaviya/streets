package net.blaklizt.streets.persistence.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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

	private Session getCurrentSession() {
		try { return sessionFactory.getCurrentSession(); } catch (Exception e) { e.printStackTrace(); return null; }
	}

	@Transactional
	public void refresh(E e)
	{
		getCurrentSession().refresh(e);
	}

	public E findById(I id)
	{
		try {
			return (E) getCurrentSession().get(entityClass, id);
		} catch (Exception ex) { ex.printStackTrace(); return null; }
	}

	public List findAll()
	{
		try {
			Query queryResult = getCurrentSession().createQuery("from " + className);
			return queryResult.list();
		} catch (Exception ex) { ex.printStackTrace(); return null; }
	}

	@Transactional
	public void saveOrUpdate(E e) {
		getCurrentSession().saveOrUpdate(e);
	}

	@Transactional
	public void save(E e) {
		getCurrentSession().save(e);
	}

	@Transactional
	public void delete(E e) {
		getCurrentSession().delete(e);
	}

	public List findByCriteria(Criterion... criterion) {
		try {
			Criteria criteria = getCurrentSession().createCriteria(entityClass);
			for (Criterion c : criterion) { criteria.add(c); }
			return criteria.list();
		} catch (Exception ex) { ex.printStackTrace(); return null; }
	}

	public List findByCriterion(Criterion criterion) { return findByCriteria(criterion); }
}