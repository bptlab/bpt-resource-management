# Requirements #

To run the BPT resource management system, you need the following  components:

  * Java 5 JDK or higher
  * Apache Tomcat 6 or higher
  * Apache CouchDB 1.2.0 or higher
  * CouchDB Lucene 0.10 or higher: [here](https://github.com/rnewson/couchdb-lucene)

# Project structure #

There are two projects including the source files:

  * **`bpt-resource-management-common`**: Maven project
    * Storage and retrievement of CouchDB documents
      * Full search with Lucene
    * Functionality to send e-mail notifications
    * Servlet to schedule tasks that validate URLs
  * **`bpt-resource-management-vaadin`**: Vaadin Eclipse project
    * Install the Vaadin plugin from http://vaadin.com/eclipse
    * Vaadin web application
    * OpenID login with JOpenID

We recommend to use Eclipse 3.x or higher for development.

# Run and deploy #

  * Deploy [CouchDB Lucene](https://github.com/rnewson/couchdb-lucene) on your Tomcat
  * In the CouchDB Configuration add to the section httpd_global_handlers the option _fti with the value {couch_httpd_proxy, handle_proxy_req, <<„http://127.0.0.1:8080">>} , the port in the url have to be the port on which your tomcat with the Lucene war file is running.
  * Modify the `bptrm.properties` in package `de.uni_potsdam.hpi.bpt.resource_management`
    * _DB\_HOST_ - host of your CouchDB
    * _DB\_PORT_ - port of your CouchDB
    * _DB\_USERNAME_ - an admin user of your CouchDB
    * _DB\_PASSWORD_ - password of the admin user of your CouchDB
    * _DEFAULT\_OPEN\_ID\_PROVIDER_ - Google or Yahoo
    * _OPENID\_RETURN\_TO_ - the URL where the application is accessed
    * _OPENID\_REALM_ - see [OpenID specification](http://openid.net/specs/openid-authentication-2_0.html#realms)
  * (optional) E-mail notifications - as mentioned in DataSchema
    * Modify the resources in package `de.uni_potsdam.hpi.bpt.resource_management.mail`
      * Configure the e-mail settings in `BPTMailUtils.java`
      * Enable the e-mail provider in `BPTServlet.java`
  * Create a JAR file from project bpt-resource-management-common and store it in `\bpt-resource-management-vaadin\WebContent\WEB-INF\lib`
  * Run the project `bpt-resource-management-vaadin` in Eclipse locally or create a WAR file from this project to deploy it on a Tomcat server
    * You may modify the `web.xml` in `\bpt-resource-management-vaadin\WebContent\WEB-INF`
      * Use templates `web_deployment.xml` and `web_development.xml`
