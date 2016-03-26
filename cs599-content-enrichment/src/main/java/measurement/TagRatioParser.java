package measurement;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import TTR.TTRAnalysis;
import shared.TikaExtractedTextBasedParser;

public class TagRatioParser extends TikaExtractedTextBasedParser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1773024749639716739L;

	@Override
	public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context)
			throws IOException, SAXException, TikaException {
		String relevantText = TTRAnalysis.getRelevantText(stream);
		
		XHTMLContentHandler xhandler = new XHTMLContentHandler(handler, metadata);
		
		xhandler.startDocument();
		xhandler.characters(relevantText);
		xhandler.endDocument();
	}

}
