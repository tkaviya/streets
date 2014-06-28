package net.blaklizt.streets.core;

import net.blaklizt.streets.persistence.dao.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 11/14/13
 * Time: 8:00 PM
 */

public class CoreDaoManager
{
	@Autowired private EventLogDao eventLogDao;
	@Autowired private ModuleDao moduleDao;
	@Autowired private ModuleTimeDao moduleTimeDao;
	@Autowired private UserDao userDao;
	@Autowired private UserAttributeDao userAttributeDao;
	@Autowired private LocationDao locationDao;
	@Autowired private GangDao gangDao;
	@Autowired private BusinessProblemDao businessProblemDao;
	@Autowired private StoreDao storeDao;
	@Autowired private StoreItemDao storeItemDao;
	@Autowired private UserItemDao userItemDao;


	private static CoreDaoManager coreDaoManager = null;

	private CoreDaoManager() {}

	public static CoreDaoManager getInstance()
	{
		if (coreDaoManager == null) coreDaoManager = new CoreDaoManager();
		return coreDaoManager;
	}

	public EventLogDao getEventLogDao() { return eventLogDao; }
	public ModuleDao getModuleDao() { return moduleDao; }
	public ModuleTimeDao getModuleTimeDao() { return moduleTimeDao; }
	public UserDao getUserDao() { return userDao; }
	public UserAttributeDao getUserAttributeDao() { return userAttributeDao; }
	public LocationDao getLocationDao() { return locationDao; }
	public GangDao getGangDao() { return gangDao; }
	public BusinessProblemDao getBusinessProblemDao() { return businessProblemDao; }
	public StoreDao getStoreDao() { return storeDao; }
	public StoreItemDao getStoreItemDao() { return storeItemDao; }
	public UserItemDao getUserItemDao() { return userItemDao; }
}
