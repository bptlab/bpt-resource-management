package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

public class BPTSmallRandomEntries extends BPTShowEntryComponent{

	private HorizontalLayout cardLayout, statistiksLayout;
	private CustomLayout layout;
	private Label numberOfEntriesLabel;
	
    private static class ChartStreamSource implements StreamSource {
        
        private static final byte[] HTML = 
        		("<html><head><script type=\"text/javascript\" src=\"https://www.google.com/jsapi\">" + 
        		"</script><script type=\"text/javascript\">" + 
        		"google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});google.setOnLoadCallback(drawPieChart);" + 
        		"function drawPieChart() {var model_data = new google.visualization.DataTable();" +  
        		"model_data.addColumn('string', 'Model types'); model_data.addColumn('number', 'Tools');" + 
        		"model_data.addRows([" + 
        		BPTContainerProvider.getTagStatisticsForJavaScriptFor("model_types") +
        		"]);" +
        		"var options = {'title':'Model types', 'width':300, 'height':300};" + 
        		"var chart = new google.visualization.PieChart(document.getElementById('pie_chart_div'));chart.draw(model_data, options);}" +
        		"</script></head><body><div id=\"pie_chart_div\" style=\"width: 300px; height: 300px;\"></div></body></html>").getBytes();

        public InputStream getStream() {
            return new ByteArrayInputStream(HTML);
        }
    }
    
    private static class ChartStreamSource2 implements StreamSource {
        
        private static final byte[] HTML = 
        		("<html><head><script type=\"text/javascript\" src=\"https://www.google.com/jsapi\">" + 
				"</script><script type=\"text/javascript\">" + 
				"google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});google.setOnLoadCallback(drawPieChart);" + 
				"function drawPieChart() {var model_data = new google.visualization.DataTable();" +  
				"model_data.addColumn('string', 'Model types'); model_data.addColumn('number', 'Tools');" + 
				"model_data.addRows([" + 
				BPTContainerProvider.getTagStatisticsForJavaScriptFor("availabilities") +
				"]);" +
				"var options = {'title':'Availabilities of tools', 'width':300, 'height':300};" + 
				"var chart = new google.visualization.BarChart(document.getElementById('pie_chart_div'));chart.draw(model_data, options);}" +
				"</script></head><body><div id=\"pie_chart_div\" style=\"width: 300px; height: 300px;\"></div></body></html>").getBytes();

        public InputStream getStream() {
        		return new ByteArrayInputStream(HTML);
        }
    }
    
    private static class ChartStreamSource3 implements StreamSource {
        
        private static final byte[] HTML = 
        		("<html><head><script type=\"text/javascript\" src=\"http://www.google.com/jsapi\"></script>" + 
        		"<script type=\"text/javascript\" src=\"http://word-cumulus-goog-vis.googlecode.com/svn/trunk/wordcumulus.js\"></script>" + 
        		"<script type=\"text/javascript\" src=\"http://word-cumulus-goog-vis.googlecode.com/svn/trunk/swfobject.js\"></script>" + 
        		"<script type=\"text/javascript\">google.load(\"visualization\", \"1\");" + 
        		"google.setOnLoadCallback(drawVisualization);" + 
        		"function drawVisualization()" +
        		" {var data = google.visualization.arrayToDataTable([" + 
        		BPTContainerProvider.getTagStatisticsForJavaScriptFor("supported_functionalities") + 
        		"]);" + 
        		"var vis = new gviz_word_cumulus.WordCumulus(document.getElementById('mydiv'));" + 
        		"vis.draw(data, {text_color: '#1B699F', speed: 50, width:300, height:300});}" + 
        		"</script></head><body><div id=\"mydiv\"></div></body></html>").getBytes();

        public InputStream getStream() {
        		return new ByteArrayInputStream(HTML);
        }
    }
	
	public BPTSmallRandomEntries(BPTApplication application) {
		super(application);
	}

	@Override
	protected void buildLayout() {
		layout = new CustomLayout("cards");
		addComponent(layout);
		statistiksLayout = new HorizontalLayout();
		layout.addComponent(statistiksLayout, "statisticsRow");
		numberOfEntriesLabel = new Label();
		numberOfEntriesLabel.setImmediate(true);
		statistiksLayout.addComponent(numberOfEntriesLabel);
//		Map<String, Integer> tagStatistics = BPTContainerProvider.getTagStatisticFor("availabilities");
//		for(String string : tagStatistics.keySet()){
//			statistiksLayout.addComponent(new Label(string + ": " + tagStatistics.get(string)));
//		}
		cardLayout = new HorizontalLayout();
		layout.addComponent(cardLayout, "cards");
		addShowAllButton();
		addCharts();
		addReloadButton();
	}

	
	private void addReloadButton() {
		Button reloadButton = new Button("reload");
		reloadButton.addListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				ArrayList<BPTToolStatus> statusList = new ArrayList<BPTToolStatus>();
				statusList.add(BPTToolStatus.Published);
				show(getEntries(statusList));
			}
		});
		layout.addComponent(reloadButton, "sortSelect");
	}
	
	private void addCharts() {
		
		 Embedded chart = new Embedded();
		 chart.setWidth("240px");
		 chart.setHeight("300px");
		 chart.setType(Embedded.TYPE_BROWSER);
		 StreamResource res = new StreamResource(new ChartStreamSource(), "", this.application);
		 res.setMIMEType("text/html; charset=utf-8");
		 chart.setSource(res);
		 layout.addComponent(chart, "piechart");
		 
		 Embedded barChart = new Embedded();
		 barChart.setWidth("240px");
		 barChart.setHeight("300px");
		 barChart.setType(Embedded.TYPE_BROWSER);
		 StreamResource barChartRessource = new StreamResource(new ChartStreamSource2(), "", this.application);
		 barChartRessource.setMIMEType("text/html; charset=utf-8");
		 barChart.setSource(barChartRessource);
		 layout.addComponent(barChart, "barchart");
		 
		 Embedded chart3 = new Embedded();
		 chart3.setWidth("240px");
		 chart3.setHeight("300px");
		 chart3.setType(Embedded.TYPE_BROWSER);
		 StreamResource res3 = new StreamResource(new ChartStreamSource3(), "", this.application);
		 res3.setMIMEType("text/html; charset=utf-8");
		 chart3.setSource(res3);
		 layout.addComponent(chart3, "tagcloud");
	}

	private void addShowAllButton() {
		Button showAllButton = new Button("Show all entries");
		showAllButton.addListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					application.showAll();
				}
		});
		showAllButton.setStyleName(BaseTheme.BUTTON_LINK);
		showAllButton.addStyleName("bpt");
		addComponent(showAllButton);
	}

	@Override
	protected void showNumberOfEntries(int numberOfEntries) {
		numberOfEntriesLabel.setCaption(numberOfEntries + " Entries");
	}

	@Override
	protected void show(IndexedContainer entries) {
		this.cardLayout.removeAllComponents();
		for (Object id : entries.getItemIds()) {
			Item item = entries.getItem(id);
			BPTShortEntry entry = new BPTShortEntry(item, application, this);
			cardLayout.addComponent(entry);
		}
	}
	
	@Override
	protected IndexedContainer getEntries(ArrayList<BPTToolStatus> statusList) {
		return BPTContainerProvider.getInstance().getRandomEntries(3);
	}
}
