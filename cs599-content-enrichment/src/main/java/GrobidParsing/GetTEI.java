package GrobidParsing;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;




public class GetTEI {
	public static void main(String args[]){
		
		String polarDumpPrefix="/Volumes/ETC/polar-fulldump/";
		int emptyAbstract=0,i;
		String tei="";
		
		try {
			PrintWriter writer = new PrintWriter("/Users/harshfatepuria/Desktop/599/HW2/FINAL FILES/grobid_output.json", "UTF-8");
			
			 writer.println("{\n");
			
            // read the json file
            FileReader reader = new FileReader("/Users/harshfatepuria/Desktop/599/HW2/FINAL FILES/application%2Fpdf.json");
 
        	
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);


            JSONArray lang= (JSONArray) jsonObject.get("files");
             
            // take the elements of the json array
            for(i=0; i<lang.size(); i++){
            
            	try{
                tei=GrobidContentExtraction.getData(polarDumpPrefix+(lang.get(i).toString()));
                
              if(tei!=null && tei.toString().equals("")==false)
              {
            	  JSONParser jsonParser2 = new JSONParser();
                  JSONObject jsonObject2 = (JSONObject) jsonParser2.parse(tei);
            	   //get abstract from TEI
   	           
            	  // [“TEI”][“teiHeader”][“profileDesc”][“abstract”]
   	            JSONObject jsonTei = (JSONObject) jsonObject2.get("TEI");
   	            JSONObject teiHeader = (JSONObject) jsonTei.get("teiHeader");
   	            JSONObject profileDesc = (JSONObject) teiHeader.get("profileDesc");
   	         JSONObject getAbstract = (JSONObject) profileDesc.get("abstract");
   	         //System.out.println(getAbstract.toString());
            	  if(getAbstract.toString().equals("")==false){
            		  //System.out.println("\""+lang.get(i).toString()+"\":");
            		  //System.out.println(tei+",\n");
            		  writer.println("\""+lang.get(i).toString()+"\":"+tei+",\n");
            	 }
            	  else
            		  emptyAbstract++;
              }
              else
            	  emptyAbstract++;
            	}
            	catch(Exception e){
            		continue;
            	}
               
            }
            
           
            
           
            System.out.println("\n\n\nNumber of papers with empty abstract: "+emptyAbstract);
            
           
    		writer.println("\n}");
    		writer.close();
            
		} 
        catch (FileNotFoundException ex) {
//            ex.printStackTrace();
        	;
        } 
        catch (IOException ex) {
           // ex.printStackTrace();
        	;
        } 
        catch (ParseException ex) {
            //ex.printStackTrace();
        	;
        } catch (NullPointerException ex) {
            //ex.printStackTrace();
        	;
        }
		catch(Exception e){
    		;
    	}
	}
	
	
	
	
	
	
	
	
	
	//private static final String filePath = "/Users/harshfatepuria/Desktop/ccc.json";
	     
//
//	    public static void getJSON(String s) {
//
//	 
//	        try {
//	            // read the json file
//	            FileReader reader = new FileReader("/Users/harshfatepuria/Desktop/599/HW2/FINAL FILES/application%2Fpdf.json");
//	 
//	        	
//	            JSONParser jsonParser = new JSONParser();
//	            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
//	
//
//	            JSONArray lang= (JSONArray) jsonObject.get("files");
//	             
//	            // take the elements of the json array
//	            for(int i=0; i<lang.size(); i++){
//	                System.out.println("The " + i + " element of the array: "+lang.get(i).toString());
//	            }
//	            
//	            // get abstract from TEI
//	           
//	           // [“TEI”][“teiHeader”][“profileDesc”][“abstract”]
////	            JSONObject tei = (JSONObject) jsonObject.get("TEI");
////	            JSONObject teiHeader = (JSONObject) tei.get("teiHeader");
////	            JSONObject profileDesc = (JSONObject) teiHeader.get("profileDesc");
////	            JSONObject getAbstract = (JSONObject) profileDesc.get("abstract");
//	           
//	           // System.out.println("Abstract: " + getAbstract.toString());
//
//	 
//	            
//	            
//	            
//	            
//	            
//
////	            // get a number from the JSON object
////
////	            long id =  (long) jsonObject.get("id");
////	            System.out.println("The id is: " + id);
////	 
////	            // get an array from the JSON object
////	            JSONArray lang= (JSONArray) jsonObject.get("languages");
//	             
////	            // take the elements of the json array
////	            for(int i=0; i<lang.size(); i++){
////	                System.out.println("The " + i + " element of the array: "+lang.get(i));
////	            }
//
////	            Iterator i = lang.iterator();
////	 
////	            // take each value from the json array separately
////	            while (i.hasNext()) {
////	                JSONObject innerObj = (JSONObject) i.next();
////	                System.out.println("language "+ innerObj.get("lang") +
////	                        " with level " + innerObj.get("knowledge"));
////	            }
//	            
////	            // handle a structure into the json object
////	                        JSONObject structure = (JSONObject) jsonObject.get("job");
////	        
////	                        System.out.println("Into job structure, name: " + structure.get("name"));
//	           
//	 
//	    }

}
