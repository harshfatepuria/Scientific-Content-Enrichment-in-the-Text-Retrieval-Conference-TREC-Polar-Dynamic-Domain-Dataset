package main;

import TTR.TTRAnalysis;
public class Main {
	public static void main(String[] args) throws Exception {
		
		
		
		String s=TTRAnalysis.getRelevantText("/Users/harshfatepuria/Desktop/RESUME.doc");
		
		System.out.println("\n\nRELEVANT TEXT:\n\n");
		
		System.out.println(s);
		
	}
}
