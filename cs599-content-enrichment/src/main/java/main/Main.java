package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToTextContentHandler;
import org.openrdf.rio.RDFFormat;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import geoparser.GeoParserRunner;
import shared.FileMarker;
import shared.PathMetadata;
import shared.TikaExtractedTextBasedParser;
import sweet.SweetParserRunner;

public class Main {
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
//			runSweet(args);
//			createMarkerFile();
//			runGeoParser(args);
//			fixMarkerFile();
//			testParser();
//			fixDataInFolder();
			System.out.println("Invalid arguments");
			return;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("Start " + sdf.format(Calendar.getInstance().getTime()));
		
		if (args[0].equalsIgnoreCase("geoparser")) {
			runGeoParser(args);
		} 
		else if (args[0].equalsIgnoreCase("sweet")) {
			runSweet(args);
		}
		
		System.out.println("Finish " + sdf.format(Calendar.getInstance().getTime()));
	}
	
	/*
	private static void test() throws IOException, TikaException, SAXException {
		Tika tika = new Tika();
//		File f = new File("D:\\Picture\\2015-08 USA First\\LA\\01\\P8010140.JPG");
		File fjpg = new File("D:\\cs599\\commoncrawl\\crawl\\awang-amd-1\\gov\\nasa\\gsfc\\gcmd\\e04ac27f869963ddbcf5851f7abd6068cc5dc892\\1416511267000.jpg");
		File fhtm = new File("D:\\cs599\\commoncrawl\\crawl\\572-team6-acadis\\aq\\biodiversity\\afg\\1c1453870dbf2ddb11fdbae9cfdc1a90631a43d6\\1423892250000.html");
		
		byte[] byteArray = Files.readAllBytes(fhtm.toPath());
		byte[] modifiedByteArray = new byte[byteArray.length - 5];
		System.arraycopy(byteArray, 4, modifiedByteArray, 0, modifiedByteArray.length);
		
		String t = new String(modifiedByteArray);
		System.out.println(t.charAt(3998));
		
		ByteArrayInputStream is = new ByteArrayInputStream(modifiedByteArray);
		
		
//		FileInputStream fis = new FileInputStream(fhtm);
		
//		fis.read(new byte[4], 0, 4);

//		System.out.println(tika.detect(fis));
//		System.out.println(tika.parseToString(fhtm));
		
//		CBORFactory factory = new CBORFactory();
//		ObjectMapper mapper = new ObjectMapper(factory);
//		mapper.read
		
		CBORObject cbor = CBORObject.ReadJSON(is);
		System.out.println(cbor.getType());
//		String fileString = cbor.getType()
//		System.out.println(fileString);
		
	}
	*/
	
	private static void runGeoParser(String[] args) throws Exception {
		System.out.println("run GeoParser");
		String baseFolder = "C:\\cs599\\polar-fulldump";
		String resultFolder = "C:\\cs599\\a2\\geo\\result";
		String markerFile = "C:\\cs599\\a2\\geo\\marker.txt";

		GeoParserRunner geoParserRunner = new GeoParserRunner(baseFolder, resultFolder, markerFile);
		List<String> successPath = geoParserRunner.runParser();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		File jsonFile = new File(resultFolder, "success.json");
		try(PrintWriter out = new PrintWriter(jsonFile)) {
			out.print(gson.toJson(successPath));
		}
		System.out.println("No of files: " + successPath.size());
		
	}
	
	private static void runSweet(String[] args) throws Exception {
		System.out.println("run SweetParser");
		String baseFolder = "C:\\cs599\\polar-fulldump";
		String resultFolder = "C:\\cs599\\a2\\sweet\\result";
		String markerFile = "C:\\cs599\\a2\\sweet\\marker.txt";
		
		SweetParserRunner sweetParserRunner = new SweetParserRunner(baseFolder, resultFolder, markerFile);
		List<String> successPath = sweetParserRunner.runParser();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		File jsonFile = new File(resultFolder, "success.json");
		try(PrintWriter out = new PrintWriter(jsonFile)) {
			out.print(gson.toJson(successPath));
		}
		System.out.println("No of files: " + successPath.size());
	}
	
	/*
	private static void createMarkerFile() throws FileNotFoundException, IOException {
		String markerFolder = "C:\\cs599\\a2\\sweet\\marker";
		String markerFile = "C:\\cs599\\a2\\sweet\\marker.txt";
		URI baseFolderUri = Paths.get(markerFolder).toUri();
		
		try(FileMarker marker = new FileMarker(new File(markerFile))) {
			Files.walk(Paths.get(markerFolder))
				.filter(Files::isRegularFile)
				.forEach(path -> {
					String relativePath = baseFolderUri.relativize(path.toUri()).toString();
					marker.mark(relativePath);
				});
		}
	}
	*/
	
	/*
	private static void fixMarkerFile() throws FileNotFoundException, IOException {
		String resultFolder = "C:\\cs599\\a2\\geo\\result";
		String markerFile = "C:\\cs599\\a2\\geo\\marker.txt";
		String suffix = ".geodata";
		URI baseFolderUri = Paths.get(resultFolder).toUri();
		
		try(FileMarker marker = new FileMarker(new File(markerFile))) {
			Files.walk(Paths.get(resultFolder))
				.filter(Files::isRegularFile)
				.forEach(path -> {
					String relativePath = baseFolderUri.relativize(path.toUri()).toString();
					relativePath = relativePath.substring(0, relativePath.lastIndexOf(suffix));
					marker.mark(relativePath);
				});
		}
	}
	*/
	
	/*
	private static void testParser() throws IOException, SAXException, TikaException {
		TikaExtractedTextBasedParser parser = new TikaExtractedTextBasedParser() {
			
			@Override
			public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context)
					throws IOException, SAXException, TikaException {
				System.out.println(getParsedText(stream));
			}
		};
		
		File f = new File("D:\\Picture\\2015-08 USA First\\LA\\01\\P8010140.JPG");
		File sf = new File("C:\\cs599\\polar-fulldump\\at\\ac\\fwf\\www\\C3F748DE8F46EB7104603E734F5A38DFB9CE0777063223C7D2BB7FD22049D21B");
		File bf = new File("C:\\cs599\\polar-fulldump\\edu\\colorado\\sidads\\2E749C4528B09021293F92BB123BCED24777DB57A8C60077C28233AE2EC303B5");
		FileInputStream fis = new FileInputStream(f);
		System.out.println("start");
		
		parser.parse(fis, null, new Metadata());
	}
	*/
	
	/*
	
	private static void fixDataInFolder() throws IOException {
		String resultFolder = "C:\\cs599\\a2\\sweet\\result";
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		List<String> notFixed = new ArrayList<>();
		int[] count = new int[]{0};
		
		Files.walk(Paths.get(resultFolder)).filter(Files::isRegularFile).forEach(path -> {
			try {
				count[0]++;
				fixData(gson, path.toFile());
			} catch (Exception e) {
				e.printStackTrace();
				notFixed.add(path.toString());
			}
		});
		
		System.out.println(count[0] + " fixed");
		notFixed.forEach(s -> System.out.println(s));
	}
	
	private static void fixData(Gson gson, File f) throws JsonSyntaxException, JsonIOException, IOException {
		PathMetadata pm = null;
		try (FileReader fr = new FileReader(f)) {
			pm = gson.fromJson(fr, PathMetadata.class);
		}
		f.delete();
		
		String json = gson.toJson(pm.getMetadata());
		
		try(PrintWriter out = new PrintWriter(f)) {
			out.print(json);
		}
	}
	*/
}
