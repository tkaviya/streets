package net.blaklizt.streets.core.menu;

import net.blaklizt.streets.core.session.UserSession;

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

    protected static Menu createFinalMenu(final String description,
                                          final Menu previous,
										  final UserSession userSession)
    {
        Menu finalMenu;

		switch (userSession.getSessionType())
		{
			case PLAIN_TEXT: finalMenu = new DefaultMenu(userSession);	break;
			case HTML:		 finalMenu = new HTMLMenu(userSession);		break;
			default:		 finalMenu = new DefaultMenu(userSession);	break;
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
