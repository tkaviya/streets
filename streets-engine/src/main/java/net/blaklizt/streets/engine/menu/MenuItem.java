package net.blaklizt.streets.engine.menu;

import net.blaklizt.streets.engine.Streets;
import net.blaklizt.streets.engine.session.UserSession;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/08
 * Time: 11:11 PM
 */
public abstract class MenuItem
{
    protected String name;

    public abstract Menu execute(final UserSession currentSession);

    public MenuItem(final String name)
    {
        this.name = name;
    }

    protected static Menu createFinalMenu(final Streets streets,
										  final String description,
                                          final Menu previous,
										  final UserSession userSession)
    {
        Menu finalMenu;

		switch (userSession.getSessionType())
		{
			case PLAIN_TEXT: finalMenu = new DefaultMenu(streets, userSession);	break;
			case HTML:		 finalMenu = new HTMLMenu(streets, userSession);	break;
			default:		 finalMenu = new DefaultMenu(streets, userSession);	break;
		}

        finalMenu.setDescription(description);
        finalMenu.addItem(new MenuItem("Back") {
            @Override
            public Menu execute(UserSession currentSession) {
                return previous;
            }
        });
        return finalMenu;
    }

    @Override
    public String toString() { return name; }
}
