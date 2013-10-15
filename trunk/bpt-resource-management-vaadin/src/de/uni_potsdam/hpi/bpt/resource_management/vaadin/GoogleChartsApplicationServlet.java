package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.vaadin.terminal.gwt.server.ApplicationServlet;

public class GoogleChartsApplicationServlet extends ApplicationServlet{

    protected void writeAjaxPageHtmlHeader(final BufferedWriter page, String title, String themeUri, HttpServletRequest request) throws IOException {
        page.write("<script type=\"text/javascript\" src=\"googleCharts.js\"></script>");
        super.writeAjaxPageHtmlHeader(page, title, themeUri, request);
   }
}
