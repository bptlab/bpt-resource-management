package de.uni_potsdam.hpi.bpt.resource_management.vaadin;
//import com.google.gwt.user.client.Command;
//import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
//import com.vaadin.terminal.gwt.client.UIDL;
//import com.vaadin.terminal.gwt.client.Util;
//import com.vaadin.terminal.gwt.client.ui.VFilterSelect;
//
//
//public class BPTFilterSelect extends VFilterSelect{
//	
//	public class FilterSelectSuggestion implements Suggestion, Command {
//			
//			        private final String key;
//			        private final String caption;
//			        private String iconUri;
//			
//			        /**
//			         * Constructor
//			         *
//			         * @param uidl
//			         *            The UIDL recieved from the server
//			         */
//			        public FilterSelectSuggestion(UIDL uidl) {
//			            key = uidl.getStringAttribute("key");
//			            caption = uidl.getStringAttribute("caption");
//			            if (uidl.hasAttribute("icon")) {
//			                iconUri = client.translateVaadinUri(uidl
//			                        .getStringAttribute("icon"));
//			            }
//			        }
//			
//			        /**
//			         * Gets the visible row in the popup as a HTML string. The string
//			         * contains an image tag with the rows icon (if an icon has been
//			         * specified) and the caption of the item
//			         */
//			        public String getDisplayString() {
//			            final StringBuffer sb = new StringBuffer();
//			            if (iconUri != null) {
//			                sb.append("<img src=\"");
//			                sb.append(iconUri);
//			                sb.append("\" alt=\"\" class=\"v-icon\" />");
//			            }
//			            sb.append("<span>" + Util.escapeHTML(caption) + "</span>");
//			            return sb.toString();
//			        }
//			
//			        /**
//			         * Get a string that represents this item. This is used in the text box.
//			         */
//			        public String getReplacementString() {
//			            return caption;
//			        }
//			
//			        /**
//			         * Get the option key which represents the item on the server side.
//			         *
//			         * @return The key of the item
//			         */
//			        public int getOptionKey() {
//			            return Integer.parseInt(key);
//			        }
//			
//			        /**
//			         * Get the URI of the icon. Used when constructing the displayed option.
//			         *
//			         * @return
//			         */
//			        public String getIconUri() {
//			            return iconUri;
//			        }
//			
//			        /**
//			         * Executes a selection of this item.
//			         */
//			        public void execute() {
//			            onSuggestionSelected(this);
//			        }
//			    }
//
//}
