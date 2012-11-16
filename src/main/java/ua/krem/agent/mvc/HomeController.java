package ua.krem.agent.mvc;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping({"/", "/home", "/documents", "/synchronisation", "/reports"})
public class HomeController {

	@RequestMapping(method = RequestMethod.GET)
	public String showHomePage(Map<String, Object> model){
		
		return "home";
	}

	
	
}
