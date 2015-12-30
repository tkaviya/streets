package net.blaklizt.streets.web;

import net.blaklizt.streets.core.Streets;
import net.blaklizt.streets.core.menu.Menu;
import net.blaklizt.streets.core.module.ModuleInterface;
import net.blaklizt.streets.core.session.UserSession;
import net.blaklizt.symbiosis.sym_common.configuration.Configuration;
import net.blaklizt.symbiosis.sym_common.utilities.Format;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * SymbiosisUser: tkaviya
 * Date: 2013/07/06
 * Time: 1:02 PM
 */

@Controller
public class StreetsWebController
{
	@Autowired
	private Streets streets;

	private static final Logger logger = Configuration.getNewLogger(StreetsWebController.class.getSimpleName());

	private final HashMap<String, ModuleInterface> moduleShortcuts = streets.getModuleShortcuts();

    @RequestMapping(value="/process", method = { RequestMethod.GET , RequestMethod.POST } )
    public @ResponseBody String process(
		HttpServletRequest request,
		@ModelAttribute(value="menu_response") String response,
		BindingResult br)
    {
		ModelMap model = new ModelMap();
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");

		if (!br.hasErrors())
        {
			logger.fine("Processing response: " + response);

			if (moduleShortcuts.get(response) != null)
			{
				userSession.setCurrentMenu(moduleShortcuts.get(response).execute(userSession));
			}
			else if (Menu.isValid(response, userSession.getCurrentMenu()))
			{
	            userSession.setCurrentMenu(
					userSession.getCurrentMenu().execute(Integer.parseInt(response) - 1, userSession));
			}
			else
			{
				userSession.getCurrentMenu().setErrorMessage(Format.formatBlink(Format.formatColor(
					"Invalid menu option: " +response+ "<br/>",
					Format.HTML_COLOR.RED.getColor())));
			}
        }
        else
        {
			logger.error("Failed to process response: " + response + " | " + br.getErrorCount() + " error[s] occurred");
            for (ObjectError oe : br.getAllErrors())
            {
				logger.error("--> " + oe.toString());
            }
            userSession.getCurrentMenu().setErrorMessage("Invalid response " + response);
        }
		model.addAttribute("userSession", userSession);

		return userSession.getCurrentMenu().toString();
    }

	@RequestMapping(value= "/getEvents", method = {RequestMethod.GET, RequestMethod.POST}, produces="text/event-stream")
	public @ResponseBody String getEvents(HttpServletRequest request)
	{
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		return ("data:" + userSession.getCurrentMenu().toString() + "\r\n\r\n");
	}
}