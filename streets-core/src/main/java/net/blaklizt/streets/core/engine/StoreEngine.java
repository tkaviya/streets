package net.blaklizt.streets.core.engine;

import net.blaklizt.streets.core.menu.Menu;
import net.blaklizt.streets.core.menu.MenuItem;
import net.blaklizt.streets.core.module.ModuleInterface;
import net.blaklizt.streets.core.session.UserSession;
import net.blaklizt.streets.persistence.*;
import net.blaklizt.symbiosis.sym_common.configuration.Configuration;
import net.blaklizt.symbiosis.sym_common.utilities.CommonUtilities;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/09
 * Time: 12:27 AM
 */
public class StoreEngine extends ModuleInterface {

	private StoreEngine() { super("Store"); }

	public String getModuleShortcut() { return "xs"; }

	@Override
	public Menu execute(UserSession userSession) {

		final User currentUser = userSession.getUser();
		final UserAttribute userAttribute = currentUser.getUserAttribute();
		final Double currentBalance = userAttribute.getBankBalance();
		String currentFunds = CommonUtilities.formatDoubleToMoney(currentBalance, Configuration.getProperty("currencySymbol"));
		Store locationStore = coreDaoManager.getStoreDao().findByLocationID(userAttribute.getLocationID());
		List<StoreItem> storeItems = coreDaoManager.getStoreItemDao().findByStoreID(locationStore.getStoreID());
		Menu returnMenu = Menu.createMenu(userSession);
		returnMenu.setDescription("Welcome to the " + locationStore.getStoreName()
				+ " store. Your bank bank balance is " + currentFunds);

		for (final StoreItem storeItem : storeItems)
		{
			String cost = CommonUtilities.formatDoubleToMoney(storeItem.getCost(), Configuration.getProperty("currencySymbol"));
			final Item item = storeItem.getItem();
			MenuItem buyItemMenu = new MenuItem(cost + " - " + item.getItemDescription())
			{
				@Override
				public Menu execute(UserSession currentSession)
				{
					if (currentBalance.compareTo(storeItem.getCost()) >= 0)
					{
						UserItem otherItem = coreDaoManager.getUserItemDao().findById(
								new UserItemPK(userAttribute.getUserID(), storeItem.getItemID()));

						if (otherItem != null && !item.getAllowMultiple())
						{
							//user already has the item
							return MenuItem.createFinalMenu(
								"You already have that item. You can only need 1 at a time.",
								streets.getMainMenu(currentSession), currentSession);
						}
						else
						{
							userAttribute.setBankBalance(currentBalance - storeItem.getCost());
							coreDaoManager.getUserAttributeDao().saveOrUpdate(userAttribute);
							coreDaoManager.getUserItemDao().saveOrUpdate(
								new UserItem(currentUser.getUserID(), storeItem.getItemID()));

							String description = "You bought a " + item.getItemName() + " for " +
								CommonUtilities.formatDoubleToMoney(storeItem.getCost(),
								Configuration.getProperty("currencySymbol"));

							coreDaoManager.getEventLogDao().save(new EventLog(null, new Date(),
								currentUser.getUserID(), description));

							return MenuItem.createFinalMenu(description,
								streets.getMainMenu(currentSession), currentSession);
						}
					}
					else
					{
						//insufficient funds
						return MenuItem.createFinalMenu("You have don't have enough funds buy that item. " +
							"Come back when you have made some more money",
							streets.getMainMenu(currentSession), currentSession);
					}
				}
			};
			returnMenu.addItem(buyItemMenu);
		}

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
