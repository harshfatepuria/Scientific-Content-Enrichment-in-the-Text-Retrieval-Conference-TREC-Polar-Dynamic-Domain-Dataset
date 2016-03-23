package sweet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ner.corenlp.CoreNLPNERecogniser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import shared.TikaHtmlResultBasedParser;
import sweet.SweetOntology.MatchedConcept;

public class SweetParser extends TikaHtmlResultBasedParser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6928289547639589962L;

	public SweetParser() throws Exception {
		sweet = SweetOntology.getInstance();
	}
	
	private SweetOntology sweet;
	

	@Override
	public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context)
			throws IOException, SAXException, TikaException {
		String text = getParsedHtmlText(stream);
		List<String> entities = getEntitiesUsingNER(text);
		
		int count = 0;
		for(String entity : entities) {
			Optional<MatchedConcept> opConcept = sweet.queryFirst(entity);
			
			if(opConcept.isPresent()) {
				MatchedConcept c = opConcept.get();
				count++;
				
				metadata.add("sweet_entity_" + count, entity);
				metadata.add("sweet_concept_" + count, c.concept);
			}
		}
	}
	
	private List<String> getEntitiesUsingNER(String text) {
		CoreNLPNERecogniser nlpRecognizer = new CoreNLPNERecogniser(CoreNLPNERecogniser.NER_3CLASS_MODEL);
		Map<String, Set<String>> nerResult = nlpRecognizer.recognise(text);
		Set<String> querySet = new HashSet<String>();;
		
		for(String key : nerResult.keySet()) {
			querySet.addAll(nerResult.get(key));
		}
		
		return new ArrayList<String>(querySet);
	}

}
