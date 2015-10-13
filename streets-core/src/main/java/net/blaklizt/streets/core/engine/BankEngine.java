package net.blaklizt.streets.core.engine;

import net.blaklizt.streets.core.CoreDaoManager;
import net.blaklizt.streets.core.menu.Menu;
import net.blaklizt.streets.core.menu.MenuItem;
import net.blaklizt.streets.core.module.ModuleInterface;
import net.blaklizt.streets.core.session.UserSession;
import net.blaklizt.streets.persistence.EventLog;
import net.blaklizt.streets.persistence.User;
import net.blaklizt.symbiosis.sym_common.configuration.Configuration;
import net.blaklizt.symbiosis.sym_common.utilities.CommonUtilities;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * SymbiosisUser: tkaviya
 * Date: 2013/07/09
 * Time: 12:27 AM
 */


public class BankEngine extends ModuleInterface {

	private static final Double INTEREST_RATE = Double.parseDouble(Configuration.getProperty("interestRate"));

    public BankEngine() { super("Bank"); }

	@Override
	public String getModuleShortcut() { return "xb"; }

	@Override
    public Menu execute(UserSession userSession)
	{
        return MenuItem.createFinalMenu("You currently have " + CommonUtilities.formatDoubleToMoney(
				userSession.getUser().getUserAttribute().getBankBalance(),
				Configuration.getProperty("currencySymbol")) + " in your bank account.",
            streets.getMainMenu(userSession), userSession);
    }

	@Override
	public void run()
	{
		logger.info("Calculating interest on all bank accounts.");
		List<User> users = CoreDaoManager.getInstance().getUserDao().findAll();

		for (User user : users)
		{
			logger.info("Processing interest for " + user.getUsername());
			Double interest = user.getUserAttribute().getBankBalance() * INTEREST_RATE;
			user.getUserAttribute().setBankBalance(user.getUserAttribute().getBankBalance() + interest);
			CoreDaoManager.getInstance().getUserAttributeDao().saveOrUpdate(user.getUserAttribute());
			CoreDaoManager.getInstance().getEventLogDao().save(
				new EventLog(null, new Date(), user.getUserID(), "Added interest of " +
				CommonUtilities.formatDoubleToMoney(interest, Configuration.getProperty("currencySymbol")) +
				" to bank account."));
		}
	}
}
