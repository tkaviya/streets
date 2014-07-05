package net.blaklizt.streets.web;

import net.blaklizt.streets.common.utilities.StreetsUtilities;
import net.blaklizt.streets.core.CoreDaoManager;
import net.blaklizt.streets.core.session.UserSession;
import net.blaklizt.streets.persistence.EventLog;
import net.blaklizt.streets.persistence.User;
import net.blaklizt.streets.persistence.UserAttribute;
import net.blaklizt.symbiosis.sym_authentication.security.Security;
import net.blaklizt.symbiosis.sym_common.configuration.Configuration;
import net.blaklizt.symbiosis.sym_common.mail.EMailer;
import net.blaklizt.symbiosis.sym_common.utilities.CommonUtilities;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
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
public class RegistrationController
{
	private static final Logger log4j = Configuration.getNewLogger(RegistrationController.class.getSimpleName());

	@RequestMapping(value = "/register", method = { RequestMethod.GET , RequestMethod.POST } )
	public ModelAndView register(HttpServletRequest request, @RequestParam(value="error", required=false) boolean error)
	{
		ModelMap model = new ModelMap();
//		if (request.getSession().getAttribute("userSession") == null)
//		{
			UserSession userSession = new UserSession(null, UserSession.SessionType.HTML);
			model.addAttribute("userSession", userSession);
			request.getSession().setAttribute("userSession", userSession);
			return new ModelAndView("register/register", model);
//		}
//		else return new ModelAndView("redirect:/main", model);
	}

	@RequestMapping(value = "/doRegister", method = { RequestMethod.GET , RequestMethod.POST } )
	public ModelAndView doRegister(
		@ModelAttribute(value="r_name") String name,
		@ModelAttribute(value="r_username") String username,
		@ModelAttribute(value="r_password1") String password1,
		@ModelAttribute(value="r_password2") String password2,
		@ModelAttribute(value="r_email") String email)
	{
		log4j.warn("Processing registration: {name="+name+"} {username="+username+"} {email="+email+"}");
		boolean registerSuccess = true;
		ModelMap model = new ModelMap();
		String error = "";

		if (name == null || name.equals("")) { registerSuccess = false; error = "Name is invalid! " + name; }
		else if (username == null || username.equals("")) { registerSuccess = false; error = "Username is invalid! " + username; }
		else if (password1 == null || password1.equals("")) { registerSuccess = false; error = "Password is invalid!"; }
		else if (password2 == null || password2.equals("")) { registerSuccess = false; error = "Password is invalid!"; }
		else if (!password2.equals(password1)) { registerSuccess = false; error = "Passwords do not match"; }
		else if (email == null || !CommonUtilities.isValidEmail(email)) { registerSuccess = false; error = "Email is invalid!"; }
		else if (CoreDaoManager .getInstance().getUserDao().findByUsername(username) != null)
		{
			registerSuccess = false;
			error = "Username is already registered!";
		}
		else if (CoreDaoManager .getInstance().getUserDao().findByEmail(email) != null)
		{
			registerSuccess = false;
			error = "Email is already registered!";
		}

		if (!registerSuccess)
		{
			log4j.error("Registration failed: " + error);
			model.put("error", error);
			return new ModelAndView("register/register", model);
		}
		else
		{
			try
			{
				CoreDaoManager cdm = CoreDaoManager.getInstance();

				log4j.error("Creating new user");
				User newUser = new User();
				newUser.setName(name);
				newUser.setUsername(username);
				newUser.setPassword(Security.encrypt(password1.getBytes()));
				newUser.setEmail(email);
				newUser.setSalt(Security.generateIV());
				newUser.setStatus(User.UserStatus.ACTIVE.getValue());
				newUser.setUserGroupID(Configuration.getProperty("defaultGroup"));

				UserAttribute newUserAttribute = new UserAttribute();
				newUserAttribute.setBankBalance(Double.parseDouble(Configuration.getProperty("initialBalance")));
				newUserAttribute.setHealthPoints(Integer.parseInt(Configuration.getProperty("initialHealth")));
				newUserAttribute.setLocationID(Long.parseLong(Configuration.getProperty("initialLocation")));
				newUserAttribute.setLocation(cdm.getLocationDao().findById(newUserAttribute.getLocationID()));

				newUser.setUserAttribute(newUserAttribute);
				cdm.getUserDao().saveOrUpdate(newUser);
				cdm.getUserDao().refresh(newUser);

				newUserAttribute.setUserID(newUser.getUserID());
				cdm.getUserAttributeDao().saveOrUpdate(newUserAttribute);

				new EMailer(new String[] {email},"Welcome","Welcome",
						StreetsUtilities.getConfiguration("emailFrom"),
						StreetsUtilities.getConfiguration("mail.smtp.host"),
						EMailer.DEFAULT_CONTENT_TYPE).start();

				cdm.getEventLogDao().save(new EventLog(null, new Date(), newUser.getUserID(),
						username + " registered successfully with email " + email));

				log4j.info("Registration succeeded");
				return new ModelAndView("register/registerSuccess", model);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				log4j.error("Registration failed: " + e.getMessage());
				model.put("error", e.getMessage());
				return new ModelAndView("register/register", model);
			}
		}
	}
}

