package net.blaklizt.streets.core.engine;

import net.blaklizt.streets.core.EventEngine;
import net.blaklizt.streets.core.event.Event;
import net.blaklizt.streets.core.menu.Menu;
import net.blaklizt.streets.core.menu.MenuItem;
import net.blaklizt.streets.core.module.ModuleInterface;
import net.blaklizt.streets.core.session.UserSession;
import net.blaklizt.streets.persistence.EventLog;
import net.blaklizt.streets.persistence.User;
import net.blaklizt.streets.persistence.UserAttribute;
import net.blaklizt.symbiosis.sym_common.configuration.Configuration;
import net.blaklizt.symbiosis.sym_common.utilities.CommonUtilities;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/09
 * Time: 12:27 AM
 */
public class LottoEngine extends ModuleInterface {

	private static HashMap<Long, Integer> lottoEntrants = new HashMap();

	private static final Double LOTTO_TICKET_COST =
			Double.valueOf(Configuration.getProperty("lottoTicketCost"));

	public LottoEngine() { super("Lotto"); }

	@Override
	public String getModuleShortcut() { return "xl"; }

	@Override
	public Menu execute(UserSession userSession) {

		final User currentUser = userSession.getUser();
		final UserAttribute userAttribute = currentUser.getUserAttribute();
		final Double currentBalance = userAttribute.getBankBalance();
		String currentFunds = CommonUtilities.formatDoubleToMoney(currentBalance,
				Configuration.getProperty("currencySymbol"));

		Menu returnMenu = Menu.createMenu(userSession);
		final Integer numEntries = lottoEntrants.get(currentUser.getUserID());
		String winnings = CommonUtilities.formatDoubleToMoney(getCurrentStakes(true),
				Configuration.getProperty("currencySymbol"));

		returnMenu.setDescription("Your bank bank balance is " + currentFunds + ". \r\n"
			+ " Buy a ticket & stand a chance to win " + winnings
			+ " or more!\r\nYou currently have " + (numEntries == null ? "0" : numEntries) + " tickets.");

		String cost = CommonUtilities.formatDoubleToMoney(LOTTO_TICKET_COST,
				Configuration.getProperty("currencySymbol"));

		MenuItem buyTicketMenu = new MenuItem(cost + " - Buy a lotto ticket")
		{
			@Override
			public Menu execute(UserSession currentSession)
			{
				if (currentBalance.compareTo(LOTTO_TICKET_COST) >= 0)
				{
					userAttribute.setBankBalance(currentBalance - LOTTO_TICKET_COST);
					coreDaoManager.getUserAttributeDao().saveOrUpdate(userAttribute);
					coreDaoManager.getEventLogDao().save(
						new EventLog(null, new Date(), userAttribute.getUserID(), "You bought a lotto ticket for " +
							CommonUtilities.formatDoubleToMoney(LOTTO_TICKET_COST,
							Configuration.getProperty("currencySymbol"))));

					if (lottoEntrants.get(currentUser.getUserID()) != null)
					{
						int newNumEntries = numEntries + 1;
						lottoEntrants.put(currentUser.getUserID(), newNumEntries);
						return MenuItem.createFinalMenu("You have bought another lotto ticket. "
							+ "Good Luck! ", streets.getMainMenu(currentSession), currentSession);
					}
					else
					{
						lottoEntrants.put(currentUser.getUserID(), 1);
						return MenuItem.createFinalMenu("You have bought a lotto ticket. Good Luck!",
							streets.getMainMenu(currentSession), currentSession);
					}
				}
				else
				{
					//insufficient funds
					return MenuItem.createFinalMenu("You have don't have enough funds buy a lotto ticket. "
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

	@Override
	public void run()
	{
		Double currentStakes = getCurrentStakes(false);
		Vector<Long> userList = new Vector<>();

		//enter all the users into the draw
		for (Long userID : lottoEntrants.keySet())
			for (int c = 0; c < lottoEntrants.get(userID); c++)
				userList.add(userID);

		if (userList.size() > 0)
		{
			double randomSelect = Math.random() * (userList.size() - 1);
			Long winnerUserID = userList.get(CommonUtilities.round(randomSelect));
			String description = coreDaoManager.getUserDao().findById(winnerUserID).getUsername() +
				" has won the lotto of " + CommonUtilities.formatDoubleToMoney(currentStakes,
				Configuration.getProperty("currencySymbol")) + "! Congradulations!";

			UserAttribute winner = coreDaoManager.getUserAttributeDao().findById(winnerUserID);
			winner.setBankBalance(winner.getBankBalance() + currentStakes);
			coreDaoManager.getUserAttributeDao().saveOrUpdate(winner);
			coreDaoManager.getEventLogDao().save(new EventLog(null, new Date(), winnerUserID, description));

			log4j.info(description);
			EventEngine.addStreetsEvent(new Event("Lotto Results", description));
			lottoEntrants.clear();
		}
		else
		{
			String description = "There are no winners on tonight's lotto.";
			log4j.info(description);
			EventEngine.addStreetsEvent(new Event("Lotto Results", description));
			lottoEntrants.clear();
		}
	}

	private Double getCurrentStakes(boolean addAdditionalTicket)
	{
		Double currentStakes = 0.0;

		for (Long userID : lottoEntrants.keySet())
		{
			currentStakes += lottoEntrants.get(userID) * LOTTO_TICKET_COST;
		}

		/* check if we are calculating potential earning or actual earnings */
		if (addAdditionalTicket) currentStakes += LOTTO_TICKET_COST;

		/* winner always only takes a percentage of the pot! :P Hey, the house must win, it's the law! */
		currentStakes *= 0.75;

		/* minimum lotto payout is 10,000 */
		if (currentStakes < 10000) currentStakes = 10000.00;

		return currentStakes;
	}
}
