package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.util.List;

import javax.servlet.ServletException;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
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
                // adds request handler at the beginning of list because VaadinService reverses this list
                handlers.add(new PageRefreshRequestHandler());
                return handlers;
            }
        };
        service.init();
        return service;
    }
    
    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().addSessionInitListener(new SessionInitListener() {
			private static final long serialVersionUID = 5057367721880923712L;
			@Override
            public void sessionInit(SessionInitEvent event) {
                event.getSession().addBootstrapListener(new BootstrapListener() {
					private static final long serialVersionUID = 5651095681114698532L;
					@Override
                    public void modifyBootstrapPage(BootstrapPageResponse response) {
                         response.getDocument().head().append("<script  type=\"text/javascript\" src=\"./VAADIN/themes/bpt/piwik.js\"/>");
                         response.getDocument().body().append("<noscript><p><img src=\"http://bpt.hpi.uni-potsdam.de/piwik/piwik.php?idsite=2\" style=\"border:0;\" alt=\"\" /></p></noscript>");
                    }
                    @Override
                    public void modifyBootstrapFragment(BootstrapFragmentResponse response) {
//                        // Wrap the fragment in a custom div element
//                        Element myDiv = new Element(Tag.valueOf("div"), "");
//                        List<Node> nodes = response.getFragmentNodes();
//                        for(Node node : nodes) {
//                            myDiv.appendChild(node);
//                        }
//                        nodes.clear();
//                        nodes.add(myDiv);
                    }
                }
        );
            }
        });
    }
}
