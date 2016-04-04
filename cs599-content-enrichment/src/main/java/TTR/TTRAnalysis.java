package TTR;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.jsoup.Jsoup;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TTRAnalysis
{
	public static String parseBodyToHTML(String filePath) throws IOException, SAXException, TikaException {
		try (InputStream stream =new FileInputStream(new File(filePath))) {
			return parseBodyToHTML(stream);
		}
	}
	
	public static String parseBodyToHTML(InputStream stream) throws IOException, SAXException, TikaException {
//	    ContentHandler handler = new BodyContentHandler(new ToXMLContentHandler());
	    ContentHandler handler = new ToHTMLContentHandler();
		 
	    AutoDetectParser parser = new AutoDetectParser();
	    Metadata metadata = new Metadata();
	    
	    parser.parse(stream, handler, metadata);
	    return handler.toString();
	    
//	    catch(Exception e){System.out.println(e);}
//		return " ";
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
	
	public static String getRelevantText(String filePath) throws IOException, SAXException, TikaException {
		try (InputStream stream =new FileInputStream(new File(filePath))) {
			return getRelevantText(stream);
		}
	}
	
	public static String getRelevantText(InputStream stream) throws IOException, SAXException, TikaException
	{
		int i,j;
		float total=0,avg=0;
		float TTRArray[];
		StringBuilder builder = new StringBuilder();
		String s=parseBodyToHTML(stream);
		s=s.trim();
		
		String[] str_array = s.split("\n");
		
		
		for(i=0; i<str_array.length; i++)
		{
			if(str_array[i].contains("<body>"))
				break;
		}
		j=i;
		if(j<str_array.length)
		{
			TTRArray=new float[str_array.length-i];
			for(i=j; i<str_array.length; i++)
			{
				TTRArray[i-j]=getTTRatio(str_array[i]);
				total+=TTRArray[i-j];
			}
			avg=total/str_array.length;
			
			for(i=j; i<str_array.length; i++)
			{
				if(TTRArray[i-j]>avg)
				{
					builder.append(html2text(str_array[i]));
					builder.append("\n");
				}
			}	
			return builder.toString();
		}
		else
			return "";
		
	}
}