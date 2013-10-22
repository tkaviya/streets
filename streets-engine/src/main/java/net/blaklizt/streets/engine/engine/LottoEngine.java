package net.blaklizt.streets.engine.engine;

import net.blaklizt.streets.common.configuration.Configuration;
import net.blaklizt.streets.common.utilities.CommonUtilities;
import net.blaklizt.streets.engine.EventEngine;
import net.blaklizt.streets.engine.Streets;
import net.blaklizt.streets.engine.event.Event;
import net.blaklizt.streets.engine.menu.Menu;
import net.blaklizt.streets.engine.menu.MenuItem;
import net.blaklizt.streets.engine.session.UserSession;
import net.blaklizt.streets.persistence.User;
import net.blaklizt.streets.persistence.UserAttribute;
import net.blaklizt.streets.persistence.dao.UserAttributeDao;
import net.blaklizt.streets.persistence.dao.UserDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/09
 * Time: 12:27 AM
 */
public class LottoEngine extends MenuItem {

	@Autowired
	private UserAttributeDao userAttributeDao;

	@Autowired
	private UserDao userDao;

	private static Streets streets;

	private static LottoEngine lottoEngine;

	private static HashMap<Long, Integer> lottoEntrants = new HashMap();

	private static final Double LOTTO_TICKET_COST =
			Double.valueOf(Configuration.getInstance().getProperty("lottoTicketCost"));

	private static final Logger log4j = Configuration.getNewLogger(EventEngine.class.getSimpleName());

	private LottoEngine()
	{
		super("Lotto");
		lottoEngine = this;
	}

	public static LottoEngine getLottoEngine()
	{
		return lottoEngine;
	}

	@Override
	public Menu execute(UserSession userSession) {

		final User currentUser = userSession.getUser();
		final UserAttribute userAttribute = currentUser.getUserAttribute();
		final Double currentBalance = userAttribute.getBankBalance();
		String currentFunds = CommonUtilities.formatDoubleToMoney(currentBalance, true);

		Double currentStakes = getCurrentStakes();

		//Store locationStore = storeDao.findByLocationID(userAttribute.getLocationID());
		//List<StoreItem> storeItems = storeItemDao.findByStoreID(locationStore.getStoreID());
		Menu returnMenu = Menu.createMenu(streets, userSession);
		final Integer numEntries = lottoEntrants.get(currentUser.getUserId());

		returnMenu.setDescription("Your bank bank balance is " + currentFunds + ". "
			+ " This is the place where your dreams can come true! LOTTO! One day is one day!"
			+ " Buy a lotto ticket and stand a chance to win up to "
			+ CommonUtilities.formatDoubleToMoney((0.75 * currentStakes + LOTTO_TICKET_COST), true)
			+ " or more! You currently have " + (numEntries == null ? "0" : numEntries) + " tickets. "
			+ "\r\n\r\nThe more tickets you buy, the higher your chances of winning. Bandz Make Her Dance!");

		String cost = CommonUtilities.formatDoubleToMoney(LOTTO_TICKET_COST, true);

		MenuItem buyTicketMenu = new MenuItem(cost + " - Buy a lotto ticket")
		{
			@Override
			public Menu execute(UserSession currentSession)
			{
				if (currentBalance.compareTo(LOTTO_TICKET_COST) >= 0)
				{
					userAttribute.setBankBalance(currentBalance - LOTTO_TICKET_COST);

					if (lottoEntrants.get(currentUser.getUserId()) != null)
					{
						int newNumEntries = numEntries + 1;
						lottoEntrants.put(currentUser.getUserId(), newNumEntries);
						return MenuItem.createFinalMenu(streets, "You have bought another lotto ticket. "
							+ "Good Luck! ", streets.getMainMenu(currentSession), currentSession);
					}
					else
					{
						lottoEntrants.put(currentUser.getUserId(), 1);					}
						return MenuItem.createFinalMenu(streets, "You have bought a lotto ticket. Good Luck!",
							streets.getMainMenu(currentSession), currentSession);
					}
				else
				{
					//insufficient funds
					return MenuItem.createFinalMenu(streets, "You have don't have enough funds buy a lotto ticket. "
						+ "Come back when you have made some more money",
						streets.getMainMenu(currentSession), currentSession);
				}
			}
		};
		returnMenu.addItem(buyTicketMenu);

		returnMenu.addItem(new MenuItem("Back")
		{
			@Override
			public Menu execute(UserSession currentSession) {
				return streets.getMainMenu(currentSession);
			}
		});

		return returnMenu;

	}

	public void runLotto()
	{
		Double currentStakes = getCurrentStakes();
		Vector<Long> userList = new Vector<>();

		//enter all the users into the draw
		for (Long userID : lottoEntrants.keySet())
			for (int c = 0; c < lottoEntrants.get(userID); c++)
				userList.add(userID);

		log4j.info("Possible problems: " + userList.size());
		double randomSelect = Math.random() * (userList.size() - 1);
		Long winnerUserID = userList.get(CommonUtilities.round(randomSelect));

		if (winnerUserID != null)
		{
			String description = userDao.findById(winnerUserID).getUsername() + " has won the lotto of " +
				CommonUtilities.formatDoubleToMoney(currentStakes,true) + "! Congradulations!";

			log4j.info(description);
			userAttributeDao.findById(winnerUserID).setBankBalance(
					userAttributeDao.findById(winnerUserID).getBankBalance() + currentStakes);
			EventEngine.addStreetsEvent(new Event("Lotto Results", description));
		}
	}

	private Double getCurrentStakes()
	{
		Double currentStakes = 0.0;

		for (Long userID : lottoEntrants.keySet())
		{
			currentStakes += lottoEntrants.get(userID) * LOTTO_TICKET_COST;
		}

		/* minimum lotto payout is 10,000 */
		if (currentStakes < 10000) currentStakes = 10000.00;

		return currentStakes;
	}

	public void setStreets(Streets streets)
	{
		this.streets = streets;
	}
}