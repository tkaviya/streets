package net.blaklizt.streets.core.engine;

import net.blaklizt.streets.core.EventEngine;
import net.blaklizt.streets.core.event.BusinessEvent;
import net.blaklizt.streets.core.menu.Menu;
import net.blaklizt.streets.core.module.ModuleInterface;
import net.blaklizt.streets.core.session.UserSession;
import net.blaklizt.streets.persistence.EventLog;
import net.blaklizt.streets.persistence.Gang;
import net.blaklizt.streets.persistence.Location;
import net.blaklizt.streets.persistence.UserAttribute;
import net.blaklizt.symbiosis.sym_common.configuration.Configuration;
import net.blaklizt.symbiosis.sym_core_lib.utilities.CommonUtilities;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * SymbiosisUser: tkaviya
 * Date: 2013/07/09
 * Time: 12:27 AM
 */
public class BusinessEngine extends ModuleInterface {

	public static List<Location> locations = null;

	public BusinessEngine() { super("Business"); }

	public void run()
	{
		if (locations == null) locations = coreDaoManager.getLocationDao().findAll();

		logger.info("Processing Business Rewards for " + locations.size() + " locations");

		for (Location location : locations)
		{
			final Gang controllingGang = location.getControllingGang();

			if (location.getCurrentBusinessType() != null && location.getBusinessProblemID() == null
					&& (controllingGang != null && !controllingGang.getAiControlled()))
			{
				controllingGang.setCurrentBalance(controllingGang.getCurrentBalance() +
						location.getCurrentBusiness().getPayout());

				List<UserAttribute> gangMembers =
						coreDaoManager.getUserAttributeDao().findByGangName(controllingGang.getGangName());

				logger.info("Processing payout for " + location.getLocationName() +
						" to " + gangMembers.size() + " members of " + controllingGang.getGangName());

				final Double payout = controllingGang.getPayout();

				if (gangMembers != null && controllingGang.getCurrentBalance().compareTo(gangMembers.size() * payout) >= 0)
				{
					for (UserAttribute userAttribute : gangMembers)
					{
						controllingGang.setCurrentBalance(controllingGang.getCurrentBalance() - payout);
						userAttribute.setBankBalance(userAttribute.getBankBalance() + payout);
						coreDaoManager.getUserAttributeDao().saveOrUpdate(userAttribute);
						coreDaoManager.getEventLogDao().save(
							new EventLog(null, new Date(), userAttribute.getUserID(), "Added gang payout of " +
							CommonUtilities.formatDoubleToMoney(payout, Configuration.getProperty("currencySymbol")) +
							" to bank account."));
					}
				}
				coreDaoManager.getGangDao().saveOrUpdate(controllingGang);
			}
		}
		EventEngine.addStreetsEvent(new BusinessEvent("Payout Received",
			"Payouts from businesses have been processed. Check your bank account for your latest balance. " +
			"If you didn't get a payout, make sure your gang has enough funds to pay out to all its members," +
			" and that there are currently no problems with your businesses."));

	}

	@Override
	public Menu execute(UserSession currentSession) {
		//TODO go to businesses menu
		return null;
	}
}
