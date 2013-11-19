package de.uni_potsdam.hpi.bpt.resource_management.vaadin;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

import de.uni_potsdam.hpi.bpt.resource_management.ektorp.BPTToolStatus;
import de.uni_potsdam.hpi.bpt.resource_management.vaadin.common.BPTContainerProvider;

@SuppressWarnings({"serial"})
public class BPTSmallRandomEntries extends BPTShowEntryComponent {

	private HorizontalLayout cardLayout, statisticsLayout;
	private CustomLayout layout;
	private Label numberOfEntriesLabel;
	
    private static class PieChartStreamSource implements StreamSource {
        
        private static final byte[] HTML = 
        		("<html><head><script type=\"text/javascript\" src=\"https://www.google.com/jsapi\">" + 
        		"</script><script type=\"text/javascript\">" + 
        		"google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});google.setOnLoadCallback(drawPieChart);" + 
        		"function drawPieChart() {" +
        		"var model_data = new google.visualization.DataTable();" +  
        		"model_data.addColumn('string', 'Model types'); model_data.addColumn('number', 'Tools');" + 
        		"model_data.addRows([" + 
        		BPTContainerProvider.getInstance().getTagStatisticsForJavaScriptFor("model_types") +
        		"]);" +
        		"var options = {'legend':'none', 'chartArea.width':200, 'pieSliceText': 'label', 'colors':['#00639C','#CC0000', '#FFCC00', '#330099', '#11CC11', '#FFA500', '#222222'], 'reverseCategories': true};" +
//        		, 'sliceVisibilityThreshold': 1/20, 'pieResidueSliceColor':'#ccc' -- colors:['#00639C','#CC0000', '#FFCC00', '#330099', '', '', '#222222'] , 'slices': {6: {color: '#cccccc'}
//				" 'title':'Model types',"
        		"var chart = new google.visualization.PieChart(document.getElementById('pie_chart_div'));chart.draw(model_data, options);" +
        		"google.visualization.events.addListener(chart, 'select', selectHandler);" +
        		"function selectHandler(e) {" +
        		"var selection = chart.getSelection();" +
//        		"for (var i = 0; i < selection.length; i++) {" +
        		"var item = selection[0];" +
        		"if(model_data.getFormattedValue(item.row, 0) == 'Others'){" +
//            	"alert('test');" +
//        		"parent.de.hpi.showAll(model_data.getFormattedValue(item.row, 0));" +
    			"}" +
    			"else{" +
//    			"alert(model_data.getFormattedValue(item.row, 0));" +
//    			"de.hpi.showAll('model_types');" +
				"parent.de.hpi.showAll(model_data.getFormattedValue(item.row, 0));" +
//				"document.getElementById('BPMN').click();" +
//    			"}" +
            	"}" +
          		"}" +
          		"}"+
        		"</script></head><body style=\" overflow:hidden; \">" +
        		"<div style=\" font-family:Arial; font-size:10; font-weight:bold; color:#CC0000; \"> Model types </div>" +
          		"<div id=\"pie_chart_div\" style=\"width: 240px; height: 300px; overflow:hidden;\"></div>" +
//				" <a id=\"BPMN\" target=\"_top\" href=\"javascript:de.hpi.showAll('verification of model properties')\"> test </a>" +
          		"</body></html>").getBytes();
    	
        public InputStream getStream() {
            return new ByteArrayInputStream(HTML);
        }
    }
    
    private static class BarChartStreamSource implements StreamSource {
        
        private static final byte[] HTML = 
        		("<html><head><script type=\"text/javascript\" src=\"https://www.google.com/jsapi\">" + 
				"</script><script type=\"text/javascript\">" + 
				"google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});google.setOnLoadCallback(drawPieChart);" + 
				"function drawPieChart() {var model_data = new google.visualization.DataTable();" +  
				"model_data.addColumn('string', 'Model types'); model_data.addColumn('number', 'Tools');" + 
				"model_data.addRows([" + 
				BPTContainerProvider.getInstance().getTagStatisticsForJavaScriptFor("availabilities") +
				"]);" +
				"var options = {'width':240, 'height':300, 'legend':'none'};" +
//				"'title':'Availabilities of tools', "
				"var chart = new google.visualization.BarChart(document.getElementById('pie_chart_div'));chart.draw(model_data, options);" +
        		"google.visualization.events.addListener(chart, 'select', selectHandler);" +
        		"function selectHandler() {" +
        		"var selection = chart.getSelection();" +
//        		"for (var i = 0; i < selection.length; i++) {" +
        		"var item = selection[0];" +
        		"if(model_data.getFormattedValue(item.row, 0) == 'Others'){" +
        		"parent.de.hpi.showAll(model_data.getFormattedValue(item.row, 0));" +
    			"}" +
    			"else{" +
//    			"alert(model_data.getFormattedValue(item.row, 0));" +
////    			"de.hpi.showAll('model_types');" +
				"parent.de.hpi.showAll(model_data.getFormattedValue(item.row, 0));" +
////				"document.getElementById('BPMN').click();" +
    			"}" +
            	"}" +
          		"}" +
				"</script></head><body style=\" overflow:hidden; \">" +
				"<div style=\" font-family:Arial; font-size:10; font-weight:bold; color:#CC0000; \"> Availabilities of tools </div>" +
          		"<div id=\"pie_chart_div\" style=\"width: 240px; height: 300px;\"></div></body></html>").getBytes();

        public InputStream getStream() {
        		return new ByteArrayInputStream(HTML);
        }
    }
    
//    private static class FlashCloudStreamSource implements StreamSource {
//        
//        private static final byte[] HTML = 
//        		("<html><head><script type=\"text/javascript\" src=\"http://www.google.com/jsapi\"></script>" + 
//        		"<script type=\"text/javascript\" src=\"http://word-cumulus-goog-vis.googlecode.com/svn/trunk/wordcumulus.js\"></script>" + 
//        		"<script type=\"text/javascript\" src=\"http://word-cumulus-goog-vis.googlecode.com/svn/trunk/swfobject.js\"></script>" + 
//        		"<script type=\"text/javascript\">google.load(\"visualization\", \"1\");" + 
//        		"google.setOnLoadCallback(drawVisualization);" + 
//        		"function drawVisualization()" +
//        		" {var data = google.visualization.arrayToDataTable([" + 
//        		BPTContainerProvider.getTagStatisticsForJavaScriptFor("supported_functionalities") + 
//        		"]);" + 
//        		"var vis = new gviz_word_cumulus.WordCumulus(document.getElementById('mydiv'));" + 
//        		"vis.draw(data, {text_color: '#1B699F', speed: 50, width:240, height:300});}" + 
//        		"</script></head><body style=\" overflow:hidden; \"><div id=\"mydiv\" style=\"width: 240px; height: 300px;\"></div></body></html>").getBytes();
//
//        public InputStream getStream() {
//        		return new ByteArrayInputStream(HTML);
//        }
//    }
    
    private static class StaticCloudStreamSource implements StreamSource {
        
        private static final byte[] HTML = 
        		("<html>" +
        				"<head>" +
        					"<link rel=\"stylesheet\" type=\"text/css\" href=\"http://visapi-gadgets.googlecode.com/svn/trunk/termcloud/tc.css\"/>"+
        					"<script type=\"text/javascript\" src=\"http://visapi-gadgets.googlecode.com/svn/trunk/termcloud/tc.js\"></script>" + 
			        		"<script type=\"text/javascript\" src=\"http://www.google.com/jsapi\"></script>" +
			        		"<script type=\"text/javascript\">" +
			        			"google.load(\"visualization\", \"1\");" +
				        		"google.setOnLoadCallback(draw);" + 
				        		"function draw() {" +
					        		"data = new google.visualization.DataTable();" +
					        		"data.addColumn('string', 'Label');" +
					        		"data.addColumn('number', 'Value');" + 
					        		"data.addColumn('string', 'Link');" +
					        		"data.addRows([" +
					        		BPTContainerProvider.getInstance().getTagStatisticsWithLinksForJavaScriptFor("supported_functionalities") + 
					        		"]);" +
					        		"var options = {'height': 300};" +
					        		"var outputDiv = document.getElementById('tcdiv');" +
					        		"var tc = new TermCloud(outputDiv);" + 
					        		"tc.draw(data, options);" +
					        	"}" +
//        		{text_color: '#1B699F', width:240, height:300}
        					"</script>" + 
        			"</head>" + 
        			"<body style=\" overflow:hidden; \">" +
        			"<div style=\" font-family:Arial; font-size:10; font-weight:bold; color:#CC0000; \"> Supported functionalities </div>" +
        			"<div id=\"tcdiv\" style=\"width: 240px; height: 300px; margin-top: 80px; \"></div>" + 
        			"</body>" + 
        		"</html>").getBytes();

        public InputStream getStream() {
        		return new ByteArrayInputStream(HTML);
        }
    }
	
	public BPTSmallRandomEntries(BPTApplicationUI bptApplicationUI) {
		super(bptApplicationUI);
	}

	@Override
	protected void buildLayout() {
		layout = new CustomLayout("cards");
		addComponent(layout);
		statisticsLayout = new HorizontalLayout();
		layout.addComponent(statisticsLayout, "statisticsRow");
		numberOfEntriesLabel = new Label();
		numberOfEntriesLabel.setImmediate(true);
		statisticsLayout.addComponent(numberOfEntriesLabel);
//		Map<String, Integer> tagStatistics = BPTContainerProvider.getTagStatisticFor("availabilities");
//		for(String string : tagStatistics.keySet()){
//			statistiksLayout.addComponent(new Label(string + ": " + tagStatistics.get(string)));
//		}
		cardLayout = new HorizontalLayout();
		layout.addComponent(cardLayout, "cards");
		addCharts();
		addReloadButton();
		addShowAllButton();
	}

	
	private void addReloadButton() {
		Button reloadButton = new Button("reload");
		reloadButton.addClickListener(new Button.ClickListener(){
			public void buttonClick(ClickEvent event) {
				ArrayList<BPTToolStatus> statusList = new ArrayList<BPTToolStatus>();
				statusList.add(BPTToolStatus.Published);
				show(getEntries(statusList));
			}
		});
		layout.addComponent(reloadButton, "sortSelect");
	}
	
	private void addCharts() {
		
		BrowserFrame barChartFrame = new BrowserFrame();
		barChartFrame.setWidth("240px");
		barChartFrame.setHeight("300px");
		StreamResource barChartRessource = new StreamResource(new BarChartStreamSource(), "");
		barChartRessource.setMIMEType("text/html; charset=utf-8");
		barChartFrame.setSource(barChartRessource);
		layout.addComponent(barChartFrame, "barchart");
		
		BrowserFrame pieChartFrame = new BrowserFrame();
		pieChartFrame.setWidth("240px");
		pieChartFrame.setHeight("300px");
		StreamResource res = new StreamResource(new PieChartStreamSource(), "");
		res.setMIMEType("text/html; charset=utf-8");
		pieChartFrame.setSource(res);
		layout.addComponent(pieChartFrame, "piechart");
				 
		BrowserFrame tagCloudFrame = new BrowserFrame();
		tagCloudFrame.setWidth("240px");
		tagCloudFrame.setHeight("300px");
		StreamResource res3 = new StreamResource(new StaticCloudStreamSource(), "");
		res3.setMIMEType("text/html; charset=utf-8");
		tagCloudFrame.setSource(res3);
		layout.addComponent(tagCloudFrame, "tagcloud");
	}

	private void addShowAllButton() {
		Button showAllButton = new Button("Show all entries");
		showAllButton.addClickListener(new Button.ClickListener(){
				public void buttonClick(ClickEvent event) {
					applicationUI.showAllAndRefreshSidebar(true);
				}
		});
		showAllButton.setStyleName(BaseTheme.BUTTON_LINK);
		showAllButton.addStyleName("bpt");
		addComponent(showAllButton);
	}

	@Override
	protected void showNumberOfEntries(int numberOfEntries) {
		numberOfEntriesLabel.setCaption(numberOfEntries + " entries in our database");
	}

	@Override
	protected void show(IndexedContainer entries) {
		this.cardLayout.removeAllComponents();
		for (Object id : entries.getItemIds()) {
			Item item = entries.getItem(id);
			BPTShortEntry entry = new BPTShortEntry(item, applicationUI, this);
			cardLayout.addComponent(entry);
		}
	}
	
	@Override
	protected IndexedContainer getEntries(ArrayList<BPTToolStatus> statusList) {
		return BPTContainerProvider.getInstance().getRandomEntries(3);
	}
}
