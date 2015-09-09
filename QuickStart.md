# Project structure #

There are two projects including the source files:

  * **`bpt-resource-management-common`**: Maven project
    * Storage and retrievement of CouchDB documents
      * Full search with Lucene
    * Functionality to send e-mail notifications
    * Servlet to schedule tasks that validate URLs
  * **`bpt-resource-management-vaadin`**: Eclipse project
    * Vaadin web application
    * OpenID login with JOpenID

We recommend to use Eclipse 3.x or higher for development.

## Frontend architecture ##

The main class structure of the user interface is listed below.

Basic structure
  * BPTApplicationUI (UI)
    * bptMainLayout.html (CustomLayout)
      * (VerticalLayout)
        * BPTSidebar (HorizontalLayout)
          * BPTSearchComponent (VerticalLayout)
            * (HorizontalLayout)
              * BPTBoxContainer (HorizontalLayout) - only when logged in
            * BPTTagSearchComponent (VerticalLayout)
              * BPTSearchTagBoxes (VerticalLayout)
                * (GridLayout)
                  * BPTSearchTag (HorizontalLayout)
            * BPTFullSearchComponent (HorizontalLayout)
          * BPTLoginComponent
            * (VerticalLayout)
              * BPTNavigationBar
                * HorizontalLayout (Find, Upload)
        * BPTMainFrame (VerticalLayout)

The pages differ in the structure that follows after BPTMainFrame:

Home page - [screenshot](http://bpt-resource-management.googlecode.com/svn/wiki/bptrm-ui-home.png)
  * BPTMainFrame (VerticalLayout)
    * BPTSmallRandomEntries (VerticalLayout)
      * randomEntries.html (CustomLayout)
        * statisticsRow (HorizontalLayout)
        * cards (HorizontalLayout)
          * shortEntry.html / BPTShortEntry (CustomLayout)

All entries page - [screenshot](http://bpt-resource-management.googlecode.com/svn/wiki/bptrm-ui-all.png)
  * BPTMainFrame (VerticalLayout)
    * BPTEntryCards (VerticalLayout)
      * entryCards.html (CustomLayout)
        * topPageSelector / BPTPageSelector (HorizontalLayout)
        * selectLayout (HorizontalLayout)
        * cards (VerticalLayout)
          * entry.html / BPTEntry (CustomLayout)
        * bottomPageSelector / BPTPageSelector (HorizontalLayout)

Single entry page - [screenshot](http://bpt-resource-management.googlecode.com/svn/wiki/bptrm-ui-single.png)
  * BPTMainFrame (VerticalLayout)
    * BPTShareableEntryContainer (VerticalLayout)
      * shareable (CustomLayout)
        * shareableEntry.html / BPTShareableEntry (CustomLayout)

Uploader page - [screenshot](http://bpt-resource-management.googlecode.com/svn/wiki/bptrm-ui-uploader.png)
  * BPTMainFrame (VerticalLayout)
    * BPTUploader (VerticalLayout)
      * BPTTagComponent (VerticalLayout)
        * BPTTagBox(VerticalLayout)
          * (GridLayout)
            * BPTSearchTag(HorizontalLayout)

# Modifying the data schema #

DataSchema modification has to be done in two Java classes:

  * `BPTDocumentTypes.java` in package `de.uni_potsdam.hpi.bpt.resource_management.ektorp`
  * `BPTVaadinResources.java` in package `de.uni_potsdam.hpi.bpt.resource_management.vaadin.common`

# Vaadin web application #

See `BPTApplicationUI.java` in package `de.uni_potsdam.hpi.bpt.resource_management.vaadin`.