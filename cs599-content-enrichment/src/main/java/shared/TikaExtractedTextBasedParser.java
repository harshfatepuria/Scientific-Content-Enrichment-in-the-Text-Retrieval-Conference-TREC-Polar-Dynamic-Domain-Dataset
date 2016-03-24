package shared;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.SAXException;

public abstract class TikaExtractedTextBasedParser extends AbstractParser {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4021371009834458713L;
	
	private Tika tika = new Tika();

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return tika.getParser().getSupportedTypes(context);
	}
	
	public String getParsedText(InputStream stream) throws IOException, SAXException, TikaException {
		return getParsedText(stream, new Metadata());
	}
	
	public String getParsedText(InputStream stream, Metadata metadata) throws IOException, SAXException, TikaException {
		if (metadata == null) {
			metadata = new Metadata();
		}

		StringBuffer sb = new StringBuffer();
		String text = tika.parseToString(stream, metadata);
		
		/* Extract metadata */
		for(String name : metadata.names()) {
			String[] values = metadata.getValues(name);
			sb.append(name).append(" : ");
			if (values.length == 1) {
				sb.append(values[0]);
			} else {
				sb.append(Arrays.toString(values));
			}
			sb.append(System.getProperty("line.separator"));
		}
		
		sb.append(text);
		
		return sb.toString();
	}
	
	public Tika getTika() {
		return tika;
	}

	public void setTika(Tika tika) {
		this.tika = tika;
	}
	
	
}
