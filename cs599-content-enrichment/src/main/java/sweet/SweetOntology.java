package sweet;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryResults;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.util.Repositories;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

public class SweetOntology {

	private static SweetOntology instance;
	
	public static SweetOntology getInstance() throws Exception {
		if (instance == null) {
			instance = new SweetOntology();
		}
		
		return instance;
	}
	
	private Repository repository;
	
	SweetOntology() throws Exception {
		repository = new SailRepository(new MemoryStore());
		repository.initialize();
		loadOntology();
		queryConcepts();
	}
	
	private void loadOntology() throws URISyntaxException, IOException {
		try (RepositoryConnection conn = repository.getConnection()) {
			File ontologyFolder = new File(this.getClass().getResource("/sweet/ontology").toURI());
			
			Files.walk(ontologyFolder.toPath())
				.filter(Files::isRegularFile)
				.filter(p -> "owl".equals(FilenameUtils.getExtension(p.toString())))
				.filter(p -> !p.getFileName().toString().equals("sweetAll.owl"))
				.forEach(path -> {
					try {
						conn.add(path.toFile(), "http://sweet.jpl.nasa.gov/2.3", RDFFormat.RDFXML);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
		}
		System.out.println("Ontology loaded");
	}
	
	private List<String> concepts;
	
	private void queryConcepts() {
		List<BindingSet> results = Repositories.tupleQuery(repository, 
	             "SELECT DISTINCT ?s WHERE {?s rdfs:subClassOf ?o }", r -> QueryResults.asList(r));
		concepts = results.stream().map(b -> b.getValue("s").stringValue()).collect(Collectors.toList());
	}
	
	public List<MatchedConcept> query(String query) {
		List<MatchedConcept> matched = new ArrayList<>();
		concepts.forEach(c -> {
			String concept = c;
			if (c.lastIndexOf("#") >= 0) {
				concept = c.substring(c.lastIndexOf("#") + 1);
			}
			
			int distance = StringUtils.getLevenshteinDistance(query.toLowerCase(), concept.toLowerCase());
			
			if (distance < 0.2 * concept.length()) {
				matched.add(new MatchedConcept(concept, distance));
			}
		});
		
		return matched.stream().sorted((a, b) -> Integer.compare(a.distance, b.distance)).collect(Collectors.toList());
	}
	
	public class MatchedConcept {
		public String concept;
		public Integer distance;
		
		public MatchedConcept(String c, Integer d) {
			concept = c;
			distance = d;
		}
	}
}
