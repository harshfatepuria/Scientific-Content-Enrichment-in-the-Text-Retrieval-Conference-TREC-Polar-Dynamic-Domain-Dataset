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
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import shared.AbstractParserRunner;
import shared.PathMetadata;

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
	protected File getResultFile(Path path) {
		return super.getResultFile(path, ".sweet");
	}
	
	@Override
	protected boolean parse(Path path, File resultFile) throws Exception {
		String relativePath = getRelativePath(path);
		Metadata metadata = parsePath(path);
		
		if (metadata.get("sweet_concept") == null) {
			return false;
		}
		
		PathMetadata sweetData = new PathMetadata(relativePath, metadata);
		String json = getGson().toJson(sweetData);
		File jsonFile = getResultFile(path);
		
		jsonFile.getParentFile().mkdirs();
		try(PrintWriter out = new PrintWriter(jsonFile)) {
			out.print(json);
		}
		
		return true;
	}
	
	private Metadata parsePath(Path path) throws IOException, TikaException, SAXException {
		ContentHandler handler = new ToXMLContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        
        try(InputStream stream = new FileInputStream(path.toFile());){
        	sweetParser.parse(stream, handler, metadata, context);
        }
        
		return metadata;
	}
}
