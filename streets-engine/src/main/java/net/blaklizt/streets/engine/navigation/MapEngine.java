package net.blaklizt.streets.engine.navigation;

import net.blaklizt.streets.common.utilities.CommonUtilities;
import net.blaklizt.streets.engine.menu.DefaultMenu;
import net.blaklizt.streets.engine.menu.HTMLMenu;
import net.blaklizt.streets.engine.menu.Menu;
import net.blaklizt.streets.engine.menu.MenuItem;
import net.blaklizt.streets.engine.navigation.location.BankEngine;
import net.blaklizt.streets.engine.navigation.location.HospitalEngine;
import net.blaklizt.streets.engine.navigation.location.StoreEngine;
import net.blaklizt.streets.engine.session.UserSession;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/08
 * Time: 11:18 PM
 */
public class MapEngine {

    private static MapEngine mapEngine = null;

    private static Menu mainMenu = null;

    private MapEngine() {} //make this a singleton

    public static MapEngine getInstance()
    {
        if (mapEngine == null)
        {
            mapEngine = new MapEngine();
        }
        return mapEngine;
    }

	public Menu getLoginMenu(UserSession userSession)
	{
		mainMenu = new DefaultMenu();

		mainMenu.setDescription("Welcome to the streets");
		mainMenu.addItem(new MenuItem("Login") {
			@Override
			public Menu execute(UserSession currentSession) {
				//get username
				//get password
				return null;
			}
		});

		return mainMenu;
	}

    public Menu getMainMenu(UserSession userSession)
    {
		if (userSession != null)
		{
			if (mainMenu == null)
			{

				switch (userSession.getSessionType())
				{
					case PLAIN_TEXT:	mainMenu = new DefaultMenu();	break;
					case HTML:			mainMenu = new HTMLMenu();		break;
					default:			mainMenu = new DefaultMenu();	break;
				}

				mainMenu.setDescription(CommonUtilities.getConfiguration("welcome_text"));

				mainMenu.addItem(BankEngine.getBankEngine());
				mainMenu.addItem(HospitalEngine.getHospitalEngine());
				mainMenu.addItem(StoreEngine.getStoreEngine());
			}
			return mainMenu;
		}
		else return getLoginMenu(userSession);
    }
}
