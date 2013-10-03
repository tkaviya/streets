package net.blaklizt.streets.engine.navigation.location;

import net.blaklizt.streets.common.utilities.CommonUtilities;
import net.blaklizt.streets.engine.Streets;
import net.blaklizt.streets.engine.menu.Menu;
import net.blaklizt.streets.engine.menu.MenuItem;
import net.blaklizt.streets.engine.session.UserSession;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/09
 * Time: 12:27 AM
 */


public class BankEngine extends MenuItem {

    private static Streets streets;

    private static BankEngine bankEngine;

    private BankEngine()
    {
        super("The Bank");
		bankEngine = this;
    }

    public static BankEngine getBankEngine()
    {
        return bankEngine;
    }

    @Override
    public Menu execute(UserSession userSession) {

        return MenuItem.createFinalMenu("You currently have " + CommonUtilities.formatDoubleToMoney(
					userSession.getUser().getUserAttribute().getBankBalance().doubleValue(),true)
					+ " in your bank account.",
            streets.getMainMenu(userSession), userSession.getSessionType());
    }

    public void setStreets(Streets streets)
    {
        this.streets = streets;
    }
}
