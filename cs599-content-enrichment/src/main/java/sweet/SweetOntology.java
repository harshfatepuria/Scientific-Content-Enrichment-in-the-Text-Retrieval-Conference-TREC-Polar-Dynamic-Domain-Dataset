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
	
	private List<Concept> concepts;
	
	private void queryConcepts() {
		concepts = new ArrayList<>();
		
		List<BindingSet> results = Repositories.tupleQuery(repository, 
	             "SELECT DISTINCT ?s WHERE {{?s ?r ?o} UNION {?o ?r ?s}}", r -> QueryResults.asList(r));
		
		results.forEach(b -> {
			String value = b.getValue("s").stringValue();
			Integer indexOfHash = value.lastIndexOf("#");
			if (indexOfHash < 0) {
				return;
			}
			
			concepts.add(new Concept(value, value.substring(indexOfHash + 1).toLowerCase()));
		});
		
		System.out.println(concepts.size());
	}
	
	public List<MatchedConcept> query(String query) {
		return query(query, 0.2f);
	}
	
	public List<MatchedConcept> query(String query, Float tolerance) {
		List<MatchedConcept> matched = new ArrayList<>();
		concepts.forEach(c -> {
			Float distance = c.getDistanceFromQuery(query.trim().toLowerCase());
			
			if (distance < tolerance * c.queryName.length()) {
				matched.add(new MatchedConcept(c.fullName, distance));
			}
		});
		
		return matched.stream().sorted((a, b) -> Float.compare(a.distance, b.distance)).collect(Collectors.toList());
	}
	
	public class MatchedConcept {
		public String concept;
		public Float distance;
		
		public MatchedConcept(String c, Float d) {
			concept = c;
			distance = d;
		}
	}
	
	private class Concept {
		public String fullName;
		public String queryName;
		
		Concept(String fullName, String queryName) {
			this.fullName = fullName;
			this.queryName = queryName;
		}
		
		public float getDistanceFromQuery(String query) {
			return StringUtils.getLevenshteinDistance(queryName, query);
		}
	}
}
