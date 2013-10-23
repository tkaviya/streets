package net.blaklizt.streets.engine.engine;

import net.blaklizt.streets.common.utilities.CommonUtilities;
import net.blaklizt.streets.engine.Streets;
import net.blaklizt.streets.engine.menu.Menu;
import net.blaklizt.streets.engine.menu.MenuItem;
import net.blaklizt.streets.engine.session.UserSession;
import net.blaklizt.streets.persistence.*;
import net.blaklizt.streets.persistence.dao.StoreDao;
import net.blaklizt.streets.persistence.dao.StoreItemDao;
import net.blaklizt.streets.persistence.dao.UserAttributeDao;
import net.blaklizt.streets.persistence.dao.UserItemDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/09
 * Time: 12:27 AM
 */
public class StoreEngine extends MenuItem {

	@Autowired
	private StoreDao storeDao;

	@Autowired
	private StoreItemDao storeItemDao;

	@Autowired
	private UserAttributeDao userAttributeDao;

	@Autowired
	private UserItemDao userItemDao;

	private static Streets streets;

	private static StoreEngine storeEngine;

	private StoreEngine()
	{
		super("Corner Store");
		storeEngine = this;
	}

	public static StoreEngine getStoreEngine()
	{
		return storeEngine;
	}

	@Override
	public Menu execute(UserSession userSession) {

		final User currentUser = userSession.getUser();
		final UserAttribute userAttribute = currentUser.getUserAttribute();
		final Double currentBalance = userAttribute.getBankBalance();
		String currentFunds = CommonUtilities.formatDoubleToMoney(currentBalance, true);
		Store locationStore = storeDao.findByLocationID(userAttribute.getLocationID());
		List<StoreItem> storeItems = storeItemDao.findByStoreID(locationStore.getStoreID());
		Menu returnMenu = Menu.createMenu(streets, userSession);
		returnMenu.setDescription("Welcome to the " + locationStore.getStoreName()
				+ " store. Your bank bank balance is " + currentFunds);

		for (final StoreItem storeItem : storeItems)
		{
			String cost = CommonUtilities.formatDoubleToMoney(storeItem.getCost(), true);
			final Item item = storeItem.getItem();
			MenuItem buyItemMenu = new MenuItem(cost + " - " + item.getItemDescription())
			{
				@Override
				public Menu execute(UserSession currentSession)
				{
					if (currentBalance.compareTo(storeItem.getCost()) >= 0)
					{
						UserItem otherItem = userItemDao.findById(
								new UserItemPK(userAttribute.getUserID(), storeItem.getItemID()));

						if (otherItem != null && !item.getAllowMultiple())
						{
							//user already has the item
							return MenuItem.createFinalMenu(streets,
									"You already have that item. You can only need 1 at a time.",
									streets.getMainMenu(currentSession), currentSession);
						}
						else
						{
							userAttribute.setBankBalance(currentBalance - storeItem.getCost());
							userAttributeDao.saveOrUpdate(userAttribute);
							userItemDao.saveOrUpdate(new UserItem(currentUser.getUserId(), storeItem.getItemID()));
							return MenuItem.createFinalMenu(streets, "You have bought a " + item.getItemName(),
									streets.getMainMenu(currentSession), currentSession);
						}
					}
					else
					{
						//insufficient funds
						return MenuItem.createFinalMenu(streets, "You have don't have enough funds buy that item. " +
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

	public void setStreets(Streets streets)
	{
		this.streets = streets;
	}
}
