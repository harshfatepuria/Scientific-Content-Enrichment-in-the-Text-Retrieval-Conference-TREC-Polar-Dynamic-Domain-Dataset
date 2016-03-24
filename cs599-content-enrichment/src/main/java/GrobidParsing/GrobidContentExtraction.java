package GrobidParsing;

import java.io.FileNotFoundException;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.journal.GrobidRESTParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.json.simple.parser.ParseException;
import org.xml.sax.ContentHandler;

public class GrobidContentExtraction {
	

	public static String getData(String pdfPath){
		
		String tei = "";
		
//		JournalParser parser=new JournalParser();
//		parser.parse(new FileInputStream(new File("/Users/harshfatepuria/Desktop/WH_TIR08.pdf")), handler,metadata);
		
		ContentHandler handler = new ToXMLContentHandler();
	    Metadata metadata = new Metadata();
		GrobidRESTParser parser = new GrobidRESTParser();
		
		try {
			parser.parse(pdfPath, handler,metadata, new ParseContext());
			
			tei=metadata.get("grobid:header_TEIJSONSource");
		
		
//		
//		for(String name: metadata.names()) {
//			System.out.println(name + " =");
//			System.out.println(metadata.get(name));
//			System.out.println();
//		}
			return tei;
		} 
		catch(Exception e){
			return "";
		}
		
		
//		System.out.println(metadata.toString());
//		System.out.println(Arrays.toString(metadata.names()));
//		System.out.println(metadata.get("Creation-Date"));
	
	}
	
}