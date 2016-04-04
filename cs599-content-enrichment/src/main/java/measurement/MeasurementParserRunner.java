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

import cbor.CborDocument;
import shared.AbstractParserRunner;

/**
 * Utility class to run MeasurementParser to all files in a specified based folder
 *
 */
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

	public boolean isExtractDumpData() {
		return measurementParser.isExtractDumpData();
	}

	public void setExtractDumpData(boolean extractDumpData) {
		measurementParser.setExtractDumpData(extractDumpData);
	}

	@Override
	protected File getResultFile(String relativePath) {
		return super.getResultFile(relativePath, ".measurement");
	}
	
	@Override
	protected boolean parse(Path path, String relativePath, File resultFile, CborDocument cborDoc) throws Exception {
		Metadata metadata = parsePath(path, cborDoc);
		
		if (metadata.get("measurement_extractedText") == null) {
			return false;
		}
		
		metadata.add("filePath", relativePath);
		
		String json = getGson().toJson(metadata);
		
		resultFile.getParentFile().mkdirs();
		try(PrintWriter out = new PrintWriter(resultFile)) {
			out.print(json);
		}
		
		return true;
	}
	
	private Metadata parsePath(Path path, CborDocument cborDoc) throws IOException, TikaException, SAXException {
		ContentHandler handler = new ToHTMLContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        
        try(InputStream stream = cborDoc == null ? new FileInputStream(path.toFile()) : cborDoc.getInputStream()){
        	measurementParser.parse(stream, handler, metadata, context);
        }
        
		return metadata;
	}

}
