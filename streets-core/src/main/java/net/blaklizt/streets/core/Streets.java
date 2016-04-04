package net.blaklizt.streets.core;

import net.blaklizt.streets.common.utilities.StreetsUtilities;
import net.blaklizt.streets.core.engine.BusinessProblemEngine;
import net.blaklizt.streets.core.event.BusinessProblemEvent;
import net.blaklizt.streets.core.menu.DefaultMenu;
import net.blaklizt.streets.core.menu.HTMLMenu;
import net.blaklizt.streets.core.menu.Menu;
import net.blaklizt.streets.core.menu.MenuItem;
import net.blaklizt.streets.core.module.ModuleInterface;
import net.blaklizt.streets.core.session.UserSession;
import net.blaklizt.streets.persistence.UserAttribute;
import net.blaklizt.streets.persistence.dao.LocationDao;
import net.blaklizt.streets.persistence.dao.UserAttributeDao;
import net.blaklizt.symbiosis.sym_common.configuration.Configuration;
import net.blaklizt.symbiosis.sym_core_lib.utilities.CommonUtilities;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class Streets
{
	@Autowired
	LocationDao locationDao;

	@Autowired
	UserAttributeDao userAttributeDao;

	private static LinkedList<UserSession> loggedInUsers = new LinkedList<>();

	private static Timer streetsEventsTimer = new Timer();

	private static HashMap<String, ModuleInterface> moduleShortcuts = new HashMap<>();

	private static Vector<ModuleInterface> loadedModules = new Vector<>();

	private static Streets streets;

	public static Streets getInstance()
	{
		if (streets == null) streets = new Streets();
		return streets;
	}
	
	private Streets() {}

	public Menu getMainMenu(UserSession userSession)
    {
        if (userSession != null && userSession.isLoggedIn())
		{
			Menu mainMenu;
			switch (userSession.getSessionType())
			{
				case PLAIN_TEXT:	mainMenu = new DefaultMenu(userSession);	break;
				case HTML:			mainMenu = new HTMLMenu(userSession);		break;
				default:			mainMenu = new DefaultMenu(userSession);	break;
			}

			mainMenu.setDescription(StreetsUtilities.getConfiguration("welcome_text"));

			for (ModuleInterface moduleInterface : loadedModules)
			{
				if (moduleInterface.getModuleMenu() != null) mainMenu.addItem(moduleInterface.getModuleMenu());
			}

			final UserAttribute userAttribute = userSession.getUser().getUserAttribute();

			List<BusinessProblemEvent> businessProblemEventList =
					BusinessProblemEngine.getUserBusinessProblems().get(userSession.getUser().getUserID());

			if (businessProblemEventList != null) userSession.setBusinessProblemEvents(businessProblemEventList);

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
						Menu returnMenu = Menu.createMenu(currentSession);
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

									userAttributeDao.saveOrUpdate(userAttribute);
									locationDao.saveOrUpdate(businessProblem.getLocation());

									for (BusinessProblemEvent businessProblemEvent : currentSession.getBusinessProblemEvents())
									{
										if (businessProblemEvent.getLocation().getLocationId().equals(
												businessProblem.getLocation().getLocationId()))
										{
											currentSession.getBusinessProblemEvents().remove(businessProblemEvent);
										}
									}

									return MenuItem.createFinalMenu("The problem has been resolved for " + cost +
											". You may continue with business as usual." +
											"Current Funds: " + CommonUtilities.formatDoubleToMoney(
													userAttribute.getBankBalance(), Configuration.getProperty("currencySymbol")),
											getMainMenu(currentSession), currentSession);
								} else {
									//insufficient funds
									return MenuItem.createFinalMenu(
											"You have don't have enough funds to fix the problem." +
													"You need to get some money first before you fix that problem",
											getMainMenu(currentSession), currentSession);

								}
							}
						};
						returnMenu.setDescription("It will cost " +
								CommonUtilities.formatDoubleToMoney(cost, Configuration.getProperty("currencySymbol")) +
								" to fix the problem.\r\n" + "You currently have " + CommonUtilities.formatDoubleToMoney(
									userAttribute.getBankBalance(), Configuration.getProperty("currencySymbol")));
						returnMenu.addItem(menuItem);
						returnMenu.addItem(new MenuItem("Back") {
							@Override
							public Menu execute(UserSession currentSession) {
								return getMainMenu(currentSession);
							}
						});

						return returnMenu;
					}
				});
			}
			return mainMenu;
		}
		return null;
    }

	public static Timer getStreetsEventsTimer() { return streetsEventsTimer; }

	public static LinkedList<UserSession> getLoggedInUsers() { return loggedInUsers; }

	public void addLoggedInUser(UserSession userSession) { loggedInUsers.add(userSession); }

	public void removeLoggedInUser(UserSession userSession) { loggedInUsers.remove(userSession); }

	public static HashMap<String, ModuleInterface> getModuleShortcuts() { return moduleShortcuts; }

	public static void addModule(ModuleInterface module)
	{
		if (module.getModuleShortcut() != null)
			if (moduleShortcuts.get(module.getModuleShortcut()) == null)
				moduleShortcuts.put(module.getModuleShortcut(), module);
		loadedModules.add(module);
	}
}
