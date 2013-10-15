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

import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.PieChart;
import com.googlecode.charts4j.Slice;

public class BPTSmallRandomEntries extends BPTShowEntryComponent{

	private HorizontalLayout cardLayout, statistiksLayout;
	private CustomLayout layout;
	private Label numberOfEntriesLabel;
	
    private static class ChartStreamSource implements StreamSource {
        
    	Map<String, Integer> tagStatistics = BPTContainerProvider.getTagStatisticFor("availabilities");
    	
        private static final byte[] HTML = ("<html><head><script type=\"text/javascript\" src=\"https://www.google.com/jsapi\">" + 
        									"</script><script type=\"text/javascript\">" + 
        									"google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});google.setOnLoadCallback(drawChart);" + 
        									"function drawChart() {var data = new google.visualization.DataTable();" +  
        									"data.addColumn('string', 'Availability'); data.addColumn('number', 'Tools');" + 
        									"data.addRows([" + 
        									BPTContainerProvider.getTagStatisticsForJavaScriptFor("availabilities") +
        									"]);" +
        									"var options = {'title':'Availability of Tools', 'width':400, 'height':300};" + 
        									"var chart = new google.visualization.PieChart(document.getElementById('chart_div'));chart.draw(data, options);}" +
        									"</script></head><body><div id=\"chart_div\" style=\"width: 900px; height: 500px;\"></div></body></html>").getBytes();

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
//		Map<String, Integer> tagStatistics = BPTContainerProvider.getTagStatisticFor("availabilities");
//		List<Slice> slices = new ArrayList<Slice>();
//		Slice slice;
//		for(String key : tagStatistics.keySet()){
//			slice = Slice.newSlice(tagStatistics.get(key), key);
//			slices.add(slice);
//		}
//		
//		PieChart chart = GCharts.newPieChart(slices);
//		chart.setSize(500, 200);
//		String url = chart.toURLString();
//		layout.addComponent(new Label("piechartURL: " + url), "piechart");
//		application.getMainWindow().executeJavaScript(loadGoogleChartsString());
//		application.getMainWindow().executeJavaScript(buildPieChartString(tagStatistics));
		//get statistics from couch
		//send them to google api
		
		 Embedded chart = new Embedded();
		 chart.setWidth("1000px");
		 chart.setHeight("600px");
		 chart.setType(Embedded.TYPE_BROWSER);
		 StreamResource res = new StreamResource(new ChartStreamSource(), "", this.application);
		 res.setMIMEType("text/html; charset=utf-8");
		 chart.setSource(res);
		 layout.addComponent(chart, "piechart");
	}

//	private String buildPieChartString(Map<String, Integer> tagStatistics) {
//		StringBuilder js = new StringBuilder("var data = new google.visualization.DataTable();" + 
//				"data.addColumn('string', 'Tag');" + 
//				"data.addColumn('number', 'Occurences');" + 
//				"data.addRows([");
//		for(String key : tagStatistics.keySet()){
//			js.append("['" + key + "', " + tagStatistics.get(key).toString() + "],");
//		}
//		js.append("]);");
//		js.append("var options = {'title':'Distribution of tags for availability'," + 
//                       "'width':400," + 
//                       "'height':300};");
//		js.append("var chart = new google.visualization.PieChart(document.getElementById('piechart'));"+ 
//                       "chart.draw(data, options);");
//		return js.toString();
//	}

//	private String loadGoogleChartsString() {
//		return "<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>" + 
//				"google.load('visualization', '1.0', {'packages':['corechart']});";
//	}

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
