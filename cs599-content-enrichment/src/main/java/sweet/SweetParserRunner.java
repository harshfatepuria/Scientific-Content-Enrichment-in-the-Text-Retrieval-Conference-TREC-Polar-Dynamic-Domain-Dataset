package sweet;

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
 * Utility class to run SweetParser to all documents in a specified base folder
 *
 */
public class SweetParserRunner extends AbstractParserRunner {
	
	private SweetParser sweetParser;
	
	public SweetParserRunner(String baseFolder, String resultFolder) throws Exception {
		this(baseFolder, resultFolder, null);
	}
	
	public SweetParserRunner(String baseFolder, String resultFolder, String markerFile) throws Exception {
		setBaseFolder(baseFolder);
		setResultFolder(resultFolder);
		setMarkerFile(markerFile);
		initializeParser();
	}
	
	private void initializeParser() throws Exception {
		sweetParser = new SweetParser();
	}

	@Override
	protected File getResultFile(String relativePath) {
		return super.getResultFile(relativePath, ".sweet");
	}
	
	@Override
	protected boolean parse(Path path, String relativePath, File resultFile, CborDocument cborDoc) throws Exception {
		Metadata metadata = parsePath(path, cborDoc);
		
		if (metadata.get("sweet_concept") == null) {
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
        	sweetParser.parse(stream, handler, metadata, context);
        }
        
		return metadata;
	}
}
