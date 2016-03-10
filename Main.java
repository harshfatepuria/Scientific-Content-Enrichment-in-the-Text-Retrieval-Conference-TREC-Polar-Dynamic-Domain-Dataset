package main;

import TTR.TTRAnalysis;
public class Main {
	public static void main(String[] args) throws Exception {
		
		String s[]=TTRAnalysis.fillTTRArray("/Users/harshfatepuria/Desktop/INTERNSHIP_CV.pdf");
		
		/*
		 s[i] =" " if ith line is not relevant text in the file
		 */
		
		System.out.println("RELEVANT TEXT:");
		for(int i=0; i<s.length; i++)
		{
			if(s[i].equals(" ")==false)
				System.out.println(s[i]);
		}
		
		
	}
}
