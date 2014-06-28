package net.blaklizt.streets.web;

import net.blaklizt.streets.common.configuration.Configuration;
import net.blaklizt.streets.core.CoreDaoManager;
import net.blaklizt.streets.core.Streets;
import net.blaklizt.streets.core.session.UserSession;
import net.blaklizt.streets.persistence.EventLog;
import net.blaklizt.streets.persistence.dao.UserDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/12/13
 * Time: 11:05 PM
 */

@Controller
public class LoginController
{
	private static final Logger log4j = Configuration.getNewLogger(StreetsWebController.class.getSimpleName());

	@Autowired
	private Streets streets;

	@Autowired
	private UserDao userDao;

	@RequestMapping(value = "/login", method = { RequestMethod.GET , RequestMethod.POST } )
	public ModelAndView login(HttpServletRequest request, @RequestParam(value="error", required=false) boolean error)
	{
		ModelMap model = new ModelMap();
		UserSession userSession = new UserSession(null, UserSession.SessionType.HTML);
		model.addAttribute("userSession", userSession);
		request.getSession().setAttribute("userSession", userSession);
		return new ModelAndView("login/login", model);
	}

	@RequestMapping(value = "/main", method = { RequestMethod.GET , RequestMethod.POST } )
	public ModelAndView main(HttpServletRequest request)
	{
		ModelMap model = new ModelMap();

		try
		{
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			userSession.setUser(userDao.findByUsername(auth.getName()));
			userSession.setLoggedIn(true);
			userSession.setCurrentMenu(streets.getMainMenu(userSession));
			request.getSession().setAttribute("userSession", userSession);
			streets.addLoggedInUser(userSession);
			log4j.info("Instantiated new session for " + userSession.getUser().getUsername());
			Date loginDateTime = new Date();
			userSession.getUser().setLastLoginDate(loginDateTime);
			CoreDaoManager.getInstance().getEventLogDao().save(
					new EventLog(null, loginDateTime, userSession.getUser().getUserID(),
							userSession.getUser().getUsername() + " Logged in successfully"));
			return new ModelAndView("index", model);
		}
		catch (Exception ex)
		{
			return new ModelAndView("login/login", model);
		}
	}

	@RequestMapping(value = "/loginFailed", method = { RequestMethod.GET , RequestMethod.POST } )
	public ModelAndView loginFailed()
	{
		log4j.warn("Login failed");
		ModelMap model = new ModelMap();
		model.put("error", "Invalid login credentials");
		return new ModelAndView("login/login", model);
	}

	@RequestMapping(value="/logoutSuccess", method = { RequestMethod.GET , RequestMethod.POST } )
	public ModelAndView logoutSuccess()
	{
		ModelMap model = new ModelMap();
		return new ModelAndView("logout/logoutSuccess", model);
	}
}
