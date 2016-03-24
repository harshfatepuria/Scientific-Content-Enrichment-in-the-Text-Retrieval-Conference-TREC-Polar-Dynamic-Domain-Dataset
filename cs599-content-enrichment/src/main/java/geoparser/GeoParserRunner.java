package geoparser;

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

public class GeoParserRunner extends AbstractParserRunner {
	private GeoWrapperParser geoParser;
	
	public GeoParserRunner(String baseFolder, String resultFolder) throws Exception {
		this(baseFolder, resultFolder, null);
	}
	
	public GeoParserRunner(String baseFolder, String resultFolder, String markerFile) throws Exception {
		setBaseFolder(baseFolder);
		setResultFolder(resultFolder);
		setMarkerFile(markerFile);
		initializeParser();
	}
	
	
	private void initializeParser() throws Exception {
		geoParser = new GeoWrapperParser();
	}
	
	
	@Override
	protected File getResultFile(Path path) {
		return super.getResultFile(path, ".geodata");
	}
	
	@Override
	protected boolean parse(Path path, File resultFile) throws Exception {
//		String relativePath = getRelativePath(path);
		
		Metadata metadata = parsePath(path);
			
		if (metadata.get("Geographic_NAME") == null) {
			return false;
		}
		
//		PathMetadata geoData = new PathMetadata(relativePath, metadata);
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
        	geoParser.parse(stream, handler, metadata, context);
        }
        
		return metadata;
	}
	
	/*
	public List<String> parse1() throws IOException {
		URI baseFolderUri = Paths.get(baseFolder).toUri();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<String> successPath = new ArrayList<>();
		ExecutorService executor = Executors.newWorkStealingPool(numberOfThread);
		
		Files.walk(Paths.get(baseFolder))
			.filter(Files::isRegularFile)
			.forEach(path -> {
				String relativePath = baseFolderUri.relativize(path.toUri()).toString();
				File jsonFile = new File(resultFolder, relativePath + ".geodata");
				
				if (jsonFile.exists()) {
					return;
				}
				
				Runnable task = () -> {
					try {
						Metadata metadata = parseFile(path.toFile());
						if (metadata.get("Geographic_NAME") == null) {
							return;
						}
						
						GeoData geoData = new GeoData(relativePath, metadata);
						
						String json = gson.toJson(geoData);
						
						jsonFile.getParentFile().mkdirs();
						try(PrintWriter out = new PrintWriter(jsonFile)) {
							out.print(json);
						}
						
						successPath.add(relativePath);
					} catch (Exception e) {
						System.out.println(path.toString());
					}
				};
				
				executor.submit(task);
			});
		
		try {
			System.out.println("attempt to shutdown executor");
			executor.shutdown();
			executor.awaitTermination(7, TimeUnit.DAYS);
		} catch (InterruptedException e) {
		    System.err.println("tasks interrupted");
		}
		finally {
		    if (!executor.isTerminated()) {
		        System.err.println("cancel non-finished tasks");
		    }
		    executor.shutdownNow();
		    System.out.println("shutdown finished");
		}
		
		return successPath;
	}
	*/
}
