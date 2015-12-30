package net.blaklizt.streets.core.engine;

import net.blaklizt.streets.common.utilities.StreetsUtilities;
import net.blaklizt.streets.core.CoreDaoManager;
import net.blaklizt.streets.core.menu.Menu;
import net.blaklizt.streets.core.menu.MenuItem;
import net.blaklizt.streets.core.module.ModuleInterface;
import net.blaklizt.streets.core.session.UserSession;
import net.blaklizt.streets.persistence.EventLog;
import net.blaklizt.streets.persistence.User;
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
public class HospitalEngine extends ModuleInterface {

    private static int HEALTH_COST = Integer.parseInt(StreetsUtilities.getConfiguration("healthCost"));
	private static int MAX_HEALTH = 100;

    private HospitalEngine() { super("Hospital"); }

	@Override
	public String getModuleShortcut() { return "xh"; }

	@Override
	public void run()
	{
		logger.info("Resetting everyone to full health.");
		List<User> users = CoreDaoManager.getInstance().getUserDao().findAll();

		for (User user : users)
		{
			if (user.getUserAttribute().getHealthPoints() != MAX_HEALTH)
			{
				logger.info("Resetting health for " + user.getUsername());
				CoreDaoManager.getInstance().getEventLogDao().save(
					new EventLog(null, new Date(), user.getUserID(), "Resetting health from " +
					user.getUserAttribute().getHealthPoints() + " to " + MAX_HEALTH));
				user.getUserAttribute().setHealthPoints(MAX_HEALTH);
				CoreDaoManager.getInstance().getUserAttributeDao().saveOrUpdate(user.getUserAttribute());
			}
		}
	}

    @Override
    public Menu execute(UserSession userSession) {

		final UserAttribute userAttribute = userSession.getUser().getUserAttribute();
        int currentHealth = userAttribute.getHealthPoints();

		switch (currentHealth)
        {
            case 0:
				return MenuItem.createFinalMenu("You have 0% health remaining, i.e. YOU'RE DEAD! Game Over!",
                    streets.getMainMenu(userSession), userSession);
            case 100:
				return MenuItem.createFinalMenu("You have 100% health. You don't need a hospital.",
                    streets.getMainMenu(userSession), userSession);
            default:
			{
				Menu returnMenu = Menu.createMenu(userSession);

				final Integer treatmentCost = (MAX_HEALTH - currentHealth) * HEALTH_COST;

				String treatmentCostStr = CommonUtilities.formatDoubleToMoney(treatmentCost,
						Configuration.getProperty("currencySymbol"));
				String menuDescription = "You have " + currentHealth + "% health remaining. It will cost " +
						treatmentCostStr + " to get treatment.";

				MenuItem menuItem = new MenuItem("Get treatment")
				{
					@Override
					public Menu execute(UserSession currentSession)
					{
						Double currentBalance = userAttribute.getBankBalance();

						if (currentBalance.compareTo((double)treatmentCost) >= 0)
						{
							userAttribute.setBankBalance(currentBalance - treatmentCost);
							userAttribute.setHealthPoints(MAX_HEALTH);
							coreDaoManager.getUserAttributeDao().saveOrUpdate(userAttribute);

							coreDaoManager.getEventLogDao().save(new EventLog(null, new Date(),
									userAttribute.getUserID(), "You have been treated for " +
									CommonUtilities.formatDoubleToMoney(treatmentCost,
									Configuration.getProperty("currencySymbol"))));

							return MenuItem.createFinalMenu("You were treated for " + treatmentCost +
								".\r\n" + "Current Health: " + MAX_HEALTH + "%.\r\n" +
								"Current Funds: " + CommonUtilities.formatDoubleToMoney(
									userAttribute.getBankBalance(), Configuration.getProperty("currencySymbol")),
									streets.getMainMenu(currentSession), currentSession);
						}
						else
						{
							//insufficient funds
							return MenuItem.createFinalMenu("You have don't have enough funds to see a doctor."
									+ " Come back when you have made some more money",
									streets.getMainMenu(currentSession), currentSession);

						}
					}
				};
				returnMenu.setDescription(menuDescription);
				returnMenu.addItem(menuItem);
				returnMenu.addItem(new MenuItem("Back")
				{
					@Override
					public Menu execute(UserSession currentSession) {
						return streets.getMainMenu(currentSession);
					}
				});

				return returnMenu;
			}
        }
	}
}
