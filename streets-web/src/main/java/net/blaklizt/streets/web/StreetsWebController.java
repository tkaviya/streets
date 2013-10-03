package net.blaklizt.streets.web;

import net.blaklizt.streets.common.configuration.Configuration;
import net.blaklizt.streets.common.utilities.CommonUtilities;
import net.blaklizt.streets.engine.EventEngine;
import net.blaklizt.streets.engine.Streets;
import net.blaklizt.streets.engine.event.BusinessProblemEvent;
import net.blaklizt.streets.engine.menu.Menu;
import net.blaklizt.streets.engine.menu.MenuItem;
import net.blaklizt.streets.engine.session.UserSession;
import net.blaklizt.streets.persistence.UserAttribute;
import net.blaklizt.streets.web.common.Format;
import org.apache.log4j.Logger;
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
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 2013/07/06
 * Time: 1:02 PM
 */

@Controller
public class StreetsWebController
{
    @Autowired
    private Streets streets;

	private static final Logger log4j = Configuration.getNewLogger(StreetsWebController.class.getSimpleName());

    @RequestMapping(value="/process", method = { RequestMethod.GET , RequestMethod.POST } )
    public @ResponseBody String process(
		HttpServletRequest request,
		@ModelAttribute(value="menu_response") String response,
		BindingResult br)
    {
		ModelMap model = new ModelMap();
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		//if (userSession == null) return new ModelAndView("login/login", model);

		if (!br.hasErrors())
        {
			log4j.debug("Processing response: " + response);
			if (Menu.isValid(response, userSession.getCurrentMenu()))
			{
	            userSession.setCurrentMenu(userSession.getCurrentMenu().execute(Integer.parseInt(response) - 1, userSession));
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
			log4j.error("Failed to process response: " + response + " | " + br.getErrorCount() + " error[s] occurred");
            for (ObjectError oe : br.getAllErrors())
            {
				log4j.error("--> " + oe.toString());
            }
            userSession.getCurrentMenu().setErrorMessage("Invalid response " + response);
        }
		model.addAttribute("userSession", userSession);
        //return new ModelAndView("index", model);

		return userSession.getCurrentMenu().toString().replaceAll("\r\n","<br/>");
    }

	@RequestMapping(value= "/getBusinessProblems", method = {RequestMethod.GET, RequestMethod.POST}, produces="text/event-stream")
	public @ResponseBody String getBusinessProblems(HttpServletRequest request, HttpServletResponse response)
	{
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		List<BusinessProblemEvent> businessProblems = EventEngine.getBusinessProblems(userSession);
		String messages = "";

		if (businessProblems != null)
		{
			final UserAttribute userAttribute = userSession.getUser().getUserAttribute();

			for (final BusinessProblemEvent businessProblem : businessProblems)
			{
				messages += businessProblem.getDescription()+ "<br/>";
				userSession.getCurrentMenu().addItem(new MenuItem(businessProblem.getName()) {
					@Override
					public Menu execute(UserSession currentSession) {

						Menu returnMenu = Menu.createMenu(currentSession.getSessionType());
						final double cost = businessProblem.getBusinessProblem().getCost();

						MenuItem menuItem = new MenuItem(businessProblem.getName())
						{
							@Override
							public Menu execute(UserSession currentSession)
							{
								Double currentBalance = userAttribute.getBankBalance();

								if (currentBalance.compareTo(cost) >= 0)
								{
									userAttribute.setBankBalance(currentBalance - cost);
									businessProblem.getLocation().setBusinessProblemID(null); //problem solved!

									return MenuItem.createFinalMenu("The problem has been resolved for " + cost +
											". You may continue with business as usual." +
											"Current Funds: " + CommonUtilities.formatDoubleToMoney(
											userAttribute.getBankBalance(), true),
											streets.getMainMenu(currentSession), currentSession.getSessionType());
								}
								else
								{
									//insufficient funds
									return MenuItem.createFinalMenu("You have don't have enough funds to fix the problem."
											+ "You need to get some money first before you fix that problem",
											streets.getMainMenu(currentSession), currentSession.getSessionType());

								}
							}
						};
						returnMenu.setDescription("It will cost " +
							CommonUtilities.formatDoubleToMoney(cost, true) + "to fix the problem");
						returnMenu.addItem(menuItem);
						returnMenu.addItem(new MenuItem("Back")
						{
							@Override
							public Menu execute(UserSession currentSession) {
								return streets.getMainMenu(currentSession);
							}
						});

						return returnMenu;





					}
				});
			}
		}

		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/event-stream");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(200);

		messages = Format.formatColor(Format.formatBlink(messages), Format.HTML_COLOR.ORANGE.getColor());
		return ("data:" + messages + "\r\n\r\n");
	}
}