package net.blaklizt.streets.web;

import net.blaklizt.streets.common.configuration.Configuration;
import net.blaklizt.streets.core.Streets;
import net.blaklizt.streets.core.session.UserSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 9/17/13
 * Time: 5:29 PM
 */
public class LogoutHandler implements Filter {

	private static final Logger log4j = Configuration.getNewLogger(LogoutHandler.class.getSimpleName());

	@Autowired
	private Streets streets;

	private static LogoutHandler logoutHandler;

	private LogoutHandler() { logoutHandler = this; }

	public static LogoutHandler getLogoutHander()
	{
		return logoutHandler;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException
	{
		UserSession userSession = (UserSession)servletRequest.getAttribute("userSession");
		userSession.setLoggedIn(false);
		log4j.info("User " + userSession.getUser().getUsername() + " logged out");
		streets.removeLoggedInUser(userSession);
	}

	@Override
	public void destroy() {}
}
