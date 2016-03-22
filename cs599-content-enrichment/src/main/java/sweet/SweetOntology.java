package sweet;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryResults;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.util.Repositories;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;


public class SweetOntology {

	private Repository repository;
	
	public SweetOntology() throws Exception {
		repository = new SailRepository(new MemoryStore());
		repository.initialize();
		loadOntology();
	}
	
	private void loadOntology() throws URISyntaxException, IOException {
		try (RepositoryConnection con = repository.getConnection()) {
			File ontologyFolder = new File(this.getClass().getResource("/sweet/ontology").toURI());
			
			Files.walk(ontologyFolder.toPath())
				.filter(Files::isRegularFile)
				.filter(p -> "owl".equals(FilenameUtils.getExtension(p.toString())))
				.filter(p -> !p.getFileName().toString().equals("sweetAll.owl"))
				.forEach(path -> {
					try {
						con.add(path.toFile(), "http://sweet.jpl.nasa.gov/2.3", RDFFormat.RDFXML);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
		}
		System.out.println("Ontology loaded");
	}
	
	public void query(String query) {
		List<BindingSet> results = Repositories.tupleQuery(repository, 
	             "SELECT * WHERE {?s ?p ?o }", r -> QueryResults.asList(r));
		
		for(int i = 0; i < 20; i++) {
			System.out.println(results.get(i).getValue("s"));
		}
	}
}
