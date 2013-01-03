package de.uni_potsdam.hpi.bpt.resource_management.ektorp;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;

@View(
	name = "all_documents", 
	map = "function(doc) { if (doc.type == 'BPTTool') emit(doc._id, doc); }"
	)
public class BPTToolRepository extends CouchDbRepositorySupport<BPTTool> {
	
	public BPTToolRepository(CouchDbConnector database) {
        super(BPTTool.class, database);
        initStandardDesignDocument();
	}
	
	@View(
		name = "number_of_documents", 
		map = "function(doc) { emit(\"count\", 1); }",
		reduce = "function(key, values, rereduce) { var count = 0; values.forEach(function(v) { count += 1; }); return count; }"
		)
	public int getNumberOfDocuments() {
		ViewQuery query = createQuery("number_of_documents");
		ViewResult result = db.queryView(query);
		try {
			return result.getRows().get(0).getValueAsInt();
		} catch (IndexOutOfBoundsException e) {
			return 0;
		}
	}

}
