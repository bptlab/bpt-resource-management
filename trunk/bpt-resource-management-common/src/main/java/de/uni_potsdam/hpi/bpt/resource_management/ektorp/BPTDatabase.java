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
	
	private static String host = "localhost";
	private static int port = 5984;
//	private static String username = "";
//	private static String password = "";

	/**
     * Connects to CouchDB.
     * 
     * @param table the name of the database to connect to
     * @return the connection to the database
     * 
     */
	public static CouchDbConnector connect(String table) {
		HttpClient httpClient = new StdHttpClient.Builder()
									.host(host)
									.port(port)
								// 	.username(username)
								// 	.password(password)
									.build();
		
		CouchDbInstance databaseInstance = new StdCouchDbInstance(httpClient);
		CouchDbConnector database = new StdCouchDbConnector(table, databaseInstance);
		
		database.createDatabaseIfNotExists();
		
		return database;
		
	}

	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		BPTDatabase.host = host;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		BPTDatabase.port = port;
	}
	
}
