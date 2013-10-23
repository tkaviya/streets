package net.blaklizt.streets.engine.engine;

import net.blaklizt.streets.common.configuration.Configuration;
import net.blaklizt.streets.common.utilities.CommonUtilities;
import net.blaklizt.streets.engine.EventEngine;
import net.blaklizt.streets.engine.Streets;
import net.blaklizt.streets.engine.event.BusinessEvent;
import net.blaklizt.streets.engine.event.BusinessProblemEvent;
import net.blaklizt.streets.engine.session.UserSession;
import net.blaklizt.streets.persistence.*;
import net.blaklizt.streets.persistence.dao.BusinessProblemDao;
import net.blaklizt.streets.persistence.dao.LocationDao;
import net.blaklizt.streets.persistence.dao.UserAttributeDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/09
 * Time: 12:27 AM
 */
public class BusinessEngine {

	@Autowired
	private LocationDao locationDao;

	@Autowired
	private UserAttributeDao userAttributeDao;

	@Autowired
	private BusinessProblemDao businessProblemDao;

	private static final Double REDUCED_RISK_FACTOR =
			Double.parseDouble(Configuration.getInstance().getProperty("reducedRiskFactor"));
	private static final Double EVENT_THRESHOLD =
			Double.parseDouble(Configuration.getInstance().getProperty("eventThreshold"));

	private static BusinessEngine businessEngine;

	private static List<Location> locations = null;

	private static final Logger log4j = Configuration.getNewLogger(BusinessEngine.class.getSimpleName());

	private static HashMap<Long,List<BusinessProblemEvent>> userBusinessProblems = new HashMap<>();


	private BusinessEngine()
	{
		businessEngine = this;
	}

	public void populateUserBusinessProblems()
	{
		List<Location> locations = locationDao.findAll();


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

				BusinessProblem businessProblem = businessProblemDao.findById(location.getBusinessProblemID());

				userBusinessProblems.get(userID).add(new BusinessProblemEvent(
						businessProblem.getProblemMenuName(),
						businessProblem.getProblemDescription(),
						businessProblem, location));
			}
		}
	}

	public void runBusinessRewards()
	{
		if (locations == null) locations = locationDao.findAll();

		log4j.info("Processing Business Rewards for " + locations.size() + " locations");

		for (Location location : locations)
		{
			final Gang controllingGang = location.getControllingGang();

			if (location.getCurrentBusinessType() != null && location.getBusinessProblemID() == null
					&& (controllingGang != null && !controllingGang.getAiControlled()))
			{
				controllingGang.setCurrentBalance(
						controllingGang.getCurrentBalance() +
								location.getCurrentBusiness().getPayout());

				List<UserAttribute> gangMembers = userAttributeDao.findByGangName(controllingGang.getGangName());

				log4j.info("Processing payout for " + location.getLocationName() +
						" to " + gangMembers.size() + " members of " + controllingGang.getGangName());

				final Double payout = controllingGang.getPayout();

				if (gangMembers != null && controllingGang.getCurrentBalance().compareTo(gangMembers.size() * payout) >= 0)
				{
					for (UserAttribute userAttribute : gangMembers)
					{
						controllingGang.setCurrentBalance(controllingGang.getCurrentBalance() - payout);
						userAttribute.setBankBalance(userAttribute.getBankBalance() + payout);
						log4j.info("New gang balance: " + controllingGang.getCurrentBalance());
						log4j.info("New user balance: " + userAttribute.getBankBalance());
					}
				}
			}
		}

	}

	//	@Schedule(hour="*/6", minute="0", second="0", persistent=false)
	public void runBusinessProblems()
	{
		if (locations == null) locations = locationDao.findAll();

		log4j.info("Processing Business Problems for " + locations.size() + " locations and " +
				Streets.getLoggedInUsers().size() + " users");

		if (Streets.getLoggedInUsers().size() < 1) return;

		for (Location location : locations)
		{
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

								System.out.println("locate businessProblemID = " + location.getBusinessProblemID());

								location.setBusinessProblemID(possibleProblems.get(randomProblem).getBusinessProblemID());

								System.out.println("locate businessProblemID = " + location.getBusinessProblemID());

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
						EventEngine.addStreetsEvent(new BusinessEvent("Payout Received",
								"Payouts from businesses have been processed. Check your bank account for your latest balance. " +
										"If you didn't get a payout, make sure your gang has enough " +
										"funds to pay out to all its members," +
										" and that there are currently no problems with your businesses."));
					}
				}
			}
		}
	}
}
