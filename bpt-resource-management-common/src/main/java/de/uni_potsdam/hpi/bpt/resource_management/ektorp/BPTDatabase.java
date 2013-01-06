package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

/**
 * Provides a static method to connect to CouchDB.
 * 
 * public static CouchDbConnector connect(String table)
 *
 * @author tw
 *
 */
public class BPTDatabase {

	/**
     * Connects to CouchDB.
     * 
     * @param table the name of the database to connect to
     * @return the connection to the database
     * 
     */
	public static CouchDbConnector connect(String table) {
		HttpClient httpClient = new StdHttpClient.Builder()
									.host("localhost")
									.port(5984)
								// 	.username("")
								// 	.password("")
									.build();
		
		CouchDbInstance databaseInstance = new StdCouchDbInstance(httpClient);
		CouchDbConnector database = new StdCouchDbConnector(table, databaseInstance);
		
		database.createDatabaseIfNotExists();
		
		return database;
		
	}
	
}
