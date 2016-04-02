package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
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
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.jpeg.JpegParser;
import org.apache.tika.parser.ocr.TesseractOCRParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ToTextContentHandler;
import org.openrdf.query.BindingSet;
import org.openrdf.rio.RDFFormat;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import TTR.TTRAnalysis;
import edu.stanford.nlp.ie.NumberNormalizer;
import edu.stanford.nlp.ie.QuantifiableEntityNormalizer;
import geoparser.GeoParserRunner;
import measurement.MeasurementParser;
import measurement.MeasurementParserRunner;
import measurement.TagRatioParser;
import shared.FileMarker;
import shared.TikaExtractedTextBasedParser;
import sweet.SweetOntology;
import sweet.SweetParserRunner;
import tessaract.TesseractOCRParserRunner;

public class Main {
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
//			runMeasurement(args);
//			testMeasurement();
//			detectType();
//			fixDataInFolderMeasurement();
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
		else if (args[0].equalsIgnoreCase("measurement")) {
			runMeasurement(args);
		} 
		else if (args[0].equalsIgnoreCase("ocr")) {
			runOCR(args);
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
	
	private static void runMeasurement(String[] args) throws Exception {
		System.out.println("run MeasurementParser");
		String baseFolder = "C:\\cs599\\polar-fulldump";
		String resultFolder = "C:\\cs599\\a2\\measurement\\result";
		String markerFile = "C:\\cs599\\a2\\measurement\\marker.txt";
		
		MeasurementParserRunner measurementParserRunner = new MeasurementParserRunner(baseFolder, resultFolder, markerFile);
		List<String> successPath = measurementParserRunner.runParser();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		File jsonFile = new File(resultFolder, "success.json");
		try(PrintWriter out = new PrintWriter(jsonFile)) {
			out.print(gson.toJson(successPath));
		}
		System.out.println("No of files: " + successPath.size());
	}
	
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
	
	private static void runOCR(String[] args) throws Exception {
		System.out.println("run TesseractOCR");
		String baseFolder = "C:\\cs599\\polar-fulldump-img\\";
		String resultFolder = "C:\\cs599\\a2\\tesseract\\result-img";
		String markerFile = "C:\\cs599\\a2\\tesseract\\marker-img.txt";
		
		TesseractOCRParserRunner runner = new TesseractOCRParserRunner(baseFolder, resultFolder, markerFile);
		List<String> successPath = runner.runParser();
		
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
	
	/*
	private static void fixDataInFolderFilePath() throws IOException {
		String resultFolder = "C:\\cs599\\a2\\geo\\result";
		String suffix = ".geodata";
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		List<String> notFixed = new ArrayList<>();
		int[] count = new int[]{0};
		
		URI baseFolderUri = Paths.get(resultFolder).toUri();
		
		Files.walk(Paths.get(resultFolder)).filter(Files::isRegularFile).forEach(path -> {
			try {
				String relativePath = baseFolderUri.relativize(path.toUri()).toString();
				relativePath = relativePath.replaceAll(suffix, "");
				
				count[0]++;
				fixDataFilePath(gson, path.toFile(), relativePath);
			} catch (Exception e) {
				e.printStackTrace();
				notFixed.add(path.toString());
			}
		});
		
		System.out.println(count[0] + " fixed");
		notFixed.forEach(s -> System.out.println(s));
	}
	
	private static void fixDataFilePath(Gson gson, File f, String relativePath) throws JsonSyntaxException, JsonIOException, IOException {
		Metadata metadata = null;
		try (FileReader fr = new FileReader(f)) {
			metadata = gson.fromJson(fr, Metadata.class);
		}
		f.delete();
		
		metadata.add("filePath", relativePath);
		
		String json = gson.toJson(metadata);
		
		try(PrintWriter out = new PrintWriter(f)) {
			out.print(json);
		}
	}
	*/
	
	/*
	private static void fixDataInFolderMeasurement() throws IOException {
		String resultFolder = "C:\\cs599\\a2\\measurement\\result";
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		List<String> notFixed = new ArrayList<>();
		int[] count = new int[]{0};
		
		Files.walk(Paths.get(resultFolder)).filter(Files::isRegularFile).forEach(path -> {
			try {
				count[0]++;
				fixDataMeasurement(gson, path.toFile());
			} catch (Exception e) {
				e.printStackTrace();
				notFixed.add(path.toString());
			}
		});
		
		System.out.println(count[0] + " fixed");
		notFixed.forEach(s -> System.out.println(s));
	}
	
	private static void fixDataMeasurement(Gson gson, File f) throws JsonSyntaxException, JsonIOException, IOException {
		Metadata meta = null;
		try (FileReader fr = new FileReader(f)) {
			meta = gson.fromJson(fr, Metadata.class);
		}
		f.delete();
		
		String[] extractedTexts = meta.getValues("measurement_extracted");
		meta.set("measurement_extracted", null);
		
		for(String text : extractedTexts) {
			String[] sp = text.split(" ");
			BigDecimal value = (new BigDecimal(sp[0]));
			String unit = sp[1];
			
			meta.add("measurement_value", value.toString());
			meta.add("measurement_unit", unit);
			meta.add("measurement_extractedText", text);
		}
		
		String json = gson.toJson(meta);
		
		try(PrintWriter out = new PrintWriter(f)) {
			out.print(json);
		}
	}
	*/
	
	private static void testMeasurement() throws Exception {
//		SweetOntology sweet = SweetOntology.getInstance();
//		String query = "ComplexUnit";
//		String concept = sweet.queryFirst(query).get().concept;
//		System.out.println(concept);
		
//		String relaType = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
//		String relaSubclass = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
//		String conceptPrefix = "http://sweet.jpl.nasa.gov/2.3/reprSciUnits.owl#Prefix";
//		String conceptUnit = "http://sweet.jpl.nasa.gov/2.3/reprSciUnits.owl#Unit";
//		String conceptBaseUnit = "http://sweet.jpl.nasa.gov/2.3/reprSciUnits.owl#BaseUnit";
//		String conceptComplexUnit = "http://sweet.jpl.nasa.gov/2.3/reprSciUnits.owl#ComplexUnit";
//		String relaHasSymbol = "http://sweet.jpl.nasa.gov/2.3/relaSci.owl#hasSymbol";
		
//		List<BindingSet> result = sweet.queryTriples("http://sweet.jpl.nasa.gov/2.3/reprSciUnits.owl#meter");
//		List<BindingSet> result = sweet.queryNestedRelationalConcept(relaType, relaSubclass, conceptUnit);
//		List<BindingSet> result = sweet.queryConceptThatRecursivelyHasRelationWith(relaHasSymbol, "http://sweet.jpl.nasa.gov/2.3/reprSciUnits.owl#meter");
//		List<BindingSet> result = sweet.queryConceptThatHasRelation("http://sweet.jpl.nasa.gov/2.3/reprSciUnits.owl#pascalPerSecond", relaHasSymbol);
////		
//		for(BindingSet bs : result) {
//			System.out.println(bs.getValue("s").stringValue() + " - " + bs.getValue("r") + " - " + bs.getValue("o").stringValue());
//		}
//		System.out.println(result.size());
		
//		System.out.println(QuantifiableEntityNormalizer.normalizedNumberString("four", "main", null));
//		System.out.println(QuantifiableEntityNormalizer.normalizedNumberString("light", "of", null));
		
		System.out.println(NumberNormalizer.wordToNumber("four"));
//		System.out.println(NumberNormalizer.wordToNumber("our"));
//		System.out.println(NumberNormalizer.wordToNumber("dog"));
		System.out.println(NumberNormalizer.wordToNumber("twenty two"));
		System.out.println(NumberNormalizer.wordToNumber("twenty-two"));
		
		
		String path = "C:\\cs599\\polar-fulldump\\gov\\biometrics\\www\\58630A1D48D40194D638346C2BDA3FAAA84669812BEDE7168620C8AA4517E90F";
//		String text = TTRAnalysis.getRelevantText(path);
//		System.out.println(text);
//		
		Metadata metadata = new Metadata();
		BodyContentHandler handler = new BodyContentHandler();
//		TesseractOCRParser
//		TagRatioParser tagParser = new TagRatioParser();
//		try (InputStream stream = new FileInputStream(path)) {
//			tagParser.parse(stream, handler, metadata);
//		}
//		System.out.println(handler.toString());
//		System.out.println(text.length() == handler.toString().length());
		
//		MeasurementParser measurementParser = new MeasurementParser();
//		try (InputStream stream = new FileInputStream(path)) {
//			measurementParser.parse(stream, handler, metadata);
//		}
//		
//		System.out.println(metadata);
		
//		MeasurementParser parser = new MeasurementParser();
//		parser.parse(null, null, null);
	}
	
	private static void detectType() throws IOException {
		String base = "C:\\cs599\\polar-fulldump\\";
		String rela = "edu\\columbia\\ciesin\\sedac\\95D91390333A603BCF46908536BA81CAED4DA960CB3D004DFFF5A32F68085095";
		
		Tika tika = new Tika();
		File f = new File(base + rela);
		System.out.println(tika.detect(f));
//		 MediaType.image("png").toString(), MediaType.image("tiff").toString(), MediaType.image("x-ms-bmp").toString()
		System.out.println(MediaType.image("x-ms-bmp").toString());
	}
}
