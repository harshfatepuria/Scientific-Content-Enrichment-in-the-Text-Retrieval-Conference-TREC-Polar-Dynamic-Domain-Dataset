package main;

import script.GeoParserRunner;

public class Main {
	public static void main(String[] args) throws Exception {
		String geoIndexPath = "C:\\cs599\\geo\\geoIndex";
		String baseFolder = "D:\\cs599\\a2\\ex-paper";
		String resultFolder = "D:\\cs599\\a2\\geo\\result";
		GeoParserRunner geoParserRunner = new GeoParserRunner(baseFolder, resultFolder);
		geoParserRunner.parse();
	}
}
