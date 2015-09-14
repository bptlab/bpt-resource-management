package de.uni_potsdam.hpi.bpt.resource_management;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolRepository;

/**
 * Initializes resources that are independent from the web application.
 * 
 * @author tw
 *
 */
public class BPTServlet extends HttpServlet {
	
	private static final long serialVersionUID = -5319972187940103522L;
	private BPTToolRepository toolRepository;
	
	@Override
	public void init() throws ServletException {
//		toolRepository = BPTToolRepository.getInstance();

		// mail notifications are disabled by default - enable here at deployment
//		toolRepository.enableMailProvider();
		
		// schedules the tasks checking the entries for broken URLs and date of last update
//		new BPTTaskScheduler();
	}
}