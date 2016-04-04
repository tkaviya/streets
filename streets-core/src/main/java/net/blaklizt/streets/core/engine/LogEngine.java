package net.blaklizt.streets.core.engine;

import net.blaklizt.streets.core.CoreDaoManager;
import net.blaklizt.streets.core.menu.Menu;
import net.blaklizt.streets.core.menu.MenuItem;
import net.blaklizt.streets.core.module.ModuleInterface;
import net.blaklizt.streets.core.session.UserSession;
import net.blaklizt.streets.persistence.EventLog;
import net.blaklizt.symbiosis.sym_common.configuration.Configuration;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * SymbiosisUser: tkaviya
 * Date: 2013/07/09
 * Time: 12:27 AM
 */


public class LogEngine extends ModuleInterface {

	private static final Integer MAX_LOGS = Integer.parseInt(Configuration.getProperty("maxLogResults"));
    public LogEngine() { super("Logs"); }

	@Override
    public Menu execute(UserSession userSession)
	{
		Menu returnMenu = Menu.createMenu(userSession);

		List<EventLog> eventLogs = CoreDaoManager.getInstance().getEventLogDao()
				.findCoreAndUserID(userSession.getUser().getUserID());

		String logs = "";
		SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");

		int count = 0;

		for (int c = eventLogs.size() - 1; c >= 0 && ++count <= MAX_LOGS; c--) {
			logs += sdf.format(eventLogs.get(c).getEventDate()) + " " + eventLogs.get(c).getDescription() + "\r\n";
		}

		returnMenu.setDescription(logs);

		returnMenu.addItem(new MenuItem("Back") {
			@Override public Menu execute(UserSession currentSession) {
				return streets.getMainMenu(currentSession);
			}
		});

		return returnMenu;
    }

	@Override
	public void run()
	{
		//TODO clear logs over 1 month old.
	}
}
