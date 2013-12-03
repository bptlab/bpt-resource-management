package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.List;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

import de.uni_potsdam.hpi.bpt.resource_management.vaadin.utils.PageRefreshRequestHandler;

public class GoogleChartsApplicationServlet extends VaadinServlet {

	private static final long serialVersionUID = 9140715679884065420L;

//   protected void writeAjaxPageHtmlHeader(final BufferedWriter page, String title, String themeUri, HttpServletRequest request) throws IOException {
//        page.write("<script type=\"text/javascript\" src=\"googleCharts.js\"></script>");
//        super.writeAjaxPageHtmlHeader(page, title, themeUri, request);
//   }
	
    @Override
    protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
        VaadinServletService service = new VaadinServletService(this, deploymentConfiguration) {
			private static final long serialVersionUID = 4002353845450193504L;
			@Override
            protected List<RequestHandler> createRequestHandlers() throws ServiceException {
                List<RequestHandler> handlers = super.createRequestHandlers();
                // adds request handler at the beginning of list
                // because VaadinService reverses this list
                handlers.add(new PageRefreshRequestHandler());
                return handlers;
            }
        };
        service.init();
        return service;
    }
}
