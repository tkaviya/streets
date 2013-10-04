package net.blaklizt.streets.engine.navigation.location;

import net.blaklizt.streets.common.utilities.CommonUtilities;
import net.blaklizt.streets.engine.Streets;
import net.blaklizt.streets.engine.menu.Menu;
import net.blaklizt.streets.engine.menu.MenuItem;
import net.blaklizt.streets.engine.session.UserSession;
import net.blaklizt.streets.persistence.UserAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/09
 * Time: 12:27 AM
 */
public class HospitalEngine extends MenuItem {

    private static Streets streets;

    private static int HEALTH_COST = Integer.parseInt(CommonUtilities.getConfiguration("healthCost"));
	private static int MAX_HEALTH = 100;

    private static HospitalEngine hospitalEngine;

    private HospitalEngine()
    {
        super("General Hospital");
		hospitalEngine = this;
    }

    public static HospitalEngine getHospitalEngine()
    {
        return hospitalEngine;
    }

    @Override
    public Menu execute(UserSession userSession) {

		final UserAttribute userAttribute = userSession.getUser().getUserAttribute();
        int currentHealth = userAttribute.getHealthPoints();

		switch (currentHealth)
        {
            case 0:
				return MenuItem.createFinalMenu(streets, "You have 0% health remaining, i.e. YOU'RE DEAD! Game Over!",
                    streets.getMainMenu(userSession), userSession);
            case 100:
				return MenuItem.createFinalMenu(streets, "You have 100% health. You don't need a hospital.",
                    streets.getMainMenu(userSession), userSession);
            default:
			{
				Menu returnMenu = Menu.createMenu(streets, userSession);

				final Integer treatmentCost = (MAX_HEALTH - currentHealth) * HEALTH_COST;

				String treatmentCostStr = CommonUtilities.formatDoubleToMoney(treatmentCost, true);
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
							return MenuItem.createFinalMenu(streets, "You have been treated for " + treatmentCost +
								".\r\n" + "Current Health: " + MAX_HEALTH + "%.\r\n" +
								"Current Funds: " + CommonUtilities.formatDoubleToMoney(
									userAttribute.getBankBalance(), true),
									streets.getMainMenu(currentSession), currentSession);
						}
						else
						{
							//insufficient funds
							return MenuItem.createFinalMenu(streets, "You have don't have enough funds to see a doctor."
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

    public void setStreets(Streets streets)
    {
        this.streets = streets;
    }
}
