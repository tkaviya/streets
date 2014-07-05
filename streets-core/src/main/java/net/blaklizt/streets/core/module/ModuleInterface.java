package net.blaklizt.streets.core.module;

import net.blaklizt.streets.core.CoreDaoManager;
import net.blaklizt.streets.core.Streets;
import net.blaklizt.streets.core.menu.Menu;
import net.blaklizt.streets.core.menu.MenuItem;
import net.blaklizt.streets.core.session.UserSession;
import net.blaklizt.streets.persistence.Module;
import net.blaklizt.streets.persistence.ModuleTime;
import net.blaklizt.symbiosis.sym_common.configuration.Configuration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 11/10/13
 * Time: 5:09 PM
 */
@Singleton
public abstract class ModuleInterface extends MenuItem
{
	protected final static Long ONE_DAY = 86400000L;

	private static HashMap<String, ModuleInterface> moduleEngines = new HashMap<>();

	protected CoreDaoManager coreDaoManager = CoreDaoManager.getInstance();

	@Autowired
	protected Streets streets;

	protected MenuItem instance = null;
	
	protected Logger log4j;

	protected Module module;
	
	protected ModuleInterface(String name)
	{
		super(name); instance = this;
		log4j = Configuration.getNewLogger(this.getClass().getSimpleName());
		moduleEngines.put(name, this);
	}

	@PostConstruct
	private void init()
	{
		module = coreDaoManager.getModuleDao().findByModuleName(name);
		streets.addModule(this);
	}

	public String getModuleShortcut() { return null; }

	public void scheduleRunTimes()
	{
		try
		{
			log4j.info("Scheduling run times for module " + name);

			Timer gameTimer = streets.getStreetsEventsTimer();

			List<ModuleTime> executionTimes = coreDaoManager.getModuleTimeDao().findModuleId(module.getModuleId());

			Calendar fireTime = Calendar.getInstance();

			for (final ModuleTime moduleTime : executionTimes)
			{
				try
				{
					int hours = Integer.parseInt(moduleTime.getRuntime());

					int hourOfDay = hours / 100;

					fireTime.set(Calendar.HOUR, hourOfDay >= 12 ? hourOfDay - 12 : hourOfDay);
					fireTime.set(Calendar.MINUTE, hours % 100);
					fireTime.set(Calendar.AM_PM, hourOfDay >= 12 ? Calendar.PM : Calendar.AM);
					fireTime.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().get(Calendar.DAY_OF_YEAR));

					long currentTime = Calendar.getInstance().getTimeInMillis();

					if (currentTime > fireTime.getTimeInMillis())
						fireTime.setTimeInMillis(fireTime.getTimeInMillis() + ONE_DAY);

					gameTimer.scheduleAtFixedRate(
						new TimerTask() { @Override public void run() { ((ModuleInterface)instance).run(); } },
						fireTime.getTimeInMillis() - currentTime, ONE_DAY);

					log4j.info(name + " is schedule to run at " + moduleTime.getRuntime() + "hrs");
				}
				catch (NumberFormatException ex) {
					log4j.error("Failed to parse time " + moduleTime.getRuntime()
						+ " for module " + module.getModuleName());
				}
			}
		}
		catch (Exception e) { e.printStackTrace(); }
	}

	public MenuItem getModuleMenu() { return instance; }

	public static ModuleInterface getModuleEngine(String moduleName) { return moduleEngines.get(moduleName); }

	public void run() {}

	@Override
	public abstract Menu execute(final UserSession currentSession);
}
