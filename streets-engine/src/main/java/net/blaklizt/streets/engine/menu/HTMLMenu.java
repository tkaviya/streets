package net.blaklizt.streets.engine.menu;

import net.blaklizt.streets.common.utilities.CommonUtilities;
import net.blaklizt.streets.engine.event.BusinessProblemEvent;
import net.blaklizt.streets.engine.session.UserSession;
import net.blaklizt.streets.persistence.UserAttribute;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/28/13
 * Time: 3:45 PM
 */
public class HTMLMenu extends Menu
{
	@Override
	public String toString()
	{
		String result = (errorMessage == null || errorMessage.matches("")) ? "" : errorMessage + "<br />";
		result += description + "<br /><br />";

		for (BusinessProblemEvent businessProblemEvent : businessProblemEvents)
		{
			result += businessProblemEvent.getDescription() + "\r\n";
		}

		for (int c = 0; c < menuItems.size(); c++)
			result += c + 1 + ". " + menuItems.get(c) + "<br />";

		return result + "<br />";
	}

	@Override
	public void setBusinessProblemEvents(UserSession userSession, List<BusinessProblemEvent> businessProblemEvents) {
		{
			this.businessProblemEvents = businessProblemEvents;
			final UserAttribute userAttribute = userSession.getUser().getUserAttribute();

			for (final BusinessProblemEvent businessProblem : businessProblemEvents)
			{
				boolean alreadyExists = false;
				for (MenuItem menuItem : menuItems)
				{
					if (menuItem.name.equals(businessProblem.getName()))
					{
						alreadyExists = true;
						break;
					}
				}
				if (alreadyExists) continue;

				addItem(new MenuItem(businessProblem.getName())
				{
					@Override
					public Menu execute(UserSession currentSession)
					{
						Menu returnMenu = Menu.createMenu(currentSession.getSessionType());
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

									return MenuItem.createFinalMenu("The problem has been resolved for " + cost +
											". You may continue with business as usual." +
											"Current Funds: " + CommonUtilities.formatDoubleToMoney(
											userAttribute.getBankBalance(), true),
											streets.getMainMenu(currentSession), currentSession.getSessionType());
								} else {
									//insufficient funds
									return MenuItem.createFinalMenu("You have don't have enough funds to fix the problem."
											+ "You need to get some money first before you fix that problem",
											streets.getMainMenu(currentSession), currentSession.getSessionType());

								}
							}
						};
						returnMenu.setDescription("It will cost " +
								CommonUtilities.formatDoubleToMoney(cost, true) + "to fix the problem");
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
		}
	}

}
