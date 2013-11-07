package de.uni_potsdam.hpi.bpt.resource_management.vaadin.utils;

import java.io.IOException;
import java.util.Map;

import com.vaadin.server.RequestHandler;
import com.vaadin.server.SynchronizedRequestHandler;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.communication.PortletUIInitHandler;
import com.vaadin.server.communication.ServletUIInitHandler;
import com.vaadin.server.communication.UIInitHandler;
import com.vaadin.ui.UI;

public class PageRefreshRequestHandler extends SynchronizedRequestHandler {
	
	private static final long serialVersionUID = -5507035573596479332L;

	@Override
	public boolean synchronizedHandleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response) throws IOException {
		if (isInitRequest(session, request)) {
			// find retained window
			UIClassSelectionEvent classSelectionEvent = new UIClassSelectionEvent(request);
			for (UIProvider p : session.getUIProviders()) {
				Class<? extends UI> uiClass = p.getUIClass(classSelectionEvent);
				if (uiClass != null) {
					String windowName = request.getParameter("v-wn");
					Map<String, Integer> retainOnRefreshUIs = session.getPreserveOnRefreshUIs();
					if (windowName != null && !retainOnRefreshUIs.isEmpty()) {
						Integer retainedUIId = retainOnRefreshUIs.get(windowName);
						if (retainedUIId != null) {
							UI retainedUI = session.getUIById(retainedUIId);
							if (uiClass.isInstance(retainedUI) && PageRefreshListener.class.isInstance(retainedUI)) {
								PageRefreshListener listener = (PageRefreshListener) retainedUI;
								listener.pageRefreshed(request);
							}
						}
					}
					break;
				}
			}
		}
		// do not block other request handlers
		return false;
	}
	
	// check if it is init request
	private boolean isInitRequest(VaadinSession session, VaadinRequest request) {
		UIInitHandler handler = findUiInitHandler(session);
		if (ServletUIInitHandler.class.isInstance(handler)) {
			return ServletUIInitHandler.isUIInitRequest(request);
		}
		if (PortletUIInitHandler.class.isInstance(handler)) {
			return PortletUIInitHandler.isUIInitRequest(request);
		}
		return false;
	}
	
	private UIInitHandler findUiInitHandler(VaadinSession session) {
		for (RequestHandler requestHandler : session.getService().getRequestHandlers()) {
			if (UIInitHandler.class.isInstance(requestHandler)) {
				return (UIInitHandler) requestHandler;
			}
		}
		return null;
    }
}