package net.blaklizt.streets.core;

import net.blaklizt.streets.core.event.Event;
import net.blaklizt.streets.core.module.ModuleInterface;
import net.blaklizt.streets.persistence.Module;
import net.blaklizt.symbiosis.sym_common.configuration.Configuration;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * SymbiosisUser: tkaviya
 * Date: 2013/07/08
 * Time: 11:50 PM
 */

public class EventEngine {

	private Logger log4j = Configuration.getNewLogger(this.getClass().getSimpleName());

	private static List<Event> streetsEvents = new LinkedList<>();

	private static Timer clearKnownEvents = new Timer();

	private static CoreDaoManager coreDaoManager = CoreDaoManager.getInstance();

	private EventEngine()
	{
		List<Module> modules = coreDaoManager.getModuleDao().findAllActive();

		for (final Module module : modules)
		{
			log4j.info("Initializing module " + module.getModuleName());
			ModuleInterface.getModuleEngine(module.getModuleName()).scheduleRunTimes();
		}
	}

	public static List<Event> getStreetsEvents() { return streetsEvents; }

	public static void addStreetsEvent(final Event event)
	{
		//notify users of the event
		streetsEvents.add(event);

		//start a timer and clear the notification in 30 seconds
		TimerTask clearEvents = new TimerTask() { @Override public void run() { streetsEvents.remove(event); } };

		clearKnownEvents.schedule(clearEvents, new Date(new Date().getTime() + 30000));
	}
}
