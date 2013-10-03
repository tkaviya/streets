package net.blaklizt.streets.engine;

import net.blaklizt.streets.common.configuration.Configuration;
import net.blaklizt.streets.common.utilities.CommonUtilities;
import net.blaklizt.streets.engine.event.BusinessProblemEvent;
import net.blaklizt.streets.engine.session.UserSession;
import net.blaklizt.streets.persistence.BusinessProblem;
import net.blaklizt.streets.persistence.Location;
import net.blaklizt.streets.persistence.User;
import net.blaklizt.streets.persistence.dao.BusinessProblemDao;
import net.blaklizt.streets.persistence.dao.LocationDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/08
 * Time: 11:50 PM
 */
public class EventEngine {

	@Autowired
	private LocationDao locationDao;

	@Autowired
	private BusinessProblemDao businessProblemDao;

	private static final Double REDUCED_RISK_FACTOR =
			Double.parseDouble(Configuration.getInstance().getProperty("reducedRiskFactor"));
	private static final Double EVENT_THRESHOLD =
			Double.parseDouble(Configuration.getInstance().getProperty("eventThreshold"));

	private static final Logger log4j = Configuration.getNewLogger(EventEngine.class.getSimpleName());

	private static HashMap<Long,List<BusinessProblemEvent>> userBusinessProblems = new HashMap<>();

	public static List<BusinessProblemEvent> getBusinessProblems(UserSession userSession) {
		return userBusinessProblems.get(userSession.getUser().getUserId());
	}

	public EventEngine()
	{
		populateUserBusinessProblems();
	}

	private void populateUserBusinessProblems()
	{
		List<Location> locations = locationDao.findAll();

		log4j.info("Populating known problems for all locations");

		for (Location location : locations)
		{
			Long userID = location.getControllingGang().getGangLeaderID();
			if (location.getBusinessProblemID() != null)
			{
				if (userBusinessProblems.get(userID) == null)
				{
					userBusinessProblems.put(userID, new LinkedList<BusinessProblemEvent>());
				}

				BusinessProblem businessProblem = businessProblemDao.findById(location.getBusinessProblemID());

				userBusinessProblems.get(userID).add(new BusinessProblemEvent(
					businessProblem.getProblemMenuName(),
					businessProblem.getProblemDescription(),
					businessProblem, location));
			}
		}
	}

	public void runBusinessProblems()
	{
		List<Location> locations = locationDao.findAll();

		log4j.info("Processing " + locations.size() + " locations and " + Streets.getLoggedInUsers().size() + " users");

		if (Streets.getLoggedInUsers().size() < 1) return;

		for (Location location : locations)
		{
			log4j.info("Location: " + location.getLocationName()
					+ " |BestBusiness: " + location.getBestBusinessType()
					+ " |CurrentBusiness: " + location.getCurrentBusinessType()
					+ " |Gang: " + (location.getControllingGang() == null ? "" : location.getControllingGang().getGangName()));

			if (location.getCurrentBusinessType() != null && location.getBusinessProblemID() == null
				&& (location.getControllingGang() != null && !location.getControllingGang().getAiControlled()))
			{
				double problemProbably;

				if (!location.getCurrentBusinessType().equals(location.getBestBusinessType()))
				{
					log4j.info("Current business " + location.getCurrentBusinessType() +
							" is NOT the best in this location.");
					problemProbably = location.getCurrentBusiness().getRiskFactor();
				}
				else
				{
					log4j.info("Current business " + location.getCurrentBusinessType() +
							" is the best in this location.");
					problemProbably = location.getCurrentBusiness().getRiskFactor() * REDUCED_RISK_FACTOR;
				}

				for (UserSession userSession : Streets.getLoggedInUsers())
				{
					User currentUser = userSession.getUser();
					Long userID = currentUser.getUserId();
					if (currentUser.getUserAttribute().getGangName().equals(location.getControllingGangName())
						&& location.getControllingGang().getGangLeaderID().equals(userSession.getUser().getUserId())
						&& location.getBusinessProblemID() == null)
					{
						double currentProbability = Math.random() * problemProbably;
						log4j.info("currentProbability = " + currentProbability);

						if (currentProbability > EVENT_THRESHOLD)
						{
							//shit!
							List<BusinessProblem> possibleProblems =
									businessProblemDao.findByBusinessType(location.getCurrentBusinessType());
							if (possibleProblems != null)
							{
								log4j.info("Possible problems: " + possibleProblems.size());
								double randomSelect = Math.random() * (possibleProblems.size() - 1);

								log4j.info("randomSelect: " + randomSelect);

								int randomProblem = CommonUtilities.round(randomSelect);

								log4j.info("Getting possibleProblems at index " + randomProblem);
								log4j.info(possibleProblems.get(randomProblem).getProblemDescription());
								location.setBusinessProblemID(possibleProblems.get(randomProblem).getBusinessProblemID());

								if (userBusinessProblems.get(userID) == null)
								{
									userBusinessProblems.put(userID, new LinkedList<BusinessProblemEvent>());
								}
								userBusinessProblems.get(userID).add(new BusinessProblemEvent(
										possibleProblems.get(randomProblem).getProblemMenuName(),
										possibleProblems.get(randomProblem).getProblemDescription(),
										possibleProblems.get(randomProblem), location));
								userSession.getCurrentMenu().setBusinessProblemEvents(
										userSession, userBusinessProblems.get(userID));
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
}
