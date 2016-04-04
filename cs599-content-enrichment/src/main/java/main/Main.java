package main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import geoparser.GeoParserRunner;
import measurement.MeasurementParserRunner;
import shared.AbstractParserRunner;
import sweet.SweetParserRunner;
import tessaract.TesseractOCRParserRunner;

public class Main {
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("Invalid arguments");
			return;
		}
		
		CommandLine cmd = parseCommand(args);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("Start " + sdf.format(Calendar.getInstance().getTime()));

		String type = cmd.getOptionValue("t");
		
		if (type.equalsIgnoreCase("geo")) {
			runGeoParser(cmd);
		} 
		else if (type.equalsIgnoreCase("sweet")) {
			runSweet(cmd);
		} 
		else if (type.equalsIgnoreCase("measurement")) {
			runMeasurement(cmd);
		} 
		else if (type.equalsIgnoreCase("ocr")) {
			runOCR(cmd);
		}
		
		System.out.println("Finish " + sdf.format(Calendar.getInstance().getTime()));
	}
	
	private static CommandLine parseCommand(String[] args) throws ParseException {
		Options options = new Options();
		options.addOption("t", true, "metadata type (measurement|geo|sweet|ocr)");
		options.addOption("b", true, "base folder of documents");
		options.addOption("r", true, "folder to store result");
		options.addOption("m", true, "marker file");
		options.addOption("ow", false, "overwrite existing result files");
		options.addOption("cbor", false, "state that documents are in CBOR format");
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);
		
		return cmd;
	}
	
	private static void configuareRunner(AbstractParserRunner runner, CommandLine cmd) {
		if (cmd.hasOption("m")) {
			runner.setMarkerFile(cmd.getOptionValue("m"));
		}
		runner.setOverwriteResult(cmd.hasOption("ow"));
		runner.setDocumentsInCborFormat(cmd.hasOption("cbor"));
	}
	
	private static void runMeasurement(CommandLine args) throws Exception {
		System.out.println("run MeasurementParser");
//		String baseFolder = "C:\\cs599\\polar-fulldump";
//		String resultFolder = "C:\\cs599\\a2\\measurement\\result";
//		String markerFile = "C:\\cs599\\a2\\measurement\\marker.txt";
//		MeasurementParserRunner measurementParserRunner = new MeasurementParserRunner(baseFolder, resultFolder, markerFile);
		
		MeasurementParserRunner measurementParserRunner = new MeasurementParserRunner(args.getOptionValue("b"), args.getOptionValue("r"));
		configuareRunner(measurementParserRunner, args);
		
		List<String> successPath = measurementParserRunner.runParser();
		
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		File jsonFile = new File(resultFolder, "success.json");
//		try(PrintWriter out = new PrintWriter(jsonFile)) {
//			out.print(gson.toJson(successPath));
//		}
		System.out.println("No of files: " + successPath.size());
	}
	
	private static void runGeoParser(CommandLine args) throws Exception {
		System.out.println("run GeoParser");
//		String baseFolder = "C:\\cs599\\polar-fulldump";
//		String resultFolder = "C:\\cs599\\a2\\geo\\result";
//		String markerFile = "C:\\cs599\\a2\\geo\\marker.txt";
		
		GeoParserRunner geoParserRunner = new GeoParserRunner(args.getOptionValue("b"), args.getOptionValue("r"));
		configuareRunner(geoParserRunner, args);
		
		List<String> successPath = geoParserRunner.runParser();
		
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		File jsonFile = new File(resultFolder, "success.json");
//		try(PrintWriter out = new PrintWriter(jsonFile)) {
//			out.print(gson.toJson(successPath));
//		}
		System.out.println("No of files: " + successPath.size());
		
	}
	
	private static void runSweet(CommandLine args) throws Exception {
		System.out.println("run SweetParser");
//		String baseFolder = "C:\\cs599\\polar-fulldump";
//		String resultFolder = "C:\\cs599\\a2\\sweet\\result";
//		String markerFile = "C:\\cs599\\a2\\sweet\\marker.txt";
//		
//		SweetParserRunner sweetParserRunner = new SweetParserRunner(baseFolder, resultFolder, markerFile);
		
		SweetParserRunner sweetParserRunner = new SweetParserRunner(args.getOptionValue("b"), args.getOptionValue("r"));
		configuareRunner(sweetParserRunner, args);
		
		List<String> successPath = sweetParserRunner.runParser();
		
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		File jsonFile = new File(resultFolder, "success.json");
//		try(PrintWriter out = new PrintWriter(jsonFile)) {
//			out.print(gson.toJson(successPath));
//		}
		System.out.println("No of files: " + successPath.size());
	}
	
	private static void runOCR(CommandLine args) throws Exception {
		System.out.println("run TesseractOCRParser");
//		String baseFolder = "C:\\cs599\\polar-fulldump-img\\";
//		String resultFolder = "C:\\cs599\\a2\\tesseract\\result-img";
//		String markerFile = "C:\\cs599\\a2\\tesseract\\marker-img.txt";
//		TesseractOCRParserRunner runner = new TesseractOCRParserRunner(baseFolder, resultFolder, markerFile);
		
		TesseractOCRParserRunner runner = new TesseractOCRParserRunner(args.getOptionValue("b"), args.getOptionValue("r"));
		configuareRunner(runner, args);
		
		List<String> successPath = runner.runParser();
		
		System.out.println("No of files: " + successPath.size());
	}

}
