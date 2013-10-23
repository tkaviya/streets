package net.blaklizt.streets.engine;

import net.blaklizt.streets.engine.engine.BusinessEngine;
import net.blaklizt.streets.engine.engine.LottoEngine;
import net.blaklizt.streets.engine.event.Event;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/08
 * Time: 11:50 PM
 */

//@Singleton
public class EventEngine {

	private static BusinessEngine businessEngine;

	private static LottoEngine lottoEngine;

	private static List<Event> streetsEvents = new LinkedList<>();

	private static Timer clearKnownEvents = new Timer();

	private EventEngine()
	{
		Timer theStreets = new Timer();

		TimerTask populateBusinessProblems = new TimerTask() {
			@Override public void run() { businessEngine.populateUserBusinessProblems(); }
		};

		TimerTask runLotto = new TimerTask() {
			@Override public void run() { lottoEngine.runLotto(); }
		};

		TimerTask runBusinessProblems = new TimerTask() {
			@Override public void run() { businessEngine.runBusinessProblems(); }
		};

		TimerTask runBusinessRewards = new TimerTask() {
			@Override public void run() { businessEngine.runBusinessRewards(); }
		};

		final Date now = new Date();

		theStreets.schedule(populateBusinessProblems, new Date(now.getTime() + 20000));
		theStreets.scheduleAtFixedRate(runBusinessProblems, new Date(now.getTime() + 42000), 120000);
		theStreets.scheduleAtFixedRate(runBusinessRewards, new Date(now.getTime() + 30000), 45000);
		theStreets.scheduleAtFixedRate(runLotto, new Date(now.getTime() + 60000), 60000);
	}

	public static List<Event> getStreetsEvents()
	{
		return streetsEvents;
	}

	public static void addStreetsEvent(final Event event)
	{
		//notify users of the event
		streetsEvents.add(event);

		//start a timer and clear the notification in 30 seconds

		TimerTask clearEvents = new TimerTask()
		{
			@Override public void run() { streetsEvents.remove(event); }
		};

		clearKnownEvents.schedule(clearEvents, new Date(new Date().getTime() + 30000));
	}

	public void setBusinessEngine(BusinessEngine businessEngine) {
		this.businessEngine = businessEngine;
	}

	public void setLottoEngine(LottoEngine lottoEngine) {
		this.lottoEngine = lottoEngine;
	}
}
