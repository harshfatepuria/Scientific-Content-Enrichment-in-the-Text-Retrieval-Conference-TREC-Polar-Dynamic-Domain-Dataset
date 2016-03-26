package measurement;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Path;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import shared.AbstractParserRunner;

public class MeasurementParserRunner extends AbstractParserRunner {

	private MeasurementParser measurementParser;
	
	public MeasurementParserRunner(String baseFolder, String resultFolder) throws Exception {
		this(baseFolder, resultFolder, null);
	}
	
	public MeasurementParserRunner(String baseFolder, String resultFolder, String markerFile) throws Exception {
		setBaseFolder(baseFolder);
		setResultFolder(resultFolder);
		setMarkerFile(markerFile);
		initializeParser();
	}
	
	private void initializeParser() throws Exception {
		measurementParser = new MeasurementParser();
	}

	@Override
	protected File getResultFile(Path path) {
		return super.getResultFile(path, ".measurement");
	}
	
	@Override
	protected boolean parse(Path path, File resultFile) throws Exception {
		String relativePath = getRelativePath(path);
		Metadata metadata = parsePath(path);
		
		if (metadata.get("measurement_extracted") == null) {
			return false;
		}
		
		metadata.add("filePath", relativePath);
		
		String json = getGson().toJson(metadata);
		File jsonFile = getResultFile(path);
		
		jsonFile.getParentFile().mkdirs();
		try(PrintWriter out = new PrintWriter(jsonFile)) {
			out.print(json);
		}
		
		return true;
	}
	
	private Metadata parsePath(Path path) throws IOException, TikaException, SAXException {
		ContentHandler handler = new ToHTMLContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        
        try(InputStream stream = new FileInputStream(path.toFile());){
        	measurementParser.parse(stream, handler, metadata, context);
        }
        
		return metadata;
	}

}
