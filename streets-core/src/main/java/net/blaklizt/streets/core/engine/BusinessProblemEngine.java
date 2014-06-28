package net.blaklizt.streets.core.engine;

import net.blaklizt.streets.common.configuration.Configuration;
import net.blaklizt.streets.core.Streets;
import net.blaklizt.streets.core.event.BusinessProblemEvent;
import net.blaklizt.streets.core.menu.Menu;
import net.blaklizt.streets.core.menu.MenuItem;
import net.blaklizt.streets.core.module.ModuleInterface;
import net.blaklizt.streets.core.session.UserSession;
import net.blaklizt.streets.persistence.BusinessProblem;
import net.blaklizt.streets.persistence.EventLog;
import net.blaklizt.streets.persistence.Location;
import net.blaklizt.streets.persistence.User;
import net.blaklizt.symbiosis.sym_common.utilities.CommonUtilities;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 11/18/13
 * Time: 4:07 PM
 */
public class BusinessProblemEngine extends ModuleInterface
{
	private static final Double REDUCED_RISK_FACTOR = Double.parseDouble(Configuration.getProperty("reducedRiskFactor"));
	private static final Double EVENT_THRESHOLD = Double.parseDouble(Configuration.getProperty("eventThreshold"));

	private static HashMap<Long,List<BusinessProblemEvent>> userBusinessProblems = new HashMap<>();

	BusinessProblemEngine() { super("BusinessProblem"); }

	@PostConstruct
	public void populateUserBusinessProblems()
	{
		List<Location> locations = coreDaoManager.getLocationDao().findAll();

		log4j.info("Populating known problems for all locations");

		for (Location location : locations)
		{
			if (location.getCurrentBusinessType() != null && location.getBusinessProblemID() != null
					&& (location.getControllingGang() != null && !location.getControllingGang().getAiControlled()))
			{
				Long userID = location.getControllingGang().getGangLeaderID();

				if (userBusinessProblems.get(userID) == null)
				{
					userBusinessProblems.put(userID, new LinkedList<BusinessProblemEvent>());
				}

				BusinessProblem problem = coreDaoManager.getBusinessProblemDao().findById(location.getBusinessProblemID());

				log4j.info("Problem for: " + location.getLocationName() + ":" + problem.getProblemDescription());
				userBusinessProblems.get(userID).add(new BusinessProblemEvent(
						problem.getProblemMenuName(),
						problem.getProblemDescription(),
						problem, location));
			}
		}
	}

	@Override
	public MenuItem getModuleMenu() { return null; }

	@Override
	public void run()
	{
		if (BusinessEngine.locations == null) BusinessEngine.locations = coreDaoManager.getLocationDao().findAll();

		log4j.info("Processing Business Problems for " + BusinessEngine.locations.size() + " locations and " +
				streets.getLoggedInUsers().size() + " users");

		if (streets.getLoggedInUsers().size() < 1) return;

		for (Location location : BusinessEngine.locations)
		{
			if (location.getCurrentBusinessType() != null && location.getBusinessProblemID() == null
					&& (location.getControllingGang() != null && !location.getControllingGang().getAiControlled()))
			{
				double problemProbably;

				if (!location.getCurrentBusinessType().equals(location.getBestBusinessType()))
				{
					log4j.debug("Current business " + location.getCurrentBusinessType() +
							" is NOT the best in this location.");
					problemProbably = location.getCurrentBusiness().getRiskFactor();
				}
				else
				{
					log4j.debug("Current business " + location.getCurrentBusinessType() +
							" is the best in this location.");
					problemProbably = location.getCurrentBusiness().getRiskFactor() * REDUCED_RISK_FACTOR;
				}

				for (UserSession userSession : Streets.getLoggedInUsers())
				{
					User currentUser = userSession.getUser();
					Long userID = currentUser.getUserID();
					if (currentUser.getUserAttribute().getGangName().equals(location.getControllingGangName())
							&& location.getControllingGang().getGangLeaderID().equals(userSession.getUser().getUserID())
							&& location.getBusinessProblemID() == null)
					{
						double currentProbability = Math.random() * problemProbably;

						if (currentProbability > EVENT_THRESHOLD)
						{
							//shit!
							List<BusinessProblem> possibleProblems = coreDaoManager.getBusinessProblemDao().
									findByBusinessType(location.getCurrentBusinessType());
							if (possibleProblems != null)
							{
								double randomSelect = Math.random() * (possibleProblems.size() - 1);
								int randomProblem = CommonUtilities.round(randomSelect);

								log4j.info(possibleProblems.get(randomProblem).getProblemDescription());

								location.setBusinessProblemID(possibleProblems.get(randomProblem).getBusinessProblemID());
								coreDaoManager.getLocationDao().saveOrUpdate(location);

								coreDaoManager.getEventLogDao().save(
									new EventLog(null, new Date(), currentUser.getUserID(),
									possibleProblems.get(randomProblem).getProblemDescription()));

								if (userBusinessProblems.get(userID) == null)
								{
									userBusinessProblems.put(userID, new LinkedList<BusinessProblemEvent>());
								}
								userBusinessProblems.get(userID).add(new BusinessProblemEvent(
										possibleProblems.get(randomProblem).getProblemMenuName(),
										possibleProblems.get(randomProblem).getProblemDescription(),
										possibleProblems.get(randomProblem), location));
								userSession.setBusinessProblemEvents(userBusinessProblems.get(userID));
							}
							else
							{
								log4j.warn(location.getBestBusinessType() + " has no associated problems!");
							}
						}
					}
				}
			}
		}
	}

	public static HashMap<Long, List<BusinessProblemEvent>> getUserBusinessProblems() {
		return userBusinessProblems;
	}

	@Override
	public Menu execute(UserSession currentSession) {
		return null;
	}
}
