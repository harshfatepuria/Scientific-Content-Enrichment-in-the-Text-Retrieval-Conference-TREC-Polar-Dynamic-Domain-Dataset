package main;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import geoparser.GeoParserRunner;
import sweet.SweetOntology;
import sweet.SweetOntology.MatchedConcept;

public class Main {
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			runSweet(args);
			System.out.println("Invalid arguments");
			return;
		}
		
		if (args[0].equalsIgnoreCase("geoparser")) {
			runGeoParser(args);
		}
	}
	
	private static void runSweet(String[] args) throws Exception {
		SweetOntology sweet = SweetOntology.getInstance();
		List<MatchedConcept> matched = sweet.query("Agriculture");
		
		for (MatchedConcept m : matched) {
			System.out.println(m.concept);
		}
	}
	
	private static void runGeoParser(String[] args) throws Exception {
		System.out.println("runGeoParser");
		String baseFolder = "C:\\cs599\\polar-fulldump";
		String resultFolder = "C:\\cs599\\a2\\geo\\result";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("Start " + sdf.format(Calendar.getInstance().getTime()));
		
		GeoParserRunner geoParserRunner = new GeoParserRunner(baseFolder, resultFolder, 2);
		List<String> successPath = geoParserRunner.parse();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		File jsonFile = new File(resultFolder, "success.json");
		try(PrintWriter out = new PrintWriter(jsonFile)) {
			out.print(gson.toJson(successPath));
		}
		System.out.println("No of files: " + successPath.size());
		System.out.println("Finish " + sdf.format(Calendar.getInstance().getTime()));
	}
}
