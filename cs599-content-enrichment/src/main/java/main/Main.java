package main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.openrdf.rio.RDFFormat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import geoparser.GeoParserRunner;
import sweet.SweetParserRunner;

public class Main {
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
//			runSweet(args);
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
		String markerFolder = "C:\\cs599\\a2\\geo\\marker";

		GeoParserRunner geoParserRunner = new GeoParserRunner(baseFolder, resultFolder, markerFolder);
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
		String markerFolder = "C:\\cs599\\a2\\sweet\\marker";
		
		SweetParserRunner sweetParserRunner = new SweetParserRunner(baseFolder, resultFolder, markerFolder);
		List<String> successPath = sweetParserRunner.runParser();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		File jsonFile = new File(resultFolder, "success.json");
		try(PrintWriter out = new PrintWriter(jsonFile)) {
			out.print(gson.toJson(successPath));
		}
		System.out.println("No of files: " + successPath.size());
	}
	
}
