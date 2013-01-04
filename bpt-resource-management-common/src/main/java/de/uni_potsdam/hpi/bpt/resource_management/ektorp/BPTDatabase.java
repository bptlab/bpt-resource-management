package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

public class BPTDatabase {

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
