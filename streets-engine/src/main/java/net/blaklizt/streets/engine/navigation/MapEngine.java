package net.blaklizt.streets.engine.navigation;

import net.blaklizt.streets.common.utilities.CommonUtilities;
import net.blaklizt.streets.engine.Streets;
import net.blaklizt.streets.engine.event.BusinessProblemEvent;
import net.blaklizt.streets.engine.menu.DefaultMenu;
import net.blaklizt.streets.engine.menu.HTMLMenu;
import net.blaklizt.streets.engine.menu.Menu;
import net.blaklizt.streets.engine.menu.MenuItem;
import net.blaklizt.streets.engine.engine.BankEngine;
import net.blaklizt.streets.engine.engine.HospitalEngine;
import net.blaklizt.streets.engine.engine.LottoEngine;
import net.blaklizt.streets.engine.engine.StoreEngine;
import net.blaklizt.streets.engine.session.UserSession;
import net.blaklizt.streets.persistence.UserAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/08
 * Time: 11:18 PM
 */
public class MapEngine {

    private static MapEngine mapEngine = null;

    private MapEngine() {} //make this a singleton

    public static MapEngine getInstance()
    {
        if (mapEngine == null)
        {
            mapEngine = new MapEngine();
        }
        return mapEngine;
    }

    public Menu getMainMenu(final Streets streets, final UserSession userSession)
    {
		Menu mainMenu;
		switch (userSession.getSessionType())
		{
			case PLAIN_TEXT:	mainMenu = new DefaultMenu(streets, userSession);	break;
			case HTML:			mainMenu = new HTMLMenu(streets, userSession);		break;
			default:			mainMenu = new DefaultMenu(streets, userSession);	break;
		}

		mainMenu.setDescription(CommonUtilities.getConfiguration("welcome_text"));

		mainMenu.addItem(BankEngine.getBankEngine());
		mainMenu.addItem(HospitalEngine.getHospitalEngine());
		mainMenu.addItem(StoreEngine.getStoreEngine());
		mainMenu.addItem(LottoEngine.getLottoEngine());

		final UserAttribute userAttribute = userSession.getUser().getUserAttribute();

		for (final BusinessProblemEvent businessProblem : userSession.getBusinessProblemEvents())
		{
			boolean alreadyExists = false;
			for (MenuItem menuItem : mainMenu.getMenuItems())
			{
				if (menuItem.toString().equals(businessProblem.getName()))
				{
					alreadyExists = true;
					break;
				}
			}
			if (alreadyExists) continue;

			mainMenu.addItem(new MenuItem(businessProblem.getName())
			{
				@Override
				public Menu execute(UserSession currentSession)
				{
					Menu returnMenu = Menu.createMenu(streets, currentSession);
					final double cost = businessProblem.getBusinessProblem().getCost();

					MenuItem menuItem = new MenuItem(businessProblem.getName())
					{
						@Override
						public Menu execute(UserSession currentSession)
						{
							Double currentBalance = userAttribute.getBankBalance();

							if (currentBalance.compareTo(cost) >= 0) {
								userAttribute.setBankBalance(currentBalance - cost);
								businessProblem.getLocation().setBusinessProblemID(null); //problem solved!

								for (BusinessProblemEvent businessProblemEvent : userSession.getBusinessProblemEvents())
								{
									if (businessProblemEvent.getLocation().getLocationId().equals(
											businessProblem.getLocation().getLocationId()))
									{
										userSession.getBusinessProblemEvents().remove(businessProblemEvent);
									}
								}

								return MenuItem.createFinalMenu(streets, "The problem has been resolved for " + cost +
										". You may continue with business as usual." +
										"Current Funds: " + CommonUtilities.formatDoubleToMoney(
										userAttribute.getBankBalance(), true),
										streets.getMainMenu(currentSession), currentSession);
							} else {
								//insufficient funds
								return MenuItem.createFinalMenu(streets,
										"You have don't have enough funds to fix the problem." +
												"You need to get some money first before you fix that problem",
										streets.getMainMenu(currentSession), currentSession);

							}
						}
					};
					returnMenu.setDescription("It will cost " +
						CommonUtilities.formatDoubleToMoney(cost, true) + " to fix the problem.\r\n" +
						"You currently have " + CommonUtilities.formatDoubleToMoney(userAttribute.getBankBalance(), true));
					returnMenu.addItem(menuItem);
					returnMenu.addItem(new MenuItem("Back") {
						@Override
						public Menu execute(UserSession currentSession) {
							return streets.getMainMenu(currentSession);
						}
					});

					return returnMenu;
				}
			});
		}


		return mainMenu;
    }
}
