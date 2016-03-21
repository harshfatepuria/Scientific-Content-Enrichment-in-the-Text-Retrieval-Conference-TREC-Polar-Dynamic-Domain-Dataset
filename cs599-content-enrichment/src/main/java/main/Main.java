package main;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import script.GeoParserRunner;

public class Main {
	public static void main(String[] args) throws Exception {
//		String geoIndexPath = "C:\\cs599\\geo\\geoIndex";
//		String baseFolder = "D:\\cs599\\a2\\ex-paper";
		String baseFolder = "C:\\cs599\\polar-fulldump";
		String resultFolder = "C:\\cs599\\a2\\geo\\result";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("Start " + sdf.format(Calendar.getInstance().getTime()));
		
		GeoParserRunner geoParserRunner = new GeoParserRunner(baseFolder, resultFolder);
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
