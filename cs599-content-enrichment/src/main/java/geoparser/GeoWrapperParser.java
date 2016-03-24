package geoparser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.geo.topic.GeoParser;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import shared.TikaExtractedTextBasedParser;

public class GeoWrapperParser extends TikaExtractedTextBasedParser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4050364333648695218L;

	private GeoParser geoParser;
	private String nerLocationModelPath = "org/apache/tika/parser/geo/topic/en-ner-location.bin";
	
	public GeoWrapperParser() throws Exception {
		initializeParser();
	}
	
	private void initializeParser() {
		geoParser = new GeoParser();
        URL modelUrl = this.getClass().getResource(nerLocationModelPath);
        geoParser.initialize(modelUrl);
	}
	
	@Override
	public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context)
			throws IOException, SAXException, TikaException {
		String text = getParsedText(stream);
		
		try(InputStream plainTextStream = IOUtils.toInputStream(text);){
        	geoParser.parse(plainTextStream, handler, metadata, context);
        }
		
		XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
        xhtml.startDocument();
        xhtml.endDocument();
	}

}
