package shared;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.xml.sax.SAXException;

public abstract class TikaHtmlResultBasedParser extends AbstractParser {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4021371009834458713L;
	
	private Tika tika = new Tika();

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return tika.getParser().getSupportedTypes(context);
	}
	
	public String getParsedHtmlText(InputStream stream) throws IOException, SAXException, TikaException {
		ToHTMLContentHandler contentHandler = new ToHTMLContentHandler();
		Metadata metadata = new Metadata();
		ParseContext parseContext = new ParseContext();
		
		tika.getParser().parse(stream, contentHandler, metadata, parseContext);
		return contentHandler.toString();
	}
	
	public Tika getTika() {
		return tika;
	}

	public void setTika(Tika tika) {
		this.tika = tika;
	}
	
	
}
