package TTR;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.jsoup.Jsoup;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TTRAnalysis
{
	public static String parseBodyToHTML(String filePath) throws IOException, SAXException, TikaException {
	    ContentHandler handler = new BodyContentHandler(
	            new ToXMLContentHandler());
	 
	    AutoDetectParser parser = new AutoDetectParser();
	    Metadata metadata = new Metadata();
	    
	    try (InputStream stream =new FileInputStream(filePath)) {
	        parser.parse(stream, handler, metadata);
	        return handler.toString();
	    }
	}
	
	public static String html2text(String html) {
	    return Jsoup.parse(html).text();
	}
	
	
	public static float getTTRatio(String line)
	{
		float ratio=0;
		int nonTagChars=html2text(line).length();
		int tags=0;
		
		ratio=nonTagChars;
		
		String REGEX = "<[^>]*>";
		Pattern p = Pattern.compile(REGEX);
	    Matcher m = p.matcher(line); 

	    while(m.find())
	         tags++;
		
		if(tags>0)
			ratio/=tags;
		return ratio;
	}
	
	public static String[] fillTTRArray(String filePath) throws IOException, SAXException, TikaException
	{
		int i;
		float total=0,avg=0;;
		String s=parseBodyToHTML(filePath);
		s=s.trim();
		
		String[] str_array = s.split("\n");
		
		//System.out.println(s);
		
		float TTRArray[]=new float[str_array.length];
		for(i=0; i<str_array.length; i++)
		{
			TTRArray[i]=getTTRatio(str_array[i]);
			total+=TTRArray[i];
		}
		avg=total/str_array.length;
		
		for(i=0; i<str_array.length; i++)
		{
			if(TTRArray[i]>avg)
				str_array[i]=html2text(str_array[i]);
			else
				str_array[i]=" ";
		}	
		return str_array;
	}
}