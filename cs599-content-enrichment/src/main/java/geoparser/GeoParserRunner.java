package geoparser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.geo.topic.GeoParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GeoParserRunner {
	private GeoParser geoParser;
	private String nerLocationModelPath = "org/apache/tika/parser/geo/topic/en-ner-location.bin";
	
	private String baseFolder;
	private String resultFolder;
	private Integer numberOfThread;
	
	public GeoParserRunner(String baseFolder, String resultFolder) throws Exception {
		this(baseFolder, resultFolder, Runtime.getRuntime().availableProcessors());
	}
	
	public GeoParserRunner(String baseFolder, String resultFolder, Integer numberOfThread) throws Exception {
		this.baseFolder = baseFolder;
		this.resultFolder = resultFolder;
		this.numberOfThread = numberOfThread;
		
		initializeParser();
	}
	
	
	private void initializeParser() {
		geoParser = new GeoParser();
        URL modelUrl = this.getClass().getResource(nerLocationModelPath);
        geoParser.initialize(modelUrl);
	}
	
	public List<String> parse() throws IOException {
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
	
	public Metadata parseFile(File file) throws IOException, TikaException, SAXException {
		ContentHandler handler = new ToXMLContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        
        Tika tika = new Tika();
        String text = tika.parseToString(file);
        
        try(InputStream plainTextStream = IOUtils.toInputStream(text);){
        	geoParser.parse(plainTextStream, handler, metadata, context);
        }
        
		return metadata;
	}
}
