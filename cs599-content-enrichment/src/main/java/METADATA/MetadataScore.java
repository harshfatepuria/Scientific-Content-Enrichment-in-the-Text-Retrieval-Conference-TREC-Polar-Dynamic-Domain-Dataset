package METADATA;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class MetadataScore {

	
	public static void main(String[] args) throws IOException {
		
		String polarDumpPrefix="/Volumes/ETC/polar-fulldump/";
		int i,j,count=0;
		JSONObject responseDetailsJson = new JSONObject();
		responseDetailsJson.put("name", "metadataScore");
		JSONArray jsonArrayMainChildren = new JSONArray();
		try {
			
			 PrintWriter writer = new PrintWriter("/Users/harshfatepuria/Desktop/599/HW2/FINAL FILES/metadataScore.json", "UTF-8");
			
			 
			 FileReader reader = new FileReader("/Users/harshfatepuria/Documents/Github/Scientific-Content-Enrichment-in-the-Text-Retrieval-Conference-TREC-Polar-Dynamic-Domain-Dataset/fulldump-path-all-json/listOfFiles.json");
	         JSONParser jsonParser = new JSONParser();
	         JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

	         JSONArray lang= (JSONArray) jsonObject.get("files");
	         
	       
	         // take the elements of the json array
	         for(i=0; i<lang.size(); i++){
	        	 	String fileName=lang.get(i).toString();
	        	 	
	        	 	JSONObject individualFileData = new JSONObject();
	        	 	individualFileData.put("name", fileName);
	        	 	
	        	 	JSONArray jsonArrayInnerChildren = new JSONArray();
	            	
	                // read the json file
	                FileReader reader2 = new FileReader("/Users/harshfatepuria/Documents/Github/Scientific-Content-Enrichment-in-the-Text-Retrieval-Conference-TREC-Polar-Dynamic-Domain-Dataset/fulldump-path-all-json/"+fileName);
	                JSONParser jsonParser2 = new JSONParser();
	                JSONObject jsonObject2 = (JSONObject) jsonParser2.parse(reader2);
	                JSONArray lang2= (JSONArray) jsonObject2.get("files");
	               
	                // take the elements of the json array
	                for(j=0; j<20; j++){
	                	try{
	                		ToXMLContentHandler handler = new ToXMLContentHandler();
	                	    AutoDetectParser parser = new AutoDetectParser();
	                	    Metadata metadata = new Metadata();
	                	    InputStream stream =new FileInputStream(new File(polarDumpPrefix+(lang2.get(j).toString())));
	            	        parser.parse(stream, handler, metadata);
	            	        count=(int)metadata.size();
	            	        JSONObject metadataJSON = new JSONObject();
	            	        
	            	        String filePathComplete=lang2.get(j).toString();
	            	        
	            	        metadataJSON.put("name", filePathComplete.substring((1+(filePathComplete.lastIndexOf('/'))))+": "+count);
	            	        metadataJSON.put("size", count);
	            	        jsonArrayInnerChildren.add(metadataJSON);
	                	}
	                	catch(Exception e){
	                		continue;
	                	}	
	                }
	                individualFileData.put("children", jsonArrayInnerChildren);
	                jsonArrayMainChildren.add(individualFileData);
	         }
	         responseDetailsJson.put("children", jsonArrayMainChildren);
	         JSONObject jsonObjectFinal = responseDetailsJson;
	         writer.println(jsonObjectFinal);
	         writer.close(); 
		} 
		catch(Exception e){
    		e.getStackTrace();
    	}
	}
}